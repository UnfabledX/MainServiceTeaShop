package com.leka.teashop.service.impl;


import com.leka.teashop.event.RegistrationEmailEvent;
import com.leka.teashop.exception.UserAlreadyExistsException;
import com.leka.teashop.model.AccountStatus;
import com.leka.teashop.model.Role;
import com.leka.teashop.model.User;
import com.leka.teashop.model.dto.UserDto;
import com.leka.teashop.repository.UserRepository;
import com.leka.teashop.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    public static final String EMAIL_ALREADY_EXISTS = "The user email already exists";
    public static final String NAME_ALREADY_EXISTS = "The user name already exists";

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final ApplicationEventPublisher publisher;
    private final LocaleChangeInterceptor localeChangeInterceptor;
    @Value("${email.user-registered.time-to-live}")
    private int timeToLive;

    @Override
    public void register(UserDto userDto, HttpServletRequest request) {
        String verificationToken = UUID.randomUUID().toString();
        String userName = userDto.getUserName();
        String userEmail = userDto.getEmail();
        if (userRepository.existsByUserName(userName)) {
            throw new UserAlreadyExistsException(NAME_ALREADY_EXISTS, userName);
        }
        if (userRepository.existsByEmail(userEmail)) {
            throw new UserAlreadyExistsException(EMAIL_ALREADY_EXISTS, userEmail);
        }
        User user = User.builder()
                .userName(userName)
                .password(encoder.encode(userDto.getPassword()))
                .verificationToken(verificationToken)
                .tokenTime(LocalDateTime.now())
                .email(userDto.getEmail())
                .role(Role.USER)
                .accountStatus(AccountStatus.INACTIVE)
                .build();
        userRepository.save(user);
        String appContextUrl = request.getRequestURL()
                .toString().replace(request.getServletPath(), "");
        publisher.publishEvent(new RegistrationEmailEvent(user, appContextUrl));
    }

    @Override
    public String verifyEmailByToken(String verificationToken) {
        String result;
        Optional<User> userOptional = userRepository.findUserByVerificationToken(verificationToken);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            LocalDateTime expirationTokenTime = user.getTokenTime().plusMinutes(timeToLive);
            Long id = user.getId();
            if (expirationTokenTime.isBefore(LocalDateTime.now())) {
                result = "redirect:/login?expired";
            } else {
                userRepository.deleteVerificationTokenAndUpdateStatusById(id);
                result = "redirect:/login?verified";
            }
        } else {
            result = "redirect:/login?corrupted";
        }
        return result;
    }

    @Override
    public String renewVerificationLink(String email, HttpServletRequest request) {
        String result;
        User user;
        Optional<User> userOptional = userRepository.findUserByEmail(email);
        if (userOptional.isPresent() && userOptional.get().getAccountStatus() == AccountStatus.INACTIVE) {
            String verificationToken = UUID.randomUUID().toString();
            user = userOptional.get();
            user.setVerificationToken(verificationToken);
            user.setTokenTime(LocalDateTime.now());
            userRepository.save(user);
            String appContextUrl = request.getRequestURL()
                    .toString().replace(request.getServletPath(), "");
            publisher.publishEvent(new RegistrationEmailEvent(user, appContextUrl));
            result = "redirect:/renew?success";
        } else {
            result = "redirect:/renew?invalid";
        }
        return result;
    }

}
