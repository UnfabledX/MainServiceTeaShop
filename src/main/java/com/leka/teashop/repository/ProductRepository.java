package com.leka.teashop.repository;

import com.leka.teashop.model.Product;
import com.leka.teashop.model.QProduct;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>,
        QuerydslPredicateExecutor<Product>, QuerydslBinderCustomizer<QProduct> {

    @Override
    default void customize(QuerydslBindings bindings, QProduct root) {
        bindings.bind(String.class)
                .first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);
        bindings.excluding(root.images);
    }

    @Query(value = "select * from main_service.products where products.search_vector @@ to_tsquery('english', ?1) " +
            "order by ts_rank(products.search_vector, to_tsquery('english', ?1)) desc",
            countQuery = "select count(*) from main_service.products where products.search_vector @@ to_tsquery('english', ?1)",
            nativeQuery = true)
    Page<Product> findAllProductsBySearch(String search, Pageable pageable);
}
