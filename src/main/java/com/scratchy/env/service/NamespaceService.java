package com.scratchy.env.service;

import com.scratchy.env.service.dto.NamespaceDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.scratchy.env.domain.Namespace}.
 */
public interface NamespaceService {
    /**
     * Save a namespace.
     *
     * @param namespaceDTO the entity to save.
     * @return the persisted entity.
     */
    NamespaceDTO save(NamespaceDTO namespaceDTO);

    /**
     * Updates a namespace.
     *
     * @param namespaceDTO the entity to update.
     * @return the persisted entity.
     */
    NamespaceDTO update(NamespaceDTO namespaceDTO);

    /**
     * Partially updates a namespace.
     *
     * @param namespaceDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<NamespaceDTO> partialUpdate(NamespaceDTO namespaceDTO);

    /**
     * Get all the namespaces.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<NamespaceDTO> findAll(Pageable pageable);

    /**
     * Get the "id" namespace.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<NamespaceDTO> findOne(Long id);

    /**
     * Delete the "id" namespace.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the namespace corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<NamespaceDTO> search(String query, Pageable pageable);
}
