package com.leka.teashop.service.impl;


import com.leka.teashop.config.EmailPropertiesConfig;
import com.leka.teashop.event.RegistrationEmailEvent;
import com.leka.teashop.event.RenewLinkEmailEvent;
import com.leka.teashop.event.ResetPasswordEmailEvent;
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
import com.leka.teashop.utils.Delay;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.leka.teashop.model.AccountStatus.ACTIVE;

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
    private final EmailPropertiesConfig emailPropertiesConfig;

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
            LocalDateTime expirationTokenTime = user.getTokenTime()
                    .plusMinutes(emailPropertiesConfig.getTimeToLiveForRegistrationLink());
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
            user = userOptional.get();
            LocalDateTime expirationTokenTime = user.getTokenTime()
                    .plusMinutes(emailPropertiesConfig.getTimeToLiveForRegistrationLink());
            if (expirationTokenTime.isAfter(LocalDateTime.now())) {
                result = "redirect:/login?stillValidLink";
            } else {
                String verificationToken = UUID.randomUUID().toString();
                user.setVerificationToken(verificationToken);
                user.setTokenTime(LocalDateTime.now());
                userRepository.save(user);
                String appContextUrl = request.getRequestURL()
                        .toString().replace(request.getServletPath(), "");
                publisher.publishEvent(new RenewLinkEmailEvent(user, appContextUrl));
                result = "redirect:/login?success";
            }
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
        User currentUser = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new NotFoundException("The user is not found!"));
        userDetailsMapper.updateUserDetails(currentUser, userDto);
        userRepository.save(currentUser);
    }

    @Override
    public String processResetPassword(String email, String appContextUrl) {
        Optional<User> userOptional = userRepository.findUserByEmail(email);
        String result;
        User user;
        if (userOptional.isPresent()) {
            if ((user = userOptional.get()).getAccountStatus() == ACTIVE) {
                String token = UUID.randomUUID().toString();
                user.setVerificationToken(token);
                user.setTokenTime(LocalDateTime.now());
                userRepository.save(user);
                publisher.publishEvent(new ResetPasswordEmailEvent(user, appContextUrl));
                ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
                executorService.schedule(() -> new Delay(userRepository)
                                .deleteTokenWhenExpiredAndNotUsedInPasswordResetOperation(token),
                        emailPropertiesConfig.getTimeToLiveForPasswordResetLink(), TimeUnit.MINUTES);
                result = "redirect:/resetPassword?success";
            } else {
                result = "redirect:/resetPassword?activateUser";
            }
        } else {
            result = "redirect:/resetPassword?emailNotExists";
        }
        return result;
    }

    @Override
    public UserDto confirmReset(String token) {
        Optional<User> userOptional = userRepository.findUserByVerificationToken(token);
        UserDto userDto = null;
        if (userOptional.isPresent() && userOptional.get().getAccountStatus() == AccountStatus.ACTIVE) {
            User user = userOptional.get();
            userRepository.deleteVerificationTokenByUserId(user.getId());
            userDto = UserDto.builder().userName(user.getUsername()).build();
        }
        return userDto;
    }

    @Override
    public void applyNewPassword(UserDto userDto) {
        User user = userRepository.findByUserName(userDto.getUserName())
                .orElseThrow(() -> new NotFoundException("The user is not found!"));
        user.setPassword(encoder.encode(userDto.getPassword()));
        userRepository.save(user);
    }


}
