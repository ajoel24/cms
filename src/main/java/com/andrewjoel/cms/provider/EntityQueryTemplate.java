package com.andrewjoel.cms.provider;

import com.andrewjoel.cms.models.hbm.HbmEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class EntityQueryTemplate {
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityQueryTemplate.class);

    @PersistenceContext
    private EntityManager entityManager;

    public Query prepareInsert(final HbmEntity model, final Map<String, Object> values) {
        final StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("INSERT INTO ").append(model.getModelName()).append(" ");

        final String queryFields = String.join(",", model.getAttributesWithoutPrimaryKey().keySet());
        final String queryValues = model.getAttributesWithoutPrimaryKey().keySet().stream().map(field -> ":" + field)
                .collect(Collectors.joining(","));

        queryBuilder.append("(").append(queryFields).append(") ")
                .append("VALUES (").append(queryValues).append(")");

        LOGGER.debug("Generated insert query: {}", queryBuilder);

        final Query query = entityManager.createNativeQuery(queryBuilder.toString());
        model.getAttributesWithoutPrimaryKey().keySet().forEach(key ->
                query.setParameter(key, values.get(key))
        );

        return query;
    }
}
