package com.leka.teashop.repository.predicate;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProductPredicatesBuilder {

    private final List<SearchCriteria> params = new ArrayList<>();

    public ProductPredicatesBuilder with(String key, String operation, Object value) {
        params.add(new SearchCriteria(key, operation, value));
        return this;
    }

    public BooleanExpression build() {
        BooleanExpression result = Expressions.asBoolean(true).isTrue();
        if (params.isEmpty()) {
            return result;
        }
        List<BooleanExpression> predicates = params.stream()
                .map(param -> new ProductPredicate(param).getPredicate())
                .filter(Objects::nonNull).toList();
        for (BooleanExpression predicate : predicates) {
            result = result.and(predicate);
        }
        return result;
    }
}
