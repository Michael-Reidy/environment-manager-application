package com.scratchy.env.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.scratchy.env.domain.Environment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EnvironmentDTO implements Serializable {

    private Long id;

    private String name;

    private Instant startDate;

    private Instant endDate;

    private EnvironmentDTO inheritsFrom;

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

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public EnvironmentDTO getInheritsFrom() {
        return inheritsFrom;
    }

    public void setInheritsFrom(EnvironmentDTO inheritsFrom) {
        this.inheritsFrom = inheritsFrom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EnvironmentDTO)) {
            return false;
        }

        EnvironmentDTO environmentDTO = (EnvironmentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, environmentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EnvironmentDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", inheritsFrom=" + getInheritsFrom() +
            "}";
    }
}
