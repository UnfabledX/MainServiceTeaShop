package com.leka.teashop.mapper;

import com.leka.teashop.model.PaymentAndDeliveryInfo;
import com.leka.teashop.model.dto.PaymentAndDeliveryInfoDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InfoMapper {


    PaymentAndDeliveryInfoDto toDto(PaymentAndDeliveryInfo info);

    PaymentAndDeliveryInfo toEntity(PaymentAndDeliveryInfoDto infoDto);
}
