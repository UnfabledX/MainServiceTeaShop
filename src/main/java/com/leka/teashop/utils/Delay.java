package com.leka.teashop.utils;

import com.leka.teashop.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Delay{

    private final UserRepository userRepository;

    public void deleteTokenWhenExpiredAndNotUsedInPasswordResetOperation(String token) {
        userRepository.findUserByVerificationToken(token)
                .ifPresent(user -> userRepository.deleteVerificationTokenByUserId(user.getId()));
    }
}
