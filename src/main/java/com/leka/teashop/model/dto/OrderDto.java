package com.leka.teashop.model.dto;

import com.leka.teashop.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private Long id;
    private Long userId;
    private Map<Long, Integer> productIdAndCount;
    private LocalDateTime createdAt;
    private OrderStatus orderStatus;
}
