package com.scratchy.env.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.scratchy.env.repository.NamespaceRepository;
import com.scratchy.env.service.NamespaceService;
import com.scratchy.env.service.dto.NamespaceDTO;
import com.scratchy.env.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.scratchy.env.domain.Namespace}.
 */
@RestController
@RequestMapping("/api")
public class NamespaceResource {

    private final Logger log = LoggerFactory.getLogger(NamespaceResource.class);

    private static final String ENTITY_NAME = "namespace";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final NamespaceService namespaceService;

    private final NamespaceRepository namespaceRepository;

    public NamespaceResource(NamespaceService namespaceService, NamespaceRepository namespaceRepository) {
        this.namespaceService = namespaceService;
        this.namespaceRepository = namespaceRepository;
    }

    /**
     * {@code POST  /namespaces} : Create a new namespace.
     *
     * @param namespaceDTO the namespaceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new namespaceDTO, or with status {@code 400 (Bad Request)} if the namespace has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/namespaces")
    public ResponseEntity<NamespaceDTO> createNamespace(@RequestBody NamespaceDTO namespaceDTO) throws URISyntaxException {
        log.debug("REST request to save Namespace : {}", namespaceDTO);
        if (namespaceDTO.getId() != null) {
            throw new BadRequestAlertException("A new namespace cannot already have an ID", ENTITY_NAME, "idexists");
        }
        NamespaceDTO result = namespaceService.save(namespaceDTO);
        return ResponseEntity
            .created(new URI("/api/namespaces/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /namespaces/:id} : Updates an existing namespace.
     *
     * @param id the id of the namespaceDTO to save.
     * @param namespaceDTO the namespaceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated namespaceDTO,
     * or with status {@code 400 (Bad Request)} if the namespaceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the namespaceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/namespaces/{id}")
    public ResponseEntity<NamespaceDTO> updateNamespace(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody NamespaceDTO namespaceDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Namespace : {}, {}", id, namespaceDTO);
        if (namespaceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, namespaceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!namespaceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        NamespaceDTO result = namespaceService.update(namespaceDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, namespaceDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /namespaces/:id} : Partial updates given fields of an existing namespace, field will ignore if it is null
     *
     * @param id the id of the namespaceDTO to save.
     * @param namespaceDTO the namespaceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated namespaceDTO,
     * or with status {@code 400 (Bad Request)} if the namespaceDTO is not valid,
     * or with status {@code 404 (Not Found)} if the namespaceDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the namespaceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/namespaces/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<NamespaceDTO> partialUpdateNamespace(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody NamespaceDTO namespaceDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Namespace partially : {}, {}", id, namespaceDTO);
        if (namespaceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, namespaceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!namespaceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<NamespaceDTO> result = namespaceService.partialUpdate(namespaceDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, namespaceDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /namespaces} : get all the namespaces.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of namespaces in body.
     */
    @GetMapping("/namespaces")
    public ResponseEntity<List<NamespaceDTO>> getAllNamespaces(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Namespaces");
        Page<NamespaceDTO> page = namespaceService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /namespaces/:id} : get the "id" namespace.
     *
     * @param id the id of the namespaceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the namespaceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/namespaces/{id}")
    public ResponseEntity<NamespaceDTO> getNamespace(@PathVariable Long id) {
        log.debug("REST request to get Namespace : {}", id);
        Optional<NamespaceDTO> namespaceDTO = namespaceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(namespaceDTO);
    }

    /**
     * {@code DELETE  /namespaces/:id} : delete the "id" namespace.
     *
     * @param id the id of the namespaceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/namespaces/{id}")
    public ResponseEntity<Void> deleteNamespace(@PathVariable Long id) {
        log.debug("REST request to delete Namespace : {}", id);
        namespaceService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/namespaces?query=:query} : search for the namespace corresponding
     * to the query.
     *
     * @param query the query of the namespace search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/namespaces")
    public ResponseEntity<List<NamespaceDTO>> searchNamespaces(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Namespaces for query {}", query);
        Page<NamespaceDTO> page = namespaceService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
