package com.leka.teashop.service.impl;

import com.leka.teashop.mapper.InfoMapper;
import com.leka.teashop.model.PaymentAndDeliveryInfo;
import com.leka.teashop.model.dto.PaymentAndDeliveryInfoDto;
import com.leka.teashop.repository.InfoRepository;
import com.leka.teashop.service.InfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InfoServiceImpl implements InfoService {

    private final InfoRepository infoRepository;
    private final InfoMapper infoMapper;
    private final long totalRecords = 50;

    @Override
    @Transactional(readOnly = true)
    public PaymentAndDeliveryInfoDto getLastContent() {
        Optional<PaymentAndDeliveryInfo> infoOptional = infoRepository.findByLastId();
        PaymentAndDeliveryInfo info = infoOptional.orElseGet(PaymentAndDeliveryInfo::new);
        return infoMapper.toDto(info);
    }

    @Override
    @Transactional
    public PaymentAndDeliveryInfoDto changeAndSavePaymentAndDeliveryContent(PaymentAndDeliveryInfoDto infoDto) {
        if (infoRepository.count() == totalRecords) {
            infoRepository.deleteTheOldestRecord();
        }
        PaymentAndDeliveryInfo entity = infoMapper.toEntity(infoDto);
        PaymentAndDeliveryInfo saved = infoRepository.save(entity);
        return infoMapper.toDto(saved);
    }
}
