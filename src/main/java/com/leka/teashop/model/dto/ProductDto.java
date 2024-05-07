package com.leka.teashop.model.dto;

import com.leka.teashop.model.Image;
import com.leka.teashop.model.ProductStatus;
import com.leka.teashop.model.ProductType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ToString
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {

    private Long id;

    @NotEmpty(message = "{notEmpty.name}")
    private String name;

    @NotEmpty(message = "{notEmpty.description}")
    private String description;

    @NotNull(message = "{notNull.price}")
    @DecimalMin(value = "0.0", inclusive = false, message = "{notNegative.price}")
    @Digits(integer = 4, fraction = 2, message = "{notADigit.price}")
    private BigDecimal priceUA;

    @NotNull(message = "{notNull.price}")
    @DecimalMin(value = "0.0", inclusive = false, message = "{notNegative.price}")
    @Digits(integer = 4, fraction = 2, message = "{notADigit.price}")
    private BigDecimal priceEU;

    private ProductStatus status;

    private ProductType type;

    private List<Image> images = new ArrayList<>();
}
