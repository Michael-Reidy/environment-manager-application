package com.scratchy.env.service.mapper;

import com.scratchy.env.domain.Environment;
import com.scratchy.env.domain.Namespace;
import com.scratchy.env.service.dto.EnvironmentDTO;
import com.scratchy.env.service.dto.NamespaceDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Namespace} and its DTO {@link NamespaceDTO}.
 */
@Mapper(componentModel = "spring")
public interface NamespaceMapper extends EntityMapper<NamespaceDTO, Namespace> {
    @Mapping(target = "environment", source = "environment", qualifiedByName = "environmentId")
    @Mapping(target = "namespace", source = "namespace", qualifiedByName = "namespaceId")
    NamespaceDTO toDto(Namespace s);

    @Named("namespaceId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    NamespaceDTO toDtoNamespaceId(Namespace namespace);

    @Named("environmentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    EnvironmentDTO toDtoEnvironmentId(Environment environment);
}
