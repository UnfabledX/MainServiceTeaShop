package com.leka.teashop.service;


import com.leka.teashop.model.User;
import com.leka.teashop.model.dto.AddressOfDeliveryDto;
import com.leka.teashop.model.dto.UserDetailsDto;
import com.leka.teashop.model.dto.UserDto;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {

    void register(UserDto userDto, HttpServletRequest httpServletRequest) throws Exception;

    String verifyEmailByToken(String verificationToken);

    String renewVerificationLink(String email, HttpServletRequest httpServletRequest);

    void saveUserAndDeliveryDetails(UserDetailsDto userDetailsDto, AddressOfDeliveryDto deliveryDto, User user);
}
