package com.scratchy.env.service.mapper;

import com.scratchy.env.domain.Environment;
import com.scratchy.env.domain.LogicalLocation;
import com.scratchy.env.service.dto.EnvironmentDTO;
import com.scratchy.env.service.dto.LogicalLocationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link LogicalLocation} and its DTO {@link LogicalLocationDTO}.
 */
@Mapper(componentModel = "spring")
public interface LogicalLocationMapper extends EntityMapper<LogicalLocationDTO, LogicalLocation> {
    @Mapping(target = "environment", source = "environment", qualifiedByName = "environmentId")
    LogicalLocationDTO toDto(LogicalLocation s);

    @Named("environmentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    EnvironmentDTO toDtoEnvironmentId(Environment environment);
}
