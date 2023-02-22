package com.scratchy.env.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.scratchy.env.domain.Namespace} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NamespaceDTO implements Serializable {

    private Long id;

    private String name;

    private EnvironmentDTO environment;

    private NamespaceDTO namespace;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EnvironmentDTO getEnvironment() {
        return environment;
    }

    public void setEnvironment(EnvironmentDTO environment) {
        this.environment = environment;
    }

    public NamespaceDTO getNamespace() {
        return namespace;
    }

    public void setNamespace(NamespaceDTO namespace) {
        this.namespace = namespace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NamespaceDTO)) {
            return false;
        }

        NamespaceDTO namespaceDTO = (NamespaceDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, namespaceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NamespaceDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", environment=" + getEnvironment() +
            ", namespace=" + getNamespace() +
            "}";
    }
}
