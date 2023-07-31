package com.leka.teashop.mapper;

import com.leka.teashop.model.Product;
import com.leka.teashop.model.dto.ProductDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    Product toEntity(ProductDto dto);

    ProductDto toDto(Product product);
}
