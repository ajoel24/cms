package com.andrewjoel.cms.utils;

import com.andrewjoel.cms.models.hbm.HbmEntity;
import com.andrewjoel.cms.models.hbm.HbmPropertyType;

import java.util.Map;
import java.util.stream.Collectors;

public final class EntityUtils {
    private EntityUtils() {
    }

    public static Map<String, Object> excludePrimaryKey(final HbmEntity model, final Map<String, Object> values) {
        return values.entrySet().stream().filter(entry -> !entry.getKey().equals(model.getPrimaryKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static String prepareQueryValues(final String operation, final HbmEntity model, final Map<String, Object> values) {
        final StringBuilder stringBuilder = new StringBuilder();

        values.forEach((key, value) -> {
            final HbmPropertyType type = model.getAttribute(key).getType();

            switch (type) {
                case STRING:
                    stringBuilder.append("'").append(value).append("'");
                    break;
                case INT:
                default:
                    stringBuilder.append(value);
            }

            stringBuilder.append(",");
        });

        return stringBuilder.substring(0, stringBuilder.lastIndexOf(",") - 1);
    }
}
