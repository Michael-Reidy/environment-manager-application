package com.scratchy.env.service.mapper;

import com.scratchy.env.domain.Namespace;
import com.scratchy.env.domain.Setting;
import com.scratchy.env.service.dto.NamespaceDTO;
import com.scratchy.env.service.dto.SettingDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Setting} and its DTO {@link SettingDTO}.
 */
@Mapper(componentModel = "spring")
public interface SettingMapper extends EntityMapper<SettingDTO, Setting> {
    @Mapping(target = "namespace", source = "namespace", qualifiedByName = "namespaceId")
    SettingDTO toDto(Setting s);

    @Named("namespaceId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    NamespaceDTO toDtoNamespaceId(Namespace namespace);
}
