package com.leka.teashop.service;


import com.leka.teashop.model.User;
import com.leka.teashop.model.dto.AddressOfDeliveryDto;
import com.leka.teashop.model.dto.UserDetailsDto;
import com.leka.teashop.model.dto.UserDetailsDtoForAdmin;
import com.leka.teashop.model.dto.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;

public interface UserService {

    void register(UserDto userDto, HttpServletRequest httpServletRequest) throws Exception;

    String verifyEmailByToken(String verificationToken);

    String renewVerificationLink(String email, HttpServletRequest httpServletRequest);

    void saveUserAndDeliveryDetails(UserDetailsDto userDetailsDto, AddressOfDeliveryDto deliveryDto, User user);

    Page<UserDetailsDtoForAdmin> getAllUsers(Integer pageNo, Integer pageSize, String sortField, String sortDirection);

    UserDetailsDtoForAdmin findById(Long userId);

    void updateUserDetails(UserDetailsDtoForAdmin userDto);

    String processResetPassword(String email, String appContextUrl);

    UserDto confirmReset(String token);

    void applyNewPassword(UserDto userDto);
}
