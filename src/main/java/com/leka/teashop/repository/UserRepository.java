package com.leka.teashop.repository;

import com.leka.teashop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserName(String userName);
    boolean existsByUserName(String userName);
    boolean existsByEmail(String userEmail);

    Optional<User> findUserByVerificationToken(String token);
    Optional<User> findUserByEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.verificationToken = null, u.tokenTime = null, " +
            "u.accountStatus = 'ACTIVE' WHERE u.id=:userId")
    void deleteVerificationTokenAndUpdateStatusById(Long userId);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.verificationToken = null, u.tokenTime = null WHERE u.id=:userId")
    void deleteVerificationTokenByUserId(Long userId);

}
