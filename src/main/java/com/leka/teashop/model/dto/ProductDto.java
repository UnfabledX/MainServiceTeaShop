package com.leka.teashop.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {

    @NotEmpty(message = "{notEmpty.name}")
    private String name;

    @NotEmpty(message = "{notEmpty.description}")
    private String description;

    @NotNull(message = "{notNull.price}")
    @DecimalMin(value = "0.0", inclusive = false, message = "{notNegative.price}")
    @Digits(integer = 4, fraction = 2, message = "{notADigit.price}")
    private BigDecimal price;

    @Override
    public String toString() {
        return "ProductDto{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                '}';
    }
}
