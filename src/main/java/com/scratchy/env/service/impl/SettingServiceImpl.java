package com.scratchy.env.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.scratchy.env.domain.Setting;
import com.scratchy.env.repository.SettingRepository;
import com.scratchy.env.repository.search.SettingSearchRepository;
import com.scratchy.env.service.SettingService;
import com.scratchy.env.service.dto.SettingDTO;
import com.scratchy.env.service.mapper.SettingMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Setting}.
 */
@Service
@Transactional
public class SettingServiceImpl implements SettingService {

    private final Logger log = LoggerFactory.getLogger(SettingServiceImpl.class);

    private final SettingRepository settingRepository;

    private final SettingMapper settingMapper;

    private final SettingSearchRepository settingSearchRepository;

    public SettingServiceImpl(
        SettingRepository settingRepository,
        SettingMapper settingMapper,
        SettingSearchRepository settingSearchRepository
    ) {
        this.settingRepository = settingRepository;
        this.settingMapper = settingMapper;
        this.settingSearchRepository = settingSearchRepository;
    }

    @Override
    public SettingDTO save(SettingDTO settingDTO) {
        log.debug("Request to save Setting : {}", settingDTO);
        Setting setting = settingMapper.toEntity(settingDTO);
        setting = settingRepository.save(setting);
        SettingDTO result = settingMapper.toDto(setting);
        settingSearchRepository.index(setting);
        return result;
    }

    @Override
    public SettingDTO update(SettingDTO settingDTO) {
        log.debug("Request to update Setting : {}", settingDTO);
        Setting setting = settingMapper.toEntity(settingDTO);
        setting = settingRepository.save(setting);
        SettingDTO result = settingMapper.toDto(setting);
        settingSearchRepository.index(setting);
        return result;
    }

    @Override
    public Optional<SettingDTO> partialUpdate(SettingDTO settingDTO) {
        log.debug("Request to partially update Setting : {}", settingDTO);

        return settingRepository
            .findById(settingDTO.getId())
            .map(existingSetting -> {
                settingMapper.partialUpdate(existingSetting, settingDTO);

                return existingSetting;
            })
            .map(settingRepository::save)
            .map(savedSetting -> {
                settingSearchRepository.save(savedSetting);

                return savedSetting;
            })
            .map(settingMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SettingDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Settings");
        return settingRepository.findAll(pageable).map(settingMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SettingDTO> findOne(Long id) {
        log.debug("Request to get Setting : {}", id);
        return settingRepository.findById(id).map(settingMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Setting : {}", id);
        settingRepository.deleteById(id);
        settingSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SettingDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Settings for query {}", query);
        return settingSearchRepository.search(query, pageable).map(settingMapper::toDto);
    }
}
