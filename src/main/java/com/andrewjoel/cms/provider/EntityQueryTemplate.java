package com.andrewjoel.cms.provider;

import com.andrewjoel.cms.models.hbm.HbmEntity;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class EntityQueryTemplate {
    @PersistenceContext
    private EntityManager entityManager;

    public Query prepareInsert(final HbmEntity model, final Map<String, Object> values) {
        final StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("INSERT INTO ").append(model.getModelName()).append(" ");

//        final Map<String, Object> filteredValues = model.getAttributes();
        // EntityUtils.excludePrimaryKey(model, values);
        final String queryFields = String.join(",", model.getAttributes().keySet());
        final String queryValues = model.getAttributes().keySet().stream().map(o -> ":" + o)
                .collect(Collectors.joining(","));

        queryBuilder.append("(").append(queryFields).append(") ")
                .append("VALUES (").append(queryValues).append(")");

        System.out.println(queryBuilder);

        final Query query = entityManager.createNativeQuery(queryBuilder.toString());
        model.getAttributes().keySet().forEach(key ->
                query.setParameter(key, values.get(key))
        );

        return query;
    }
}
