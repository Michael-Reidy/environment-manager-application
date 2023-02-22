package com.scratchy.env.service;

import com.scratchy.env.service.dto.LogicalLocationDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.scratchy.env.domain.LogicalLocation}.
 */
public interface LogicalLocationService {
    /**
     * Save a logicalLocation.
     *
     * @param logicalLocationDTO the entity to save.
     * @return the persisted entity.
     */
    LogicalLocationDTO save(LogicalLocationDTO logicalLocationDTO);

    /**
     * Updates a logicalLocation.
     *
     * @param logicalLocationDTO the entity to update.
     * @return the persisted entity.
     */
    LogicalLocationDTO update(LogicalLocationDTO logicalLocationDTO);

    /**
     * Partially updates a logicalLocation.
     *
     * @param logicalLocationDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<LogicalLocationDTO> partialUpdate(LogicalLocationDTO logicalLocationDTO);

    /**
     * Get all the logicalLocations.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<LogicalLocationDTO> findAll(Pageable pageable);

    /**
     * Get the "id" logicalLocation.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<LogicalLocationDTO> findOne(Long id);

    /**
     * Delete the "id" logicalLocation.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the logicalLocation corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<LogicalLocationDTO> search(String query, Pageable pageable);
}
