package com.leka.teashop.mapper;

import com.leka.teashop.model.AddressOfDelivery;
import com.leka.teashop.model.User;
import com.leka.teashop.model.dto.AddressOfDeliveryDto;
import com.leka.teashop.model.dto.UserDetailsDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl {

    public void updateUserDetails(User user, UserDetailsDto userDetailsDto, AddressOfDeliveryDto deliveryDto) {
        user.setFirstName(userDetailsDto.getFirstName());
        user.setLastName(userDetailsDto.getLastName());
        user.setPhone(userDetailsDto.getPhone());
        if (userDetailsDto.getBirthday() != null) {
            user.setBirthday(userDetailsDto.getBirthday());
        }
        AddressOfDelivery addressOfDelivery = AddressOfDelivery.builder()
                .id(deliveryDto.getId())
                .country(deliveryDto.getCountry())
                .region(deliveryDto.getRegion())
                .city(deliveryDto.getCity())
                .operatorName(deliveryDto.getOperatorName())
                .numberOfDepartment(deliveryDto.getNumberOfDepartment())
                .zipCode(deliveryDto.getZipCode())
                .deliveryDetails(deliveryDto.getDeliveryDetails())
                .build();
        user.setAddressOfDelivery(addressOfDelivery);
    }


    public UserDetailsDto toDto(User user) {
        UserDetailsDto userDetailsDto = UserDetailsDto.builder()
                .userName(user.getUsername())
                .email(user.getEmail()).build();
        if (user.getFirstName() != null) {
            userDetailsDto.setFirstName(user.getFirstName());
        }
        if (user.getLastName() != null) {
            userDetailsDto.setLastName(user.getLastName());
        }
        if (user.getPhone() != null){
            userDetailsDto.setPhone(user.getPhone());
        }
        if (user.getBirthday() != null) {
            userDetailsDto.setBirthday(user.getBirthday());
        }
        return userDetailsDto;
    }
}
