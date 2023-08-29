package com.leka.teashop.service.impl;


import com.leka.teashop.event.RegistrationEmailEvent;
import com.leka.teashop.exception.NotFoundException;
import com.leka.teashop.exception.UserAlreadyExistsException;
import com.leka.teashop.mapper.UserMapperImpl;
import com.leka.teashop.model.AccountStatus;
import com.leka.teashop.model.Role;
import com.leka.teashop.model.User;
import com.leka.teashop.model.dto.AddressOfDeliveryDto;
import com.leka.teashop.model.dto.UserDetailsDto;
import com.leka.teashop.model.dto.UserDetailsDtoForAdmin;
import com.leka.teashop.model.dto.UserDto;
import com.leka.teashop.repository.UserRepository;
import com.leka.teashop.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final UserMapperImpl userDetailsMapper;
    private final ApplicationEventPublisher publisher;
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

    @Override
    public void saveUserAndDeliveryDetails(UserDetailsDto userDetailsDto, AddressOfDeliveryDto deliveryDto, User user) {
        userDetailsMapper.updateUserDetails(user, userDetailsDto, deliveryDto);
        userRepository.save(user);
    }

    @Override
    public Page<UserDetailsDtoForAdmin> getAllUsers(Integer pageNo, Integer pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return userRepository.findAll(pageable)
                .map(userDetailsMapper::toUsersDetailsDtoForAdmin);
    }

    @Override
    public UserDetailsDtoForAdmin findById(Long userId) {
        return userRepository.findById(userId)
                .map(userDetailsMapper::toUsersDetailsDtoForAdmin)
                .orElseThrow(() -> new NotFoundException("The user is not found!"));
    }

    @Override
    public void updateUserDetails(UserDetailsDtoForAdmin userDto) {
        User currentUser = userRepository.findUserByEmail(userDto.getEmail())
                .orElseThrow(() -> new NotFoundException("The user is not found!"));
        userDetailsMapper.updateUserDetails(currentUser, userDto);
        userRepository.save(currentUser);
    }

}
