package com.scratchy.env.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Namespace.
 */
@Entity
@Table(name = "namespace")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "namespace")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Namespace implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "namespace")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "composedOfs", "contains", "environment", "namespace" }, allowSetters = true)
    private Set<Namespace> composedOfs = new HashSet<>();

    @OneToMany(mappedBy = "namespace")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "namespace" }, allowSetters = true)
    private Set<Setting> contains = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "inheritsFrom", "contains", "appliesTos" }, allowSetters = true)
    private Environment environment;

    @ManyToOne
    @JsonIgnoreProperties(value = { "composedOfs", "contains", "environment", "namespace" }, allowSetters = true)
    private Namespace namespace;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Namespace id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Namespace name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Namespace> getComposedOfs() {
        return this.composedOfs;
    }

    public void setComposedOfs(Set<Namespace> namespaces) {
        if (this.composedOfs != null) {
            this.composedOfs.forEach(i -> i.setNamespace(null));
        }
        if (namespaces != null) {
            namespaces.forEach(i -> i.setNamespace(this));
        }
        this.composedOfs = namespaces;
    }

    public Namespace composedOfs(Set<Namespace> namespaces) {
        this.setComposedOfs(namespaces);
        return this;
    }

    public Namespace addComposedOf(Namespace namespace) {
        this.composedOfs.add(namespace);
        namespace.setNamespace(this);
        return this;
    }

    public Namespace removeComposedOf(Namespace namespace) {
        this.composedOfs.remove(namespace);
        namespace.setNamespace(null);
        return this;
    }

    public Set<Setting> getContains() {
        return this.contains;
    }

    public void setContains(Set<Setting> settings) {
        if (this.contains != null) {
            this.contains.forEach(i -> i.setNamespace(null));
        }
        if (settings != null) {
            settings.forEach(i -> i.setNamespace(this));
        }
        this.contains = settings;
    }

    public Namespace contains(Set<Setting> settings) {
        this.setContains(settings);
        return this;
    }

    public Namespace addContains(Setting setting) {
        this.contains.add(setting);
        setting.setNamespace(this);
        return this;
    }

    public Namespace removeContains(Setting setting) {
        this.contains.remove(setting);
        setting.setNamespace(null);
        return this;
    }

    public Environment getEnvironment() {
        return this.environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Namespace environment(Environment environment) {
        this.setEnvironment(environment);
        return this;
    }

    public Namespace getNamespace() {
        return this.namespace;
    }

    public void setNamespace(Namespace namespace) {
        this.namespace = namespace;
    }

    public Namespace namespace(Namespace namespace) {
        this.setNamespace(namespace);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Namespace)) {
            return false;
        }
        return id != null && id.equals(((Namespace) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Namespace{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
