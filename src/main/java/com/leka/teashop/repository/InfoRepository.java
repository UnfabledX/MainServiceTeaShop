package com.leka.teashop.repository;

import com.leka.teashop.model.PaymentAndDeliveryInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InfoRepository extends JpaRepository<PaymentAndDeliveryInfo, Long> {

    @Query(value = "SELECT * FROM main_service.info ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Optional<PaymentAndDeliveryInfo> findByLastId();

    @Modifying
    @Query(value = "delete from main_service.info where id = (select id from main_service.info order by id limit 1)",
            nativeQuery = true)
    void deleteTheOldestRecord();
}
