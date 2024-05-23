package com.leka.teashop.model;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Setter
@Getter
@Entity
@Table(name = "info")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class PaymentAndDeliveryInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String contentUA;

    @Column(nullable = false)
    private String contentEU;
}
