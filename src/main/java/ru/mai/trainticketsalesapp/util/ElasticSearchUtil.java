package ru.mai.trainticketsalesapp.util;

import co.elastic.clients.elasticsearch._types.query_dsl.FuzzyQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import lombok.val;

import java.util.function.Supplier;

public class ElasticSearchUtil {
    public static Supplier<Query> createSupplierQuery(String approximateDepartureStation) {
        Supplier<Query> supplier = () -> Query.of(q -> q.fuzzy(createFuzzyQuery(approximateDepartureStation)));
        return supplier;
    }
    public static FuzzyQuery createFuzzyQuery(String approximateDepartureStation) {
        val fuzzyQuery = new FuzzyQuery.Builder();
        return fuzzyQuery.field("route.stations.name").value(approximateDepartureStation).build();
    }
}
