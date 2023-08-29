package com.leka.teashop.model.dto;

import com.leka.teashop.model.AccountStatus;
import com.leka.teashop.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsDtoForAdmin {

    private Long id;
    private String userName;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
    private AddressOfDeliveryDto addressOfDelivery;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    private Role role;
    private AccountStatus accountStatus;
}
