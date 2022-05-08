package com.andrewjoel.cms.models.hbm;

import java.util.Arrays;

public enum HbmPropertyType {
    STRING("string"),
    INT("integer");

    final String value;

    HbmPropertyType(final String value) {
        this.value = value;
    }

    public static HbmPropertyType fromString(final String text) {
        return Arrays.stream(values()).filter(hbmPropertyType -> hbmPropertyType.getValue().equals(text))
                .findFirst()
                .orElse(STRING);
    }

    public String getValue() {
        return value;
    }
}
