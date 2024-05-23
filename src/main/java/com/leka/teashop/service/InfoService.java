package com.leka.teashop.service;

import com.leka.teashop.model.dto.PaymentAndDeliveryInfoDto;

public interface InfoService {

    PaymentAndDeliveryInfoDto getLastContent();

    PaymentAndDeliveryInfoDto changeAndSavePaymentAndDeliveryContent(PaymentAndDeliveryInfoDto infoDto);
}
