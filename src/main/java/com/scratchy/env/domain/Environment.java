package com.scratchy.env.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Environment.
 */
@Entity
@Table(name = "environment")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "environment")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Environment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    @JsonIgnoreProperties(value = { "inheritsFrom", "contains", "appliesTos" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private Environment inheritsFrom;

    @OneToMany(mappedBy = "environment")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "composedOfs", "contains", "environment", "namespace" }, allowSetters = true)
    private Set<Namespace> contains = new HashSet<>();

    @OneToMany(mappedBy = "environment")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "environment" }, allowSetters = true)
    private Set<LogicalLocation> appliesTos = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Environment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Environment name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getStartDate() {
        return this.startDate;
    }

    public Environment startDate(Instant startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return this.endDate;
    }

    public Environment endDate(Instant endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public Environment getInheritsFrom() {
        return this.inheritsFrom;
    }

    public void setInheritsFrom(Environment environment) {
        this.inheritsFrom = environment;
    }

    public Environment inheritsFrom(Environment environment) {
        this.setInheritsFrom(environment);
        return this;
    }

    public Set<Namespace> getContains() {
        return this.contains;
    }

    public void setContains(Set<Namespace> namespaces) {
        if (this.contains != null) {
            this.contains.forEach(i -> i.setEnvironment(null));
        }
        if (namespaces != null) {
            namespaces.forEach(i -> i.setEnvironment(this));
        }
        this.contains = namespaces;
    }

    public Environment contains(Set<Namespace> namespaces) {
        this.setContains(namespaces);
        return this;
    }

    public Environment addContains(Namespace namespace) {
        this.contains.add(namespace);
        namespace.setEnvironment(this);
        return this;
    }

    public Environment removeContains(Namespace namespace) {
        this.contains.remove(namespace);
        namespace.setEnvironment(null);
        return this;
    }

    public Set<LogicalLocation> getAppliesTos() {
        return this.appliesTos;
    }

    public void setAppliesTos(Set<LogicalLocation> logicalLocations) {
        if (this.appliesTos != null) {
            this.appliesTos.forEach(i -> i.setEnvironment(null));
        }
        if (logicalLocations != null) {
            logicalLocations.forEach(i -> i.setEnvironment(this));
        }
        this.appliesTos = logicalLocations;
    }

    public Environment appliesTos(Set<LogicalLocation> logicalLocations) {
        this.setAppliesTos(logicalLocations);
        return this;
    }

    public Environment addAppliesTo(LogicalLocation logicalLocation) {
        this.appliesTos.add(logicalLocation);
        logicalLocation.setEnvironment(this);
        return this;
    }

    public Environment removeAppliesTo(LogicalLocation logicalLocation) {
        this.appliesTos.remove(logicalLocation);
        logicalLocation.setEnvironment(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Environment)) {
            return false;
        }
        return id != null && id.equals(((Environment) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Environment{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            "}";
    }
}
