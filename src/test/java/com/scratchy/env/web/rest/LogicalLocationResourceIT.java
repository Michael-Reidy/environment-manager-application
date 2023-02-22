package com.scratchy.env.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.scratchy.env.IntegrationTest;
import com.scratchy.env.domain.LogicalLocation;
import com.scratchy.env.repository.LogicalLocationRepository;
import com.scratchy.env.repository.search.LogicalLocationSearchRepository;
import com.scratchy.env.service.dto.LogicalLocationDTO;
import com.scratchy.env.service.mapper.LogicalLocationMapper;
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
 * Integration tests for the {@link LogicalLocationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class LogicalLocationResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/logical-locations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/logical-locations";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private LogicalLocationRepository logicalLocationRepository;

    @Autowired
    private LogicalLocationMapper logicalLocationMapper;

    @Autowired
    private LogicalLocationSearchRepository logicalLocationSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLogicalLocationMockMvc;

    private LogicalLocation logicalLocation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LogicalLocation createEntity(EntityManager em) {
        LogicalLocation logicalLocation = new LogicalLocation().name(DEFAULT_NAME);
        return logicalLocation;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LogicalLocation createUpdatedEntity(EntityManager em) {
        LogicalLocation logicalLocation = new LogicalLocation().name(UPDATED_NAME);
        return logicalLocation;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        logicalLocationSearchRepository.deleteAll();
        assertThat(logicalLocationSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        logicalLocation = createEntity(em);
    }

    @Test
    @Transactional
    void createLogicalLocation() throws Exception {
        int databaseSizeBeforeCreate = logicalLocationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(logicalLocationSearchRepository.findAll());
        // Create the LogicalLocation
        LogicalLocationDTO logicalLocationDTO = logicalLocationMapper.toDto(logicalLocation);
        restLogicalLocationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(logicalLocationDTO))
            )
            .andExpect(status().isCreated());

        // Validate the LogicalLocation in the database
        List<LogicalLocation> logicalLocationList = logicalLocationRepository.findAll();
        assertThat(logicalLocationList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(logicalLocationSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        LogicalLocation testLogicalLocation = logicalLocationList.get(logicalLocationList.size() - 1);
        assertThat(testLogicalLocation.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void createLogicalLocationWithExistingId() throws Exception {
        // Create the LogicalLocation with an existing ID
        logicalLocation.setId(1L);
        LogicalLocationDTO logicalLocationDTO = logicalLocationMapper.toDto(logicalLocation);

        int databaseSizeBeforeCreate = logicalLocationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(logicalLocationSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restLogicalLocationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(logicalLocationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LogicalLocation in the database
        List<LogicalLocation> logicalLocationList = logicalLocationRepository.findAll();
        assertThat(logicalLocationList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(logicalLocationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllLogicalLocations() throws Exception {
        // Initialize the database
        logicalLocationRepository.saveAndFlush(logicalLocation);

        // Get all the logicalLocationList
        restLogicalLocationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(logicalLocation.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getLogicalLocation() throws Exception {
        // Initialize the database
        logicalLocationRepository.saveAndFlush(logicalLocation);

        // Get the logicalLocation
        restLogicalLocationMockMvc
            .perform(get(ENTITY_API_URL_ID, logicalLocation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(logicalLocation.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getNonExistingLogicalLocation() throws Exception {
        // Get the logicalLocation
        restLogicalLocationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLogicalLocation() throws Exception {
        // Initialize the database
        logicalLocationRepository.saveAndFlush(logicalLocation);

        int databaseSizeBeforeUpdate = logicalLocationRepository.findAll().size();
        logicalLocationSearchRepository.save(logicalLocation);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(logicalLocationSearchRepository.findAll());

        // Update the logicalLocation
        LogicalLocation updatedLogicalLocation = logicalLocationRepository.findById(logicalLocation.getId()).get();
        // Disconnect from session so that the updates on updatedLogicalLocation are not directly saved in db
        em.detach(updatedLogicalLocation);
        updatedLogicalLocation.name(UPDATED_NAME);
        LogicalLocationDTO logicalLocationDTO = logicalLocationMapper.toDto(updatedLogicalLocation);

        restLogicalLocationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, logicalLocationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(logicalLocationDTO))
            )
            .andExpect(status().isOk());

        // Validate the LogicalLocation in the database
        List<LogicalLocation> logicalLocationList = logicalLocationRepository.findAll();
        assertThat(logicalLocationList).hasSize(databaseSizeBeforeUpdate);
        LogicalLocation testLogicalLocation = logicalLocationList.get(logicalLocationList.size() - 1);
        assertThat(testLogicalLocation.getName()).isEqualTo(UPDATED_NAME);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(logicalLocationSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<LogicalLocation> logicalLocationSearchList = IterableUtils.toList(logicalLocationSearchRepository.findAll());
                LogicalLocation testLogicalLocationSearch = logicalLocationSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testLogicalLocationSearch.getName()).isEqualTo(UPDATED_NAME);
            });
    }

    @Test
    @Transactional
    void putNonExistingLogicalLocation() throws Exception {
        int databaseSizeBeforeUpdate = logicalLocationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(logicalLocationSearchRepository.findAll());
        logicalLocation.setId(count.incrementAndGet());

        // Create the LogicalLocation
        LogicalLocationDTO logicalLocationDTO = logicalLocationMapper.toDto(logicalLocation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLogicalLocationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, logicalLocationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(logicalLocationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LogicalLocation in the database
        List<LogicalLocation> logicalLocationList = logicalLocationRepository.findAll();
        assertThat(logicalLocationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(logicalLocationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchLogicalLocation() throws Exception {
        int databaseSizeBeforeUpdate = logicalLocationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(logicalLocationSearchRepository.findAll());
        logicalLocation.setId(count.incrementAndGet());

        // Create the LogicalLocation
        LogicalLocationDTO logicalLocationDTO = logicalLocationMapper.toDto(logicalLocation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLogicalLocationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(logicalLocationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LogicalLocation in the database
        List<LogicalLocation> logicalLocationList = logicalLocationRepository.findAll();
        assertThat(logicalLocationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(logicalLocationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLogicalLocation() throws Exception {
        int databaseSizeBeforeUpdate = logicalLocationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(logicalLocationSearchRepository.findAll());
        logicalLocation.setId(count.incrementAndGet());

        // Create the LogicalLocation
        LogicalLocationDTO logicalLocationDTO = logicalLocationMapper.toDto(logicalLocation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLogicalLocationMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(logicalLocationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the LogicalLocation in the database
        List<LogicalLocation> logicalLocationList = logicalLocationRepository.findAll();
        assertThat(logicalLocationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(logicalLocationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateLogicalLocationWithPatch() throws Exception {
        // Initialize the database
        logicalLocationRepository.saveAndFlush(logicalLocation);

        int databaseSizeBeforeUpdate = logicalLocationRepository.findAll().size();

        // Update the logicalLocation using partial update
        LogicalLocation partialUpdatedLogicalLocation = new LogicalLocation();
        partialUpdatedLogicalLocation.setId(logicalLocation.getId());

        partialUpdatedLogicalLocation.name(UPDATED_NAME);

        restLogicalLocationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLogicalLocation.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLogicalLocation))
            )
            .andExpect(status().isOk());

        // Validate the LogicalLocation in the database
        List<LogicalLocation> logicalLocationList = logicalLocationRepository.findAll();
        assertThat(logicalLocationList).hasSize(databaseSizeBeforeUpdate);
        LogicalLocation testLogicalLocation = logicalLocationList.get(logicalLocationList.size() - 1);
        assertThat(testLogicalLocation.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void fullUpdateLogicalLocationWithPatch() throws Exception {
        // Initialize the database
        logicalLocationRepository.saveAndFlush(logicalLocation);

        int databaseSizeBeforeUpdate = logicalLocationRepository.findAll().size();

        // Update the logicalLocation using partial update
        LogicalLocation partialUpdatedLogicalLocation = new LogicalLocation();
        partialUpdatedLogicalLocation.setId(logicalLocation.getId());

        partialUpdatedLogicalLocation.name(UPDATED_NAME);

        restLogicalLocationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLogicalLocation.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLogicalLocation))
            )
            .andExpect(status().isOk());

        // Validate the LogicalLocation in the database
        List<LogicalLocation> logicalLocationList = logicalLocationRepository.findAll();
        assertThat(logicalLocationList).hasSize(databaseSizeBeforeUpdate);
        LogicalLocation testLogicalLocation = logicalLocationList.get(logicalLocationList.size() - 1);
        assertThat(testLogicalLocation.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingLogicalLocation() throws Exception {
        int databaseSizeBeforeUpdate = logicalLocationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(logicalLocationSearchRepository.findAll());
        logicalLocation.setId(count.incrementAndGet());

        // Create the LogicalLocation
        LogicalLocationDTO logicalLocationDTO = logicalLocationMapper.toDto(logicalLocation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLogicalLocationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, logicalLocationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(logicalLocationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LogicalLocation in the database
        List<LogicalLocation> logicalLocationList = logicalLocationRepository.findAll();
        assertThat(logicalLocationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(logicalLocationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLogicalLocation() throws Exception {
        int databaseSizeBeforeUpdate = logicalLocationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(logicalLocationSearchRepository.findAll());
        logicalLocation.setId(count.incrementAndGet());

        // Create the LogicalLocation
        LogicalLocationDTO logicalLocationDTO = logicalLocationMapper.toDto(logicalLocation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLogicalLocationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(logicalLocationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LogicalLocation in the database
        List<LogicalLocation> logicalLocationList = logicalLocationRepository.findAll();
        assertThat(logicalLocationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(logicalLocationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLogicalLocation() throws Exception {
        int databaseSizeBeforeUpdate = logicalLocationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(logicalLocationSearchRepository.findAll());
        logicalLocation.setId(count.incrementAndGet());

        // Create the LogicalLocation
        LogicalLocationDTO logicalLocationDTO = logicalLocationMapper.toDto(logicalLocation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLogicalLocationMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(logicalLocationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the LogicalLocation in the database
        List<LogicalLocation> logicalLocationList = logicalLocationRepository.findAll();
        assertThat(logicalLocationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(logicalLocationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteLogicalLocation() throws Exception {
        // Initialize the database
        logicalLocationRepository.saveAndFlush(logicalLocation);
        logicalLocationRepository.save(logicalLocation);
        logicalLocationSearchRepository.save(logicalLocation);

        int databaseSizeBeforeDelete = logicalLocationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(logicalLocationSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the logicalLocation
        restLogicalLocationMockMvc
            .perform(delete(ENTITY_API_URL_ID, logicalLocation.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<LogicalLocation> logicalLocationList = logicalLocationRepository.findAll();
        assertThat(logicalLocationList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(logicalLocationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchLogicalLocation() throws Exception {
        // Initialize the database
        logicalLocation = logicalLocationRepository.saveAndFlush(logicalLocation);
        logicalLocationSearchRepository.save(logicalLocation);

        // Search the logicalLocation
        restLogicalLocationMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + logicalLocation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(logicalLocation.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }
}
