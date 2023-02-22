package com.scratchy.env.service;

import com.scratchy.env.service.dto.EnvironmentDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.scratchy.env.domain.Environment}.
 */
public interface EnvironmentService {
    /**
     * Save a environment.
     *
     * @param environmentDTO the entity to save.
     * @return the persisted entity.
     */
    EnvironmentDTO save(EnvironmentDTO environmentDTO);

    /**
     * Updates a environment.
     *
     * @param environmentDTO the entity to update.
     * @return the persisted entity.
     */
    EnvironmentDTO update(EnvironmentDTO environmentDTO);

    /**
     * Partially updates a environment.
     *
     * @param environmentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<EnvironmentDTO> partialUpdate(EnvironmentDTO environmentDTO);

    /**
     * Get all the environments.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<EnvironmentDTO> findAll(Pageable pageable);

    /**
     * Get the "id" environment.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<EnvironmentDTO> findOne(Long id);

    /**
     * Delete the "id" environment.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the environment corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<EnvironmentDTO> search(String query, Pageable pageable);
}
