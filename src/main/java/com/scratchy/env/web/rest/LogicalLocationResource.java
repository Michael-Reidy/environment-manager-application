package com.scratchy.env.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.scratchy.env.repository.LogicalLocationRepository;
import com.scratchy.env.service.LogicalLocationService;
import com.scratchy.env.service.dto.LogicalLocationDTO;
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
 * REST controller for managing {@link com.scratchy.env.domain.LogicalLocation}.
 */
@RestController
@RequestMapping("/api")
public class LogicalLocationResource {

    private final Logger log = LoggerFactory.getLogger(LogicalLocationResource.class);

    private static final String ENTITY_NAME = "logicalLocation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LogicalLocationService logicalLocationService;

    private final LogicalLocationRepository logicalLocationRepository;

    public LogicalLocationResource(LogicalLocationService logicalLocationService, LogicalLocationRepository logicalLocationRepository) {
        this.logicalLocationService = logicalLocationService;
        this.logicalLocationRepository = logicalLocationRepository;
    }

    /**
     * {@code POST  /logical-locations} : Create a new logicalLocation.
     *
     * @param logicalLocationDTO the logicalLocationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new logicalLocationDTO, or with status {@code 400 (Bad Request)} if the logicalLocation has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/logical-locations")
    public ResponseEntity<LogicalLocationDTO> createLogicalLocation(@RequestBody LogicalLocationDTO logicalLocationDTO)
        throws URISyntaxException {
        log.debug("REST request to save LogicalLocation : {}", logicalLocationDTO);
        if (logicalLocationDTO.getId() != null) {
            throw new BadRequestAlertException("A new logicalLocation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        LogicalLocationDTO result = logicalLocationService.save(logicalLocationDTO);
        return ResponseEntity
            .created(new URI("/api/logical-locations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /logical-locations/:id} : Updates an existing logicalLocation.
     *
     * @param id the id of the logicalLocationDTO to save.
     * @param logicalLocationDTO the logicalLocationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated logicalLocationDTO,
     * or with status {@code 400 (Bad Request)} if the logicalLocationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the logicalLocationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/logical-locations/{id}")
    public ResponseEntity<LogicalLocationDTO> updateLogicalLocation(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody LogicalLocationDTO logicalLocationDTO
    ) throws URISyntaxException {
        log.debug("REST request to update LogicalLocation : {}, {}", id, logicalLocationDTO);
        if (logicalLocationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, logicalLocationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!logicalLocationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        LogicalLocationDTO result = logicalLocationService.update(logicalLocationDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, logicalLocationDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /logical-locations/:id} : Partial updates given fields of an existing logicalLocation, field will ignore if it is null
     *
     * @param id the id of the logicalLocationDTO to save.
     * @param logicalLocationDTO the logicalLocationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated logicalLocationDTO,
     * or with status {@code 400 (Bad Request)} if the logicalLocationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the logicalLocationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the logicalLocationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/logical-locations/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LogicalLocationDTO> partialUpdateLogicalLocation(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody LogicalLocationDTO logicalLocationDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update LogicalLocation partially : {}, {}", id, logicalLocationDTO);
        if (logicalLocationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, logicalLocationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!logicalLocationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LogicalLocationDTO> result = logicalLocationService.partialUpdate(logicalLocationDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, logicalLocationDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /logical-locations} : get all the logicalLocations.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of logicalLocations in body.
     */
    @GetMapping("/logical-locations")
    public ResponseEntity<List<LogicalLocationDTO>> getAllLogicalLocations(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get a page of LogicalLocations");
        Page<LogicalLocationDTO> page = logicalLocationService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /logical-locations/:id} : get the "id" logicalLocation.
     *
     * @param id the id of the logicalLocationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the logicalLocationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/logical-locations/{id}")
    public ResponseEntity<LogicalLocationDTO> getLogicalLocation(@PathVariable Long id) {
        log.debug("REST request to get LogicalLocation : {}", id);
        Optional<LogicalLocationDTO> logicalLocationDTO = logicalLocationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(logicalLocationDTO);
    }

    /**
     * {@code DELETE  /logical-locations/:id} : delete the "id" logicalLocation.
     *
     * @param id the id of the logicalLocationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/logical-locations/{id}")
    public ResponseEntity<Void> deleteLogicalLocation(@PathVariable Long id) {
        log.debug("REST request to delete LogicalLocation : {}", id);
        logicalLocationService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/logical-locations?query=:query} : search for the logicalLocation corresponding
     * to the query.
     *
     * @param query the query of the logicalLocation search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/logical-locations")
    public ResponseEntity<List<LogicalLocationDTO>> searchLogicalLocations(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of LogicalLocations for query {}", query);
        Page<LogicalLocationDTO> page = logicalLocationService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
