package com.leka.teashop.mapper;

import com.leka.teashop.model.AddressOfDelivery;
import com.leka.teashop.model.User;
import com.leka.teashop.model.dto.AddressOfDeliveryDto;
import com.leka.teashop.model.dto.UserDetailsDto;
import com.leka.teashop.model.dto.UserDetailsDtoForAdmin;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapperImpl {

    private final AddressOfDeliveryMapper addressOfDeliveryMapper;

    public void updateUserDetails(User user, UserDetailsDto userDetailsDto, AddressOfDeliveryDto deliveryDto) {
        user.setFirstName(userDetailsDto.getFirstName());
        user.setLastName(userDetailsDto.getLastName());

        String dtoPhone = userDetailsDto.getPhone();
        if (!dtoPhone.isBlank()) {
            user.setPhone(dtoPhone);
        }
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

    public void updateUserDetails(User user, UserDetailsDtoForAdmin userDetailsDto) {
        String dtoUsername = userDetailsDto.getUserName();
        if (!dtoUsername.isBlank()) {
            user.setUserName(dtoUsername);
        }

        String dtoEmail = userDetailsDto.getEmail();
        if (!dtoEmail.isBlank()) {
            user.setEmail(dtoEmail);
        }

        String dtoFirstName = userDetailsDto.getFirstName();
        if (!dtoFirstName.isBlank()) {
            user.setFirstName(dtoFirstName);
        }

        String dtoLastName = userDetailsDto.getLastName();
        if (!dtoLastName.isBlank()) {
            user.setLastName(dtoLastName);
        }

        String dtoPhone = userDetailsDto.getPhone();
        if (!dtoPhone.isBlank()) {
            user.setPhone(dtoPhone);
        }

        if (userDetailsDto.getBirthday() != null) {
            user.setBirthday(userDetailsDto.getBirthday());
        }

        user.setRole(userDetailsDto.getRole());
        user.setAccountStatus(userDetailsDto.getAccountStatus());
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
        if (user.getPhone() != null) {
            userDetailsDto.setPhone(user.getPhone());
        }
        if (user.getBirthday() != null) {
            userDetailsDto.setBirthday(user.getBirthday());
        }
        return userDetailsDto;
    }

    public UserDetailsDtoForAdmin toUsersDetailsDtoForAdmin(User user) {
        AddressOfDeliveryDto addressDto = addressOfDeliveryMapper.toDto(user.getAddressOfDelivery());
        UserDetailsDtoForAdmin userDetailsDto = UserDetailsDtoForAdmin.builder()
                .id(user.getId())
                .userName(user.getUsername())
                .email(user.getEmail())
                .addressOfDelivery(addressDto)
                .role(user.getRole())
                .accountStatus(user.getAccountStatus())
                .createdAt(user.getCreatedAt()).build();
        if (user.getFirstName() != null) {
            userDetailsDto.setFirstName(user.getFirstName());
        }
        if (user.getLastName() != null) {
            userDetailsDto.setLastName(user.getLastName());
        }
        if (user.getPhone() != null) {
            userDetailsDto.setPhone(user.getPhone());
        }
        if (user.getBirthday() != null) {
            userDetailsDto.setBirthday(user.getBirthday());
        }
        if (user.getUpdatedAt() != null) {
            userDetailsDto.setUpdatedAt(user.getUpdatedAt());
        }
        return userDetailsDto;
    }
}
