package com.leka.teashop.service;

import com.leka.teashop.model.dto.AddressOfDeliveryDto;

public interface AddressOdDeliveryService {
    void updateAddressDelivery(AddressOfDeliveryDto deliveryDto);

    AddressOfDeliveryDto findById(Long deliveryId);

    void createAddressDelivery(AddressOfDeliveryDto deliveryDto, Long userId);
}
