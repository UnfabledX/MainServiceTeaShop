package com.leka.teashop.model.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

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
