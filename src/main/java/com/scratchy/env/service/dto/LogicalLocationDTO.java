package com.scratchy.env.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.scratchy.env.domain.LogicalLocation} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LogicalLocationDTO implements Serializable {

    private Long id;

    private String name;

    private EnvironmentDTO environment;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LogicalLocationDTO)) {
            return false;
        }

        LogicalLocationDTO logicalLocationDTO = (LogicalLocationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, logicalLocationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LogicalLocationDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", environment=" + getEnvironment() +
            "}";
    }
}
