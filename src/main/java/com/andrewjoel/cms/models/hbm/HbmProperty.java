package com.andrewjoel.cms.models.hbm;

import java.io.Serializable;

public class HbmProperty implements Serializable {
    private String name;

    private HbmPropertyType type;

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
}
