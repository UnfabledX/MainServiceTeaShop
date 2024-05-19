package com.leka.teashop.model;

import io.hypersistence.utils.hibernate.type.search.PostgreSQLTSVectorType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ToString
@Builder
@Setter
@Getter
@Entity
@Table(name = "products")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(name = "price_ua", nullable = false)
    private BigDecimal priceUA;

    @Column(name = "price_eu", nullable = false)
    private BigDecimal priceEU;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProductStatus status = ProductStatus.PRESENT;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ProductType type;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Image> images = new ArrayList<>();

    @Type(PostgreSQLTSVectorType.class)
    @Column(name = "search_vector", columnDefinition = "tsvector", insertable = false)
    private String searchVector;

    public void addImage(Image image) {
        this.images.add(image);
    }
}
