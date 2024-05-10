package com.leka.teashop.repository.predicate;

import com.leka.teashop.model.ProductStatus;
import com.leka.teashop.model.ProductType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;

import java.util.List;
import java.util.stream.Stream;

public class PredicateUtils {

    private PredicateUtils() {
    }

    public static BooleanExpression getNumericBooleanExpression(PathBuilder<?> entityPath, SearchCriteria criteria) {
        String key = criteria.getKey();
        List<String> fieldKeys = Stream.of("priceEU", "priceUA")
                .filter(key::equalsIgnoreCase)
                .toList();
        if (fieldKeys.isEmpty()) return null;

        String fieldKey = fieldKeys.iterator().next();
        NumberPath<Long> path = entityPath.getNumber(fieldKey, Long.class);
        long value = Long.parseLong(criteria.getValue().toString());
        return switch (criteria.getOperation()) {
            case ":" -> path.eq(value);
            case ">" -> path.gt(value);
            case "<" -> path.lt(value);
            case "!" -> path.notIn(value);
            default -> null;
        };
    }

    public static BooleanExpression getEnumBooleanExpression(PathBuilder<?> entityPath, SearchCriteria criteria) {
        String valueUpperCase = criteria.getValue().toString().toUpperCase();
        String operation = criteria.getOperation();
        if (criteria.getKey().equalsIgnoreCase("status")) {
            EnumPath<ProductStatus> path = entityPath.getEnum("status", ProductStatus.class);
            if (operation.equalsIgnoreCase(":")) {
                return path.eq(ProductStatus.valueOf(valueUpperCase));
            } else if (operation.equalsIgnoreCase("!")) {
                return path.ne(ProductStatus.valueOf(valueUpperCase));
            }
        } else if (criteria.getKey().equalsIgnoreCase("type")) {
            EnumPath<ProductType> path = entityPath.getEnum("type", ProductType.class);
            if (operation.equalsIgnoreCase(":")) {
                return path.eq(ProductType.valueOf(valueUpperCase));
            } else if (operation.equalsIgnoreCase("!")) {
                return path.ne(ProductType.valueOf(valueUpperCase));
            }
        }
        return null;
    }

    public static BooleanExpression getStringBooleanExpression(PathBuilder<?> entityPath, SearchCriteria criteria) {
        String key = criteria.getKey();
        List<String> fieldKeys = Stream.of("name", "description")
                .filter(key::equalsIgnoreCase)
                .toList();
        if (fieldKeys.isEmpty()) return null;

        String fieldKey = fieldKeys.iterator().next();
        StringPath path = entityPath.getString(fieldKey);
        if (criteria.getOperation().equalsIgnoreCase(":")) {
            return path.contains(criteria.getValue().toString());
        }
        return null;
    }
}
