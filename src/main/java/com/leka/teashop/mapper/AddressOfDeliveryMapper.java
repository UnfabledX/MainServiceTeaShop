package com.leka.teashop.mapper;

import com.leka.teashop.model.AddressOfDelivery;
import com.leka.teashop.model.dto.AddressOfDeliveryDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface AddressOfDeliveryMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget AddressOfDelivery addressOfDelivery, AddressOfDeliveryDto deliveryDto);

    AddressOfDeliveryDto toDto(AddressOfDelivery addressOfDelivery);

    AddressOfDelivery toEntity(AddressOfDeliveryDto deliveryDto);
}
