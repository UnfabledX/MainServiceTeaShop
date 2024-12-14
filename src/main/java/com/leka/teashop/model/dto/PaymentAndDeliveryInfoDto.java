package com.leka.teashop.model.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentAndDeliveryInfoDto {

    private Long id;

    @NotEmpty(message = "{notEmpty.content}")
    private String contentUA;

    @NotEmpty(message = "{notEmpty.content}")
    private String contentEU;
}
