package com.andrewjoel.cms.models.hbm;

import java.io.Serializable;
import java.util.Map;
import java.util.stream.Collectors;

public class HbmEntity implements Serializable {
    private static final long serialVersionUID = 4191846739415586131L;

    private String className;
    private Map<String, HbmProperty> attributes;
    private String primaryKey;
    private String modelName;

    public String getModelName() {
        return modelName;
    }

    public void setModelName(final String modelName) {
        this.modelName = modelName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(final String className) {
        this.className = className;
    }

    public Map<String, HbmProperty> getAttributes() {
        return attributes;
    }

    public void setAttributes(final Map<String, HbmProperty> attributes) {
        this.attributes = attributes;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(final String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public HbmProperty getAttribute(final String name) {
        return this.attributes.get(name);
    }

    public Map<String, HbmProperty> getAttributesWithoutPrimaryKey() {
        return attributes.entrySet().stream().filter(x -> !x.getKey().equals(primaryKey))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
