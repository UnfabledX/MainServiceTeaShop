package com.leka.teashop.mapper;

import com.leka.teashop.model.Product;
import com.leka.teashop.model.dto.ProductDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "name", source = "name")
    Product toEntity(ProductDto dto);
}
