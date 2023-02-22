package com.scratchy.env.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.scratchy.env.IntegrationTest;
import com.scratchy.env.domain.Setting;
import com.scratchy.env.domain.enumeration.ExpressionType;
import com.scratchy.env.domain.enumeration.ValueType;
import com.scratchy.env.repository.SettingRepository;
import com.scratchy.env.repository.search.SettingSearchRepository;
import com.scratchy.env.service.dto.SettingDTO;
import com.scratchy.env.service.mapper.SettingMapper;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link SettingResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SettingResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final ValueType DEFAULT_VALUE_TYPE = ValueType.BOOLEAN;
    private static final ValueType UPDATED_VALUE_TYPE = ValueType.STRING;

    private static final ExpressionType DEFAULT_EXPRESSION_TYPE = ExpressionType.SIMPLE;
    private static final ExpressionType UPDATED_EXPRESSION_TYPE = ExpressionType.COMPLEX;

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/settings";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/settings";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SettingRepository settingRepository;

    @Autowired
    private SettingMapper settingMapper;

    @Autowired
    private SettingSearchRepository settingSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSettingMockMvc;

    private Setting setting;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Setting createEntity(EntityManager em) {
        Setting setting = new Setting()
            .name(DEFAULT_NAME)
            .valueType(DEFAULT_VALUE_TYPE)
            .expressionType(DEFAULT_EXPRESSION_TYPE)
            .value(DEFAULT_VALUE);
        return setting;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Setting createUpdatedEntity(EntityManager em) {
        Setting setting = new Setting()
            .name(UPDATED_NAME)
            .valueType(UPDATED_VALUE_TYPE)
            .expressionType(UPDATED_EXPRESSION_TYPE)
            .value(UPDATED_VALUE);
        return setting;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        settingSearchRepository.deleteAll();
        assertThat(settingSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        setting = createEntity(em);
    }

    @Test
    @Transactional
    void createSetting() throws Exception {
        int databaseSizeBeforeCreate = settingRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(settingSearchRepository.findAll());
        // Create the Setting
        SettingDTO settingDTO = settingMapper.toDto(setting);
        restSettingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(settingDTO)))
            .andExpect(status().isCreated());

        // Validate the Setting in the database
        List<Setting> settingList = settingRepository.findAll();
        assertThat(settingList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(settingSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Setting testSetting = settingList.get(settingList.size() - 1);
        assertThat(testSetting.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSetting.getValueType()).isEqualTo(DEFAULT_VALUE_TYPE);
        assertThat(testSetting.getExpressionType()).isEqualTo(DEFAULT_EXPRESSION_TYPE);
        assertThat(testSetting.getValue()).isEqualTo(DEFAULT_VALUE);
    }

    @Test
    @Transactional
    void createSettingWithExistingId() throws Exception {
        // Create the Setting with an existing ID
        setting.setId(1L);
        SettingDTO settingDTO = settingMapper.toDto(setting);

        int databaseSizeBeforeCreate = settingRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(settingSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restSettingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(settingDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Setting in the database
        List<Setting> settingList = settingRepository.findAll();
        assertThat(settingList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(settingSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllSettings() throws Exception {
        // Initialize the database
        settingRepository.saveAndFlush(setting);

        // Get all the settingList
        restSettingMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(setting.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].valueType").value(hasItem(DEFAULT_VALUE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].expressionType").value(hasItem(DEFAULT_EXPRESSION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)));
    }

    @Test
    @Transactional
    void getSetting() throws Exception {
        // Initialize the database
        settingRepository.saveAndFlush(setting);

        // Get the setting
        restSettingMockMvc
            .perform(get(ENTITY_API_URL_ID, setting.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(setting.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.valueType").value(DEFAULT_VALUE_TYPE.toString()))
            .andExpect(jsonPath("$.expressionType").value(DEFAULT_EXPRESSION_TYPE.toString()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE));
    }

    @Test
    @Transactional
    void getNonExistingSetting() throws Exception {
        // Get the setting
        restSettingMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSetting() throws Exception {
        // Initialize the database
        settingRepository.saveAndFlush(setting);

        int databaseSizeBeforeUpdate = settingRepository.findAll().size();
        settingSearchRepository.save(setting);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(settingSearchRepository.findAll());

        // Update the setting
        Setting updatedSetting = settingRepository.findById(setting.getId()).get();
        // Disconnect from session so that the updates on updatedSetting are not directly saved in db
        em.detach(updatedSetting);
        updatedSetting.name(UPDATED_NAME).valueType(UPDATED_VALUE_TYPE).expressionType(UPDATED_EXPRESSION_TYPE).value(UPDATED_VALUE);
        SettingDTO settingDTO = settingMapper.toDto(updatedSetting);

        restSettingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, settingDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(settingDTO))
            )
            .andExpect(status().isOk());

        // Validate the Setting in the database
        List<Setting> settingList = settingRepository.findAll();
        assertThat(settingList).hasSize(databaseSizeBeforeUpdate);
        Setting testSetting = settingList.get(settingList.size() - 1);
        assertThat(testSetting.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSetting.getValueType()).isEqualTo(UPDATED_VALUE_TYPE);
        assertThat(testSetting.getExpressionType()).isEqualTo(UPDATED_EXPRESSION_TYPE);
        assertThat(testSetting.getValue()).isEqualTo(UPDATED_VALUE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(settingSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Setting> settingSearchList = IterableUtils.toList(settingSearchRepository.findAll());
                Setting testSettingSearch = settingSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testSettingSearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testSettingSearch.getValueType()).isEqualTo(UPDATED_VALUE_TYPE);
                assertThat(testSettingSearch.getExpressionType()).isEqualTo(UPDATED_EXPRESSION_TYPE);
                assertThat(testSettingSearch.getValue()).isEqualTo(UPDATED_VALUE);
            });
    }

    @Test
    @Transactional
    void putNonExistingSetting() throws Exception {
        int databaseSizeBeforeUpdate = settingRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(settingSearchRepository.findAll());
        setting.setId(count.incrementAndGet());

        // Create the Setting
        SettingDTO settingDTO = settingMapper.toDto(setting);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSettingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, settingDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(settingDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Setting in the database
        List<Setting> settingList = settingRepository.findAll();
        assertThat(settingList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(settingSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchSetting() throws Exception {
        int databaseSizeBeforeUpdate = settingRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(settingSearchRepository.findAll());
        setting.setId(count.incrementAndGet());

        // Create the Setting
        SettingDTO settingDTO = settingMapper.toDto(setting);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSettingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(settingDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Setting in the database
        List<Setting> settingList = settingRepository.findAll();
        assertThat(settingList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(settingSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSetting() throws Exception {
        int databaseSizeBeforeUpdate = settingRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(settingSearchRepository.findAll());
        setting.setId(count.incrementAndGet());

        // Create the Setting
        SettingDTO settingDTO = settingMapper.toDto(setting);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSettingMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(settingDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Setting in the database
        List<Setting> settingList = settingRepository.findAll();
        assertThat(settingList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(settingSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateSettingWithPatch() throws Exception {
        // Initialize the database
        settingRepository.saveAndFlush(setting);

        int databaseSizeBeforeUpdate = settingRepository.findAll().size();

        // Update the setting using partial update
        Setting partialUpdatedSetting = new Setting();
        partialUpdatedSetting.setId(setting.getId());

        restSettingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSetting.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSetting))
            )
            .andExpect(status().isOk());

        // Validate the Setting in the database
        List<Setting> settingList = settingRepository.findAll();
        assertThat(settingList).hasSize(databaseSizeBeforeUpdate);
        Setting testSetting = settingList.get(settingList.size() - 1);
        assertThat(testSetting.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSetting.getValueType()).isEqualTo(DEFAULT_VALUE_TYPE);
        assertThat(testSetting.getExpressionType()).isEqualTo(DEFAULT_EXPRESSION_TYPE);
        assertThat(testSetting.getValue()).isEqualTo(DEFAULT_VALUE);
    }

    @Test
    @Transactional
    void fullUpdateSettingWithPatch() throws Exception {
        // Initialize the database
        settingRepository.saveAndFlush(setting);

        int databaseSizeBeforeUpdate = settingRepository.findAll().size();

        // Update the setting using partial update
        Setting partialUpdatedSetting = new Setting();
        partialUpdatedSetting.setId(setting.getId());

        partialUpdatedSetting.name(UPDATED_NAME).valueType(UPDATED_VALUE_TYPE).expressionType(UPDATED_EXPRESSION_TYPE).value(UPDATED_VALUE);

        restSettingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSetting.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSetting))
            )
            .andExpect(status().isOk());

        // Validate the Setting in the database
        List<Setting> settingList = settingRepository.findAll();
        assertThat(settingList).hasSize(databaseSizeBeforeUpdate);
        Setting testSetting = settingList.get(settingList.size() - 1);
        assertThat(testSetting.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSetting.getValueType()).isEqualTo(UPDATED_VALUE_TYPE);
        assertThat(testSetting.getExpressionType()).isEqualTo(UPDATED_EXPRESSION_TYPE);
        assertThat(testSetting.getValue()).isEqualTo(UPDATED_VALUE);
    }

    @Test
    @Transactional
    void patchNonExistingSetting() throws Exception {
        int databaseSizeBeforeUpdate = settingRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(settingSearchRepository.findAll());
        setting.setId(count.incrementAndGet());

        // Create the Setting
        SettingDTO settingDTO = settingMapper.toDto(setting);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSettingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, settingDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(settingDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Setting in the database
        List<Setting> settingList = settingRepository.findAll();
        assertThat(settingList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(settingSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSetting() throws Exception {
        int databaseSizeBeforeUpdate = settingRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(settingSearchRepository.findAll());
        setting.setId(count.incrementAndGet());

        // Create the Setting
        SettingDTO settingDTO = settingMapper.toDto(setting);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSettingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(settingDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Setting in the database
        List<Setting> settingList = settingRepository.findAll();
        assertThat(settingList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(settingSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSetting() throws Exception {
        int databaseSizeBeforeUpdate = settingRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(settingSearchRepository.findAll());
        setting.setId(count.incrementAndGet());

        // Create the Setting
        SettingDTO settingDTO = settingMapper.toDto(setting);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSettingMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(settingDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Setting in the database
        List<Setting> settingList = settingRepository.findAll();
        assertThat(settingList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(settingSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteSetting() throws Exception {
        // Initialize the database
        settingRepository.saveAndFlush(setting);
        settingRepository.save(setting);
        settingSearchRepository.save(setting);

        int databaseSizeBeforeDelete = settingRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(settingSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the setting
        restSettingMockMvc
            .perform(delete(ENTITY_API_URL_ID, setting.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Setting> settingList = settingRepository.findAll();
        assertThat(settingList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(settingSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchSetting() throws Exception {
        // Initialize the database
        setting = settingRepository.saveAndFlush(setting);
        settingSearchRepository.save(setting);

        // Search the setting
        restSettingMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + setting.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(setting.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].valueType").value(hasItem(DEFAULT_VALUE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].expressionType").value(hasItem(DEFAULT_EXPRESSION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)));
    }
}
