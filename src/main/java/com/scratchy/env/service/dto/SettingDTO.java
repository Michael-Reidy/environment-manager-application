package com.scratchy.env.service.dto;

import com.scratchy.env.domain.enumeration.ExpressionType;
import com.scratchy.env.domain.enumeration.ValueType;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.scratchy.env.domain.Setting} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SettingDTO implements Serializable {

    private Long id;

    private String name;

    private ValueType valueType;

    private ExpressionType expressionType;

    private String value;

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

    public ValueType getValueType() {
        return valueType;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    public ExpressionType getExpressionType() {
        return expressionType;
    }

    public void setExpressionType(ExpressionType expressionType) {
        this.expressionType = expressionType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
        if (!(o instanceof SettingDTO)) {
            return false;
        }

        SettingDTO settingDTO = (SettingDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, settingDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SettingDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", valueType='" + getValueType() + "'" +
            ", expressionType='" + getExpressionType() + "'" +
            ", value='" + getValue() + "'" +
            ", namespace=" + getNamespace() +
            "}";
    }
}
