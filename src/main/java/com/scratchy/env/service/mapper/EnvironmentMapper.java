package com.scratchy.env.service.mapper;

import com.scratchy.env.domain.Environment;
import com.scratchy.env.service.dto.EnvironmentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Environment} and its DTO {@link EnvironmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface EnvironmentMapper extends EntityMapper<EnvironmentDTO, Environment> {
    @Mapping(target = "inheritsFrom", source = "inheritsFrom", qualifiedByName = "environmentId")
    EnvironmentDTO toDto(Environment s);

    @Named("environmentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    EnvironmentDTO toDtoEnvironmentId(Environment environment);
}
