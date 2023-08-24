package com.leka.teashop.repository;

import com.leka.teashop.model.AddressOfDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressOfDeliveryRepository extends JpaRepository<AddressOfDelivery, Long> {

}
