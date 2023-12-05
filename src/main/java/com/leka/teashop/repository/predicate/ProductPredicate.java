package com.leka.teashop.repository.predicate;

import com.leka.teashop.model.Product;
import com.leka.teashop.model.ProductStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import lombok.RequiredArgsConstructor;

import static com.leka.teashop.repository.predicate.PredicateUtils.getEnumBooleanExpression;
import static com.leka.teashop.repository.predicate.PredicateUtils.getNumericBooleanExpression;
import static com.leka.teashop.repository.predicate.PredicateUtils.getStringBooleanExpression;
import static org.apache.commons.lang.StringUtils.isNumeric;

@RequiredArgsConstructor
public class ProductPredicate {

    private final SearchCriteria criteria;

    /**
     * Given method is designated to create paths dynamically
     * for more abstract search usage.
     *
     * @return Boolean expressions which is used as Predicate
     * argument for repository methods.
     */
    public BooleanExpression getPredicate() {
        PathBuilder<Product> entityPath = new PathBuilder<>(Product.class, "product");
        String stringValue = criteria.getValue().toString();
        BooleanExpression expression;
        if (isNumeric(stringValue)) {
            expression = getNumericBooleanExpression(entityPath, criteria);
        } else if (isEnum(criteria.getKey(), stringValue)) {
            expression = getEnumBooleanExpression(entityPath, criteria);
        } else {
            expression = getStringBooleanExpression(entityPath, criteria);
        }
        return expression;
    }

    private boolean isEnum(String key, String value) {
        try {
            if (key.equalsIgnoreCase("status")) {
                ProductStatus.valueOf(value.toUpperCase());
            } else {
                return false;
            }
        } catch (IllegalArgumentException ex) {
            return false;
        }
        return true;
    }
}
