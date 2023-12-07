package com.leka.teashop.mapper;

import com.leka.teashop.model.Product;
import com.leka.teashop.model.ProductStatus;
import com.leka.teashop.model.dto.ProductDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    Product toEntity(ProductDto dto);

    @AfterMapping
    default void setProductStatus(@MappingTarget Product product, ProductDto dto) {
        if (dto.getId() == null) {
            product.setStatus(ProductStatus.PRESENT);
        } else {
            product.setStatus(dto.getStatus());
        }
    }

    ProductDto toDto(Product product);
}
