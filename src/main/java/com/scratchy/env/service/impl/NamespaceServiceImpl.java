package com.scratchy.env.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.scratchy.env.domain.Namespace;
import com.scratchy.env.repository.NamespaceRepository;
import com.scratchy.env.repository.search.NamespaceSearchRepository;
import com.scratchy.env.service.NamespaceService;
import com.scratchy.env.service.dto.NamespaceDTO;
import com.scratchy.env.service.mapper.NamespaceMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Namespace}.
 */
@Service
@Transactional
public class NamespaceServiceImpl implements NamespaceService {

    private final Logger log = LoggerFactory.getLogger(NamespaceServiceImpl.class);

    private final NamespaceRepository namespaceRepository;

    private final NamespaceMapper namespaceMapper;

    private final NamespaceSearchRepository namespaceSearchRepository;

    public NamespaceServiceImpl(
        NamespaceRepository namespaceRepository,
        NamespaceMapper namespaceMapper,
        NamespaceSearchRepository namespaceSearchRepository
    ) {
        this.namespaceRepository = namespaceRepository;
        this.namespaceMapper = namespaceMapper;
        this.namespaceSearchRepository = namespaceSearchRepository;
    }

    @Override
    public NamespaceDTO save(NamespaceDTO namespaceDTO) {
        log.debug("Request to save Namespace : {}", namespaceDTO);
        Namespace namespace = namespaceMapper.toEntity(namespaceDTO);
        namespace = namespaceRepository.save(namespace);
        NamespaceDTO result = namespaceMapper.toDto(namespace);
        namespaceSearchRepository.index(namespace);
        return result;
    }

    @Override
    public NamespaceDTO update(NamespaceDTO namespaceDTO) {
        log.debug("Request to update Namespace : {}", namespaceDTO);
        Namespace namespace = namespaceMapper.toEntity(namespaceDTO);
        namespace = namespaceRepository.save(namespace);
        NamespaceDTO result = namespaceMapper.toDto(namespace);
        namespaceSearchRepository.index(namespace);
        return result;
    }

    @Override
    public Optional<NamespaceDTO> partialUpdate(NamespaceDTO namespaceDTO) {
        log.debug("Request to partially update Namespace : {}", namespaceDTO);

        return namespaceRepository
            .findById(namespaceDTO.getId())
            .map(existingNamespace -> {
                namespaceMapper.partialUpdate(existingNamespace, namespaceDTO);

                return existingNamespace;
            })
            .map(namespaceRepository::save)
            .map(savedNamespace -> {
                namespaceSearchRepository.save(savedNamespace);

                return savedNamespace;
            })
            .map(namespaceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NamespaceDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Namespaces");
        return namespaceRepository.findAll(pageable).map(namespaceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NamespaceDTO> findOne(Long id) {
        log.debug("Request to get Namespace : {}", id);
        return namespaceRepository.findById(id).map(namespaceMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Namespace : {}", id);
        namespaceRepository.deleteById(id);
        namespaceSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NamespaceDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Namespaces for query {}", query);
        return namespaceSearchRepository.search(query, pageable).map(namespaceMapper::toDto);
    }
}
