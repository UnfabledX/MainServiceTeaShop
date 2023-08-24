package com.leka.teashop.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressOfDeliveryDto {

    private Long id;

    @Size(min = 2, max = 50, message = "{wrong.country}")
    private String country;

    private String region;

    @Size(min = 2, max = 50, message = "{wrong.city}")
    private String city;

    @NotEmpty(message = "{notEmpty.operatorName}")
    private String operatorName;

    private Integer numberOfDepartment;

    private Integer zipCode;

    @Size(max = 250, message = "{wrong.deliveryDetails}")
    private String deliveryDetails;

}
