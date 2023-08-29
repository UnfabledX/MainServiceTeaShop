package com.leka.teashop.service.impl;

import com.leka.teashop.exception.NotFoundException;
import com.leka.teashop.mapper.AddressOfDeliveryMapper;
import com.leka.teashop.model.AddressOfDelivery;
import com.leka.teashop.model.User;
import com.leka.teashop.model.dto.AddressOfDeliveryDto;
import com.leka.teashop.repository.AddressOfDeliveryRepository;
import com.leka.teashop.repository.UserRepository;
import com.leka.teashop.service.AddressOdDeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AddressOdDeliveryServiceImpl implements AddressOdDeliveryService {

    private final AddressOfDeliveryRepository addressOfDeliveryRepository;
    private final UserRepository userRepository;
    private final AddressOfDeliveryMapper addressOfDeliveryMapper;

    @Override
    public void updateAddressDelivery(AddressOfDeliveryDto deliveryDto) {
        AddressOfDelivery addressOfDelivery = addressOfDeliveryRepository.findById(deliveryDto.getId())
                .orElseThrow(() -> new NotFoundException("Address of delivery is not found!"));
        addressOfDeliveryMapper.updateEntity(addressOfDelivery, deliveryDto);
        addressOfDeliveryRepository.save(addressOfDelivery);
    }

    @Override
    public AddressOfDeliveryDto findById(Long deliveryId) {
        return addressOfDeliveryRepository.findById(deliveryId)
                .map(addressOfDeliveryMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Address of delivery is not found!"));
    }

    @Override
    public void createAddressDelivery(AddressOfDeliveryDto deliveryDto, Long userId) {
        User currentUser = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("The user is not found!"));
        AddressOfDelivery address = addressOfDeliveryMapper.toEntity(deliveryDto);
        currentUser.setAddressOfDelivery(address);
        userRepository.save(currentUser);
    }
}
