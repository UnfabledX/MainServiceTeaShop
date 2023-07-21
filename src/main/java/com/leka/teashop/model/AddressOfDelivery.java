package com.leka.teashop.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "address_deliveries")
public class AddressOfDelivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String country;
    @Column
    private String region;
    @Column
    private String city;

    @Column(name = "operator_name")
    private String operatorName;

    @Column(name = "number_department")
    private Integer numberOfDepartment;

    @Column(name = "zip_code")
    private Integer zipCode;

}
