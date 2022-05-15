package com.andrewjoel.cms.models.hbm;

import java.io.Serializable;

public class HbmProperty implements Serializable {
    private static final long serialVersionUID = 4692491695953506544L;

    private String name;
    private HbmPropertyType type;
    private boolean isNullable;

    public HbmProperty() {
        this.isNullable = true;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public HbmPropertyType getType() {
        return type;
    }

    public void setType(final HbmPropertyType type) {
        this.type = type;
    }

    public boolean isNullable() {
        return isNullable;
    }

    public void setNullable(final boolean isNullable) {
        this.isNullable = isNullable;
    }
}
