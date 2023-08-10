package com.leka.teashop.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @Size(min = 3, max = 24, message = "{wrong.username}")
    private String userName;

    @Size(min = 5, max = 64, message = "{wrong.password}")
    private String password;

    @Email(message = "{wrong.email}")
    private String email;
}
