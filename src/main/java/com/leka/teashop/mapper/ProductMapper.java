package com.leka.teashop.mapper;

import com.leka.teashop.model.Product;
import com.leka.teashop.model.dto.ProductDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toEntity(ProductDto dto);

    ProductDto toDto(Product product);
}
