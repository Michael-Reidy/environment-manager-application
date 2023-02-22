package com.scratchy.env.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.scratchy.env.IntegrationTest;
import com.scratchy.env.domain.Environment;
import com.scratchy.env.repository.EnvironmentRepository;
import com.scratchy.env.repository.search.EnvironmentSearchRepository;
import com.scratchy.env.service.dto.EnvironmentDTO;
import com.scratchy.env.service.mapper.EnvironmentMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link EnvironmentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EnvironmentResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Instant DEFAULT_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/environments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/environments";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EnvironmentRepository environmentRepository;

    @Autowired
    private EnvironmentMapper environmentMapper;

    @Autowired
    private EnvironmentSearchRepository environmentSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEnvironmentMockMvc;

    private Environment environment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Environment createEntity(EntityManager em) {
        Environment environment = new Environment().name(DEFAULT_NAME).startDate(DEFAULT_START_DATE).endDate(DEFAULT_END_DATE);
        return environment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Environment createUpdatedEntity(EntityManager em) {
        Environment environment = new Environment().name(UPDATED_NAME).startDate(UPDATED_START_DATE).endDate(UPDATED_END_DATE);
        return environment;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        environmentSearchRepository.deleteAll();
        assertThat(environmentSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        environment = createEntity(em);
    }

    @Test
    @Transactional
    void createEnvironment() throws Exception {
        int databaseSizeBeforeCreate = environmentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(environmentSearchRepository.findAll());
        // Create the Environment
        EnvironmentDTO environmentDTO = environmentMapper.toDto(environment);
        restEnvironmentMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(environmentDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Environment in the database
        List<Environment> environmentList = environmentRepository.findAll();
        assertThat(environmentList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(environmentSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Environment testEnvironment = environmentList.get(environmentList.size() - 1);
        assertThat(testEnvironment.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testEnvironment.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testEnvironment.getEndDate()).isEqualTo(DEFAULT_END_DATE);
    }

    @Test
    @Transactional
    void createEnvironmentWithExistingId() throws Exception {
        // Create the Environment with an existing ID
        environment.setId(1L);
        EnvironmentDTO environmentDTO = environmentMapper.toDto(environment);

        int databaseSizeBeforeCreate = environmentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(environmentSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restEnvironmentMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(environmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Environment in the database
        List<Environment> environmentList = environmentRepository.findAll();
        assertThat(environmentList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(environmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllEnvironments() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        // Get all the environmentList
        restEnvironmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(environment.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())));
    }

    @Test
    @Transactional
    void getEnvironment() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        // Get the environment
        restEnvironmentMockMvc
            .perform(get(ENTITY_API_URL_ID, environment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(environment.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingEnvironment() throws Exception {
        // Get the environment
        restEnvironmentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEnvironment() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        int databaseSizeBeforeUpdate = environmentRepository.findAll().size();
        environmentSearchRepository.save(environment);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(environmentSearchRepository.findAll());

        // Update the environment
        Environment updatedEnvironment = environmentRepository.findById(environment.getId()).get();
        // Disconnect from session so that the updates on updatedEnvironment are not directly saved in db
        em.detach(updatedEnvironment);
        updatedEnvironment.name(UPDATED_NAME).startDate(UPDATED_START_DATE).endDate(UPDATED_END_DATE);
        EnvironmentDTO environmentDTO = environmentMapper.toDto(updatedEnvironment);

        restEnvironmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, environmentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(environmentDTO))
            )
            .andExpect(status().isOk());

        // Validate the Environment in the database
        List<Environment> environmentList = environmentRepository.findAll();
        assertThat(environmentList).hasSize(databaseSizeBeforeUpdate);
        Environment testEnvironment = environmentList.get(environmentList.size() - 1);
        assertThat(testEnvironment.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testEnvironment.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testEnvironment.getEndDate()).isEqualTo(UPDATED_END_DATE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(environmentSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Environment> environmentSearchList = IterableUtils.toList(environmentSearchRepository.findAll());
                Environment testEnvironmentSearch = environmentSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testEnvironmentSearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testEnvironmentSearch.getStartDate()).isEqualTo(UPDATED_START_DATE);
                assertThat(testEnvironmentSearch.getEndDate()).isEqualTo(UPDATED_END_DATE);
            });
    }

    @Test
    @Transactional
    void putNonExistingEnvironment() throws Exception {
        int databaseSizeBeforeUpdate = environmentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(environmentSearchRepository.findAll());
        environment.setId(count.incrementAndGet());

        // Create the Environment
        EnvironmentDTO environmentDTO = environmentMapper.toDto(environment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEnvironmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, environmentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(environmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Environment in the database
        List<Environment> environmentList = environmentRepository.findAll();
        assertThat(environmentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(environmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchEnvironment() throws Exception {
        int databaseSizeBeforeUpdate = environmentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(environmentSearchRepository.findAll());
        environment.setId(count.incrementAndGet());

        // Create the Environment
        EnvironmentDTO environmentDTO = environmentMapper.toDto(environment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnvironmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(environmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Environment in the database
        List<Environment> environmentList = environmentRepository.findAll();
        assertThat(environmentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(environmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEnvironment() throws Exception {
        int databaseSizeBeforeUpdate = environmentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(environmentSearchRepository.findAll());
        environment.setId(count.incrementAndGet());

        // Create the Environment
        EnvironmentDTO environmentDTO = environmentMapper.toDto(environment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnvironmentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(environmentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Environment in the database
        List<Environment> environmentList = environmentRepository.findAll();
        assertThat(environmentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(environmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateEnvironmentWithPatch() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        int databaseSizeBeforeUpdate = environmentRepository.findAll().size();

        // Update the environment using partial update
        Environment partialUpdatedEnvironment = new Environment();
        partialUpdatedEnvironment.setId(environment.getId());

        partialUpdatedEnvironment.name(UPDATED_NAME).startDate(UPDATED_START_DATE);

        restEnvironmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEnvironment.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEnvironment))
            )
            .andExpect(status().isOk());

        // Validate the Environment in the database
        List<Environment> environmentList = environmentRepository.findAll();
        assertThat(environmentList).hasSize(databaseSizeBeforeUpdate);
        Environment testEnvironment = environmentList.get(environmentList.size() - 1);
        assertThat(testEnvironment.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testEnvironment.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testEnvironment.getEndDate()).isEqualTo(DEFAULT_END_DATE);
    }

    @Test
    @Transactional
    void fullUpdateEnvironmentWithPatch() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        int databaseSizeBeforeUpdate = environmentRepository.findAll().size();

        // Update the environment using partial update
        Environment partialUpdatedEnvironment = new Environment();
        partialUpdatedEnvironment.setId(environment.getId());

        partialUpdatedEnvironment.name(UPDATED_NAME).startDate(UPDATED_START_DATE).endDate(UPDATED_END_DATE);

        restEnvironmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEnvironment.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEnvironment))
            )
            .andExpect(status().isOk());

        // Validate the Environment in the database
        List<Environment> environmentList = environmentRepository.findAll();
        assertThat(environmentList).hasSize(databaseSizeBeforeUpdate);
        Environment testEnvironment = environmentList.get(environmentList.size() - 1);
        assertThat(testEnvironment.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testEnvironment.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testEnvironment.getEndDate()).isEqualTo(UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingEnvironment() throws Exception {
        int databaseSizeBeforeUpdate = environmentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(environmentSearchRepository.findAll());
        environment.setId(count.incrementAndGet());

        // Create the Environment
        EnvironmentDTO environmentDTO = environmentMapper.toDto(environment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEnvironmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, environmentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(environmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Environment in the database
        List<Environment> environmentList = environmentRepository.findAll();
        assertThat(environmentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(environmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEnvironment() throws Exception {
        int databaseSizeBeforeUpdate = environmentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(environmentSearchRepository.findAll());
        environment.setId(count.incrementAndGet());

        // Create the Environment
        EnvironmentDTO environmentDTO = environmentMapper.toDto(environment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnvironmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(environmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Environment in the database
        List<Environment> environmentList = environmentRepository.findAll();
        assertThat(environmentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(environmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEnvironment() throws Exception {
        int databaseSizeBeforeUpdate = environmentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(environmentSearchRepository.findAll());
        environment.setId(count.incrementAndGet());

        // Create the Environment
        EnvironmentDTO environmentDTO = environmentMapper.toDto(environment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnvironmentMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(environmentDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Environment in the database
        List<Environment> environmentList = environmentRepository.findAll();
        assertThat(environmentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(environmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteEnvironment() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);
        environmentRepository.save(environment);
        environmentSearchRepository.save(environment);

        int databaseSizeBeforeDelete = environmentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(environmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the environment
        restEnvironmentMockMvc
            .perform(delete(ENTITY_API_URL_ID, environment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Environment> environmentList = environmentRepository.findAll();
        assertThat(environmentList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(environmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchEnvironment() throws Exception {
        // Initialize the database
        environment = environmentRepository.saveAndFlush(environment);
        environmentSearchRepository.save(environment);

        // Search the environment
        restEnvironmentMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + environment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(environment.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())));
    }
}
