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

    @Query(value = "select * from main_service.products where " +
            "(?1 operator (main_service.<%) name and main_service.word_similarity(?1, name) > 0.55) or " +
            "(?1 operator (main_service.<%) description and main_service.word_similarity(?1, description) > 0.55) " +
            "order by main_service.word_similarity(?1, name) desc;",
            countQuery = "select count(*) from main_service.products where " +
                    "(?1 operator (main_service.<%) name and main_service.word_similarity(?1, name) > 0.55) or " +
                    "(?1 operator (main_service.<%) description and main_service.word_similarity(?1, description) > 0.55) ",
            nativeQuery = true)
    Page<Product> findAllProductsBySearch(String search, Pageable pageable);

    @Query(value = "select * from main_service.products order by " +
            "case type " +
            "when 'TEA' then 1 " +
            "when 'JAMS' then 2 " +
            "when 'MUSHROOMS' then 3 " +
            "when 'HERBS' then 4 " +
            "when 'OTHERS' then 5 " +
            "else 6 " +
            "end",
            countQuery = "select count(*) from main_service.products", nativeQuery = true)
    Page<Product> findAllProductsOrderedByType(Pageable pageable);
}
