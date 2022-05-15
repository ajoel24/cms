package com.andrewjoel.cms.utils;

public class QueryBuilder {
    private final StringBuilder query;

    private QueryBuilder() {
        query = new StringBuilder();
    }

    public static QueryBuilder newQuery() {
        return new QueryBuilder();
    }


}
