package com.scratchy.env.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.scratchy.env.IntegrationTest;
import com.scratchy.env.domain.Namespace;
import com.scratchy.env.repository.NamespaceRepository;
import com.scratchy.env.repository.search.NamespaceSearchRepository;
import com.scratchy.env.service.dto.NamespaceDTO;
import com.scratchy.env.service.mapper.NamespaceMapper;
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
 * Integration tests for the {@link NamespaceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class NamespaceResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/namespaces";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/namespaces";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private NamespaceRepository namespaceRepository;

    @Autowired
    private NamespaceMapper namespaceMapper;

    @Autowired
    private NamespaceSearchRepository namespaceSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restNamespaceMockMvc;

    private Namespace namespace;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Namespace createEntity(EntityManager em) {
        Namespace namespace = new Namespace().name(DEFAULT_NAME);
        return namespace;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Namespace createUpdatedEntity(EntityManager em) {
        Namespace namespace = new Namespace().name(UPDATED_NAME);
        return namespace;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        namespaceSearchRepository.deleteAll();
        assertThat(namespaceSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        namespace = createEntity(em);
    }

    @Test
    @Transactional
    void createNamespace() throws Exception {
        int databaseSizeBeforeCreate = namespaceRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(namespaceSearchRepository.findAll());
        // Create the Namespace
        NamespaceDTO namespaceDTO = namespaceMapper.toDto(namespace);
        restNamespaceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(namespaceDTO)))
            .andExpect(status().isCreated());

        // Validate the Namespace in the database
        List<Namespace> namespaceList = namespaceRepository.findAll();
        assertThat(namespaceList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(namespaceSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Namespace testNamespace = namespaceList.get(namespaceList.size() - 1);
        assertThat(testNamespace.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void createNamespaceWithExistingId() throws Exception {
        // Create the Namespace with an existing ID
        namespace.setId(1L);
        NamespaceDTO namespaceDTO = namespaceMapper.toDto(namespace);

        int databaseSizeBeforeCreate = namespaceRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(namespaceSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restNamespaceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(namespaceDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Namespace in the database
        List<Namespace> namespaceList = namespaceRepository.findAll();
        assertThat(namespaceList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(namespaceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllNamespaces() throws Exception {
        // Initialize the database
        namespaceRepository.saveAndFlush(namespace);

        // Get all the namespaceList
        restNamespaceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(namespace.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getNamespace() throws Exception {
        // Initialize the database
        namespaceRepository.saveAndFlush(namespace);

        // Get the namespace
        restNamespaceMockMvc
            .perform(get(ENTITY_API_URL_ID, namespace.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(namespace.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getNonExistingNamespace() throws Exception {
        // Get the namespace
        restNamespaceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingNamespace() throws Exception {
        // Initialize the database
        namespaceRepository.saveAndFlush(namespace);

        int databaseSizeBeforeUpdate = namespaceRepository.findAll().size();
        namespaceSearchRepository.save(namespace);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(namespaceSearchRepository.findAll());

        // Update the namespace
        Namespace updatedNamespace = namespaceRepository.findById(namespace.getId()).get();
        // Disconnect from session so that the updates on updatedNamespace are not directly saved in db
        em.detach(updatedNamespace);
        updatedNamespace.name(UPDATED_NAME);
        NamespaceDTO namespaceDTO = namespaceMapper.toDto(updatedNamespace);

        restNamespaceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, namespaceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(namespaceDTO))
            )
            .andExpect(status().isOk());

        // Validate the Namespace in the database
        List<Namespace> namespaceList = namespaceRepository.findAll();
        assertThat(namespaceList).hasSize(databaseSizeBeforeUpdate);
        Namespace testNamespace = namespaceList.get(namespaceList.size() - 1);
        assertThat(testNamespace.getName()).isEqualTo(UPDATED_NAME);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(namespaceSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Namespace> namespaceSearchList = IterableUtils.toList(namespaceSearchRepository.findAll());
                Namespace testNamespaceSearch = namespaceSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testNamespaceSearch.getName()).isEqualTo(UPDATED_NAME);
            });
    }

    @Test
    @Transactional
    void putNonExistingNamespace() throws Exception {
        int databaseSizeBeforeUpdate = namespaceRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(namespaceSearchRepository.findAll());
        namespace.setId(count.incrementAndGet());

        // Create the Namespace
        NamespaceDTO namespaceDTO = namespaceMapper.toDto(namespace);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNamespaceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, namespaceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(namespaceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Namespace in the database
        List<Namespace> namespaceList = namespaceRepository.findAll();
        assertThat(namespaceList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(namespaceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchNamespace() throws Exception {
        int databaseSizeBeforeUpdate = namespaceRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(namespaceSearchRepository.findAll());
        namespace.setId(count.incrementAndGet());

        // Create the Namespace
        NamespaceDTO namespaceDTO = namespaceMapper.toDto(namespace);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNamespaceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(namespaceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Namespace in the database
        List<Namespace> namespaceList = namespaceRepository.findAll();
        assertThat(namespaceList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(namespaceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamNamespace() throws Exception {
        int databaseSizeBeforeUpdate = namespaceRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(namespaceSearchRepository.findAll());
        namespace.setId(count.incrementAndGet());

        // Create the Namespace
        NamespaceDTO namespaceDTO = namespaceMapper.toDto(namespace);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNamespaceMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(namespaceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Namespace in the database
        List<Namespace> namespaceList = namespaceRepository.findAll();
        assertThat(namespaceList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(namespaceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateNamespaceWithPatch() throws Exception {
        // Initialize the database
        namespaceRepository.saveAndFlush(namespace);

        int databaseSizeBeforeUpdate = namespaceRepository.findAll().size();

        // Update the namespace using partial update
        Namespace partialUpdatedNamespace = new Namespace();
        partialUpdatedNamespace.setId(namespace.getId());

        partialUpdatedNamespace.name(UPDATED_NAME);

        restNamespaceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNamespace.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNamespace))
            )
            .andExpect(status().isOk());

        // Validate the Namespace in the database
        List<Namespace> namespaceList = namespaceRepository.findAll();
        assertThat(namespaceList).hasSize(databaseSizeBeforeUpdate);
        Namespace testNamespace = namespaceList.get(namespaceList.size() - 1);
        assertThat(testNamespace.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void fullUpdateNamespaceWithPatch() throws Exception {
        // Initialize the database
        namespaceRepository.saveAndFlush(namespace);

        int databaseSizeBeforeUpdate = namespaceRepository.findAll().size();

        // Update the namespace using partial update
        Namespace partialUpdatedNamespace = new Namespace();
        partialUpdatedNamespace.setId(namespace.getId());

        partialUpdatedNamespace.name(UPDATED_NAME);

        restNamespaceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNamespace.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNamespace))
            )
            .andExpect(status().isOk());

        // Validate the Namespace in the database
        List<Namespace> namespaceList = namespaceRepository.findAll();
        assertThat(namespaceList).hasSize(databaseSizeBeforeUpdate);
        Namespace testNamespace = namespaceList.get(namespaceList.size() - 1);
        assertThat(testNamespace.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingNamespace() throws Exception {
        int databaseSizeBeforeUpdate = namespaceRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(namespaceSearchRepository.findAll());
        namespace.setId(count.incrementAndGet());

        // Create the Namespace
        NamespaceDTO namespaceDTO = namespaceMapper.toDto(namespace);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNamespaceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, namespaceDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(namespaceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Namespace in the database
        List<Namespace> namespaceList = namespaceRepository.findAll();
        assertThat(namespaceList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(namespaceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchNamespace() throws Exception {
        int databaseSizeBeforeUpdate = namespaceRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(namespaceSearchRepository.findAll());
        namespace.setId(count.incrementAndGet());

        // Create the Namespace
        NamespaceDTO namespaceDTO = namespaceMapper.toDto(namespace);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNamespaceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(namespaceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Namespace in the database
        List<Namespace> namespaceList = namespaceRepository.findAll();
        assertThat(namespaceList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(namespaceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamNamespace() throws Exception {
        int databaseSizeBeforeUpdate = namespaceRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(namespaceSearchRepository.findAll());
        namespace.setId(count.incrementAndGet());

        // Create the Namespace
        NamespaceDTO namespaceDTO = namespaceMapper.toDto(namespace);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNamespaceMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(namespaceDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Namespace in the database
        List<Namespace> namespaceList = namespaceRepository.findAll();
        assertThat(namespaceList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(namespaceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteNamespace() throws Exception {
        // Initialize the database
        namespaceRepository.saveAndFlush(namespace);
        namespaceRepository.save(namespace);
        namespaceSearchRepository.save(namespace);

        int databaseSizeBeforeDelete = namespaceRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(namespaceSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the namespace
        restNamespaceMockMvc
            .perform(delete(ENTITY_API_URL_ID, namespace.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Namespace> namespaceList = namespaceRepository.findAll();
        assertThat(namespaceList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(namespaceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchNamespace() throws Exception {
        // Initialize the database
        namespace = namespaceRepository.saveAndFlush(namespace);
        namespaceSearchRepository.save(namespace);

        // Search the namespace
        restNamespaceMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + namespace.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(namespace.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }
}
