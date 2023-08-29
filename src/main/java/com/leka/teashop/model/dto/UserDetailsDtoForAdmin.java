package com.leka.teashop.model.dto;

import com.leka.teashop.model.AccountStatus;
import com.leka.teashop.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
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
    @Email(message = "{wrong.email}")
    private String email;
    private String firstName;
    private String lastName;
    @Pattern(regexp = "(^$)|(\\+[0-9]{3}\\s?[0-9]{2}\\s?[0-9]{3}\\s?[0-9]{4})",
            message = "{wrong.format}")
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
