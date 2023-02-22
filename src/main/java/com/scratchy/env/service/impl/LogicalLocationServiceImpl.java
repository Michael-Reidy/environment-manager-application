package com.scratchy.env.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.scratchy.env.domain.LogicalLocation;
import com.scratchy.env.repository.LogicalLocationRepository;
import com.scratchy.env.repository.search.LogicalLocationSearchRepository;
import com.scratchy.env.service.LogicalLocationService;
import com.scratchy.env.service.dto.LogicalLocationDTO;
import com.scratchy.env.service.mapper.LogicalLocationMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link LogicalLocation}.
 */
@Service
@Transactional
public class LogicalLocationServiceImpl implements LogicalLocationService {

    private final Logger log = LoggerFactory.getLogger(LogicalLocationServiceImpl.class);

    private final LogicalLocationRepository logicalLocationRepository;

    private final LogicalLocationMapper logicalLocationMapper;

    private final LogicalLocationSearchRepository logicalLocationSearchRepository;

    public LogicalLocationServiceImpl(
        LogicalLocationRepository logicalLocationRepository,
        LogicalLocationMapper logicalLocationMapper,
        LogicalLocationSearchRepository logicalLocationSearchRepository
    ) {
        this.logicalLocationRepository = logicalLocationRepository;
        this.logicalLocationMapper = logicalLocationMapper;
        this.logicalLocationSearchRepository = logicalLocationSearchRepository;
    }

    @Override
    public LogicalLocationDTO save(LogicalLocationDTO logicalLocationDTO) {
        log.debug("Request to save LogicalLocation : {}", logicalLocationDTO);
        LogicalLocation logicalLocation = logicalLocationMapper.toEntity(logicalLocationDTO);
        logicalLocation = logicalLocationRepository.save(logicalLocation);
        LogicalLocationDTO result = logicalLocationMapper.toDto(logicalLocation);
        logicalLocationSearchRepository.index(logicalLocation);
        return result;
    }

    @Override
    public LogicalLocationDTO update(LogicalLocationDTO logicalLocationDTO) {
        log.debug("Request to update LogicalLocation : {}", logicalLocationDTO);
        LogicalLocation logicalLocation = logicalLocationMapper.toEntity(logicalLocationDTO);
        logicalLocation = logicalLocationRepository.save(logicalLocation);
        LogicalLocationDTO result = logicalLocationMapper.toDto(logicalLocation);
        logicalLocationSearchRepository.index(logicalLocation);
        return result;
    }

    @Override
    public Optional<LogicalLocationDTO> partialUpdate(LogicalLocationDTO logicalLocationDTO) {
        log.debug("Request to partially update LogicalLocation : {}", logicalLocationDTO);

        return logicalLocationRepository
            .findById(logicalLocationDTO.getId())
            .map(existingLogicalLocation -> {
                logicalLocationMapper.partialUpdate(existingLogicalLocation, logicalLocationDTO);

                return existingLogicalLocation;
            })
            .map(logicalLocationRepository::save)
            .map(savedLogicalLocation -> {
                logicalLocationSearchRepository.save(savedLogicalLocation);

                return savedLogicalLocation;
            })
            .map(logicalLocationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LogicalLocationDTO> findAll(Pageable pageable) {
        log.debug("Request to get all LogicalLocations");
        return logicalLocationRepository.findAll(pageable).map(logicalLocationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LogicalLocationDTO> findOne(Long id) {
        log.debug("Request to get LogicalLocation : {}", id);
        return logicalLocationRepository.findById(id).map(logicalLocationMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete LogicalLocation : {}", id);
        logicalLocationRepository.deleteById(id);
        logicalLocationSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LogicalLocationDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of LogicalLocations for query {}", query);
        return logicalLocationSearchRepository.search(query, pageable).map(logicalLocationMapper::toDto);
    }
}
