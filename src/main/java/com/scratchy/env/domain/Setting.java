package com.scratchy.env.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.scratchy.env.domain.enumeration.ExpressionType;
import com.scratchy.env.domain.enumeration.ValueType;
import java.io.Serializable;
import javax.persistence.*;

/**
 * A Setting.
 */
@Entity
@Table(name = "setting")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "setting")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Setting implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "value_type")
    private ValueType valueType;

    @Enumerated(EnumType.STRING)
    @Column(name = "expression_type")
    private ExpressionType expressionType;

    @Column(name = "value")
    private String value;

    @ManyToOne
    @JsonIgnoreProperties(value = { "composedOfs", "contains", "environment", "namespace" }, allowSetters = true)
    private Namespace namespace;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Setting id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Setting name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ValueType getValueType() {
        return this.valueType;
    }

    public Setting valueType(ValueType valueType) {
        this.setValueType(valueType);
        return this;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    public ExpressionType getExpressionType() {
        return this.expressionType;
    }

    public Setting expressionType(ExpressionType expressionType) {
        this.setExpressionType(expressionType);
        return this;
    }

    public void setExpressionType(ExpressionType expressionType) {
        this.expressionType = expressionType;
    }

    public String getValue() {
        return this.value;
    }

    public Setting value(String value) {
        this.setValue(value);
        return this;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Namespace getNamespace() {
        return this.namespace;
    }

    public void setNamespace(Namespace namespace) {
        this.namespace = namespace;
    }

    public Setting namespace(Namespace namespace) {
        this.setNamespace(namespace);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Setting)) {
            return false;
        }
        return id != null && id.equals(((Setting) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Setting{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", valueType='" + getValueType() + "'" +
            ", expressionType='" + getExpressionType() + "'" +
            ", value='" + getValue() + "'" +
            "}";
    }
}
