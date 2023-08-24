package com.leka.teashop.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsDto {

    private String userName;
    private String email;

    @NotEmpty(message = "{notEmpty.firstname}")
    @Size(min = 2, max = 24, message = "{wrong.firstname}")
    private String firstName;

    @NotEmpty(message = "{notEmpty.lastname}")
    @Size(min = 3, max = 30, message = "{wrong.lastname}")
    private String lastName;

    @NotEmpty(message = "{notEmpty.phone}")
    @Pattern(regexp = "\\+[0-9]{3}\\s?[0-9]{2}\\s?[0-9]{3}\\s?[0-9]{4}",
            message = "{wrong.format}")
    private String phone;

    @Past(message = "{wrong.date}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
}
