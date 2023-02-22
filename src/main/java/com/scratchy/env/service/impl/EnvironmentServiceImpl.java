package com.scratchy.env.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.scratchy.env.domain.Environment;
import com.scratchy.env.repository.EnvironmentRepository;
import com.scratchy.env.repository.search.EnvironmentSearchRepository;
import com.scratchy.env.service.EnvironmentService;
import com.scratchy.env.service.dto.EnvironmentDTO;
import com.scratchy.env.service.mapper.EnvironmentMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Environment}.
 */
@Service
@Transactional
public class EnvironmentServiceImpl implements EnvironmentService {

    private final Logger log = LoggerFactory.getLogger(EnvironmentServiceImpl.class);

    private final EnvironmentRepository environmentRepository;

    private final EnvironmentMapper environmentMapper;

    private final EnvironmentSearchRepository environmentSearchRepository;

    public EnvironmentServiceImpl(
        EnvironmentRepository environmentRepository,
        EnvironmentMapper environmentMapper,
        EnvironmentSearchRepository environmentSearchRepository
    ) {
        this.environmentRepository = environmentRepository;
        this.environmentMapper = environmentMapper;
        this.environmentSearchRepository = environmentSearchRepository;
    }

    @Override
    public EnvironmentDTO save(EnvironmentDTO environmentDTO) {
        log.debug("Request to save Environment : {}", environmentDTO);
        Environment environment = environmentMapper.toEntity(environmentDTO);
        environment = environmentRepository.save(environment);
        EnvironmentDTO result = environmentMapper.toDto(environment);
        environmentSearchRepository.index(environment);
        return result;
    }

    @Override
    public EnvironmentDTO update(EnvironmentDTO environmentDTO) {
        log.debug("Request to update Environment : {}", environmentDTO);
        Environment environment = environmentMapper.toEntity(environmentDTO);
        environment = environmentRepository.save(environment);
        EnvironmentDTO result = environmentMapper.toDto(environment);
        environmentSearchRepository.index(environment);
        return result;
    }

    @Override
    public Optional<EnvironmentDTO> partialUpdate(EnvironmentDTO environmentDTO) {
        log.debug("Request to partially update Environment : {}", environmentDTO);

        return environmentRepository
            .findById(environmentDTO.getId())
            .map(existingEnvironment -> {
                environmentMapper.partialUpdate(existingEnvironment, environmentDTO);

                return existingEnvironment;
            })
            .map(environmentRepository::save)
            .map(savedEnvironment -> {
                environmentSearchRepository.save(savedEnvironment);

                return savedEnvironment;
            })
            .map(environmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EnvironmentDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Environments");
        return environmentRepository.findAll(pageable).map(environmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EnvironmentDTO> findOne(Long id) {
        log.debug("Request to get Environment : {}", id);
        return environmentRepository.findById(id).map(environmentMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Environment : {}", id);
        environmentRepository.deleteById(id);
        environmentSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EnvironmentDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Environments for query {}", query);
        return environmentSearchRepository.search(query, pageable).map(environmentMapper::toDto);
    }
}
