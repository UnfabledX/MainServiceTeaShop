package com.leka.teashop.controller;


import com.leka.teashop.mapper.UserMapperImpl;
import com.leka.teashop.model.AddressOfDelivery;
import com.leka.teashop.model.User;
import com.leka.teashop.model.dto.AddressOfDeliveryDto;
import com.leka.teashop.model.dto.UserDetailsDto;
import com.leka.teashop.model.dto.UserDetailsDtoForAdmin;
import com.leka.teashop.model.dto.UserDto;
import com.leka.teashop.service.AddressOdDeliveryService;
import com.leka.teashop.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {

    @Value("${web.pageable.default-page-size}")
    private int defaultPageSize;
    private final UserService userService;
    private final UserMapperImpl userMapper;
    private final AddressOdDeliveryService deliveryService;

    /**
     * Builds presentation of registration form
     *
     * @param model model to insert in the form
     * @return the file name of *.html
     */
    @GetMapping("/register")
    public String getRegistration(Model model) {
        UserDto userDto = new UserDto();
        model.addAttribute("user", userDto);
        return "registration";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") UserDto userDto,
                           BindingResult result, Model model,
                           HttpServletRequest httpServletRequest) throws Exception {
        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "registration";
        }
        String passwordConfirm = httpServletRequest.getParameter("retypePass");
        if (!userDto.getPassword().equals(passwordConfirm)) {
            model.addAttribute("passwordError", "Passwords don't coincide");
            return "registration";
        }
        userService.register(userDto, httpServletRequest);
        return "redirect:/register?success";
    }

    @GetMapping("/verifyEmail")
    public String verifyEmail(@RequestParam("token") String verificationToken) {
        return userService.verifyEmailByToken(verificationToken);
    }

    @PostMapping("/renew")
    public String renewVerificationLink(@Valid @ModelAttribute("user") UserDto userDto,
                                        HttpServletRequest httpServletRequest) {
        return userService.renewVerificationLink(userDto.getEmail(), httpServletRequest);
    }

    @GetMapping("/renew")
    public String renewFormPage(Model model) {
        UserDto userDto = new UserDto();
        model.addAttribute("user", userDto);
        return "renew";
    }

    @GetMapping("/login")
    public String getLogin() {
        return "login";
    }

    @GetMapping("/error")
    public String getError() {
        return "error";
    }

    @GetMapping("/adminPanel")
    public String getAdminPanel() {
        return "admin-panel";
    }

    @GetMapping("/settings")
    public String getSettings(Model model, UsernamePasswordAuthenticationToken token) {
        User user = (User) token.getPrincipal();
        UserDetailsDto userDetailsDto = userMapper.toDto(user);
        model.addAttribute("user", userDetailsDto);
        AddressOfDelivery addressOfDelivery;
        if (user.getAddressOfDelivery() != null) {
            addressOfDelivery = user.getAddressOfDelivery();
        } else {
            addressOfDelivery = new AddressOfDelivery();
        }
        model.addAttribute("address", addressOfDelivery);
        return "user-settings";
    }

    @PostMapping("/settings")
    public String applySettings(@Valid @ModelAttribute("user") UserDetailsDto userDetailsDto, BindingResult resultUser,
                                @Valid @ModelAttribute("address") AddressOfDeliveryDto deliveryDto, BindingResult resultDelivery,
                                Model model, UsernamePasswordAuthenticationToken token) {
        if (resultDelivery.hasErrors() || resultUser.hasErrors()) {
            model.addAttribute("user", userDetailsDto);
            model.addAttribute("address", deliveryDto);
            return "user-settings";
        }
        User user = (User) token.getPrincipal();
        userService.saveUserAndDeliveryDetails(userDetailsDto, deliveryDto, user);
        return "redirect:/settings?success";
    }

    @GetMapping("/allUsers")
    public String getAllUsers(@RequestParam(name = "page", required = false) Integer pageNo,
                              @RequestParam(name = "size", required = false) Integer pageSize,
                              @RequestParam(name = "sort", required = false) String sortField,
                              @RequestParam(name = "dir", required = false) String sortDirection,
                              Model model) {
        if (sortDirection == null) {
            sortDirection = "asc";
        }
        if (sortField == null) {
            sortField = "createdAt";
        }
        if (pageNo == null) {
            pageNo = 1;
        }
        if (pageSize == null) {
            pageSize = defaultPageSize;
        }
        Page<UserDetailsDtoForAdmin> usersPage = userService.getAllUsers(pageNo, pageSize, sortField, sortDirection);
        List<UserDetailsDtoForAdmin> userDetailsDtoForAdmins = usersPage.getContent();

        model.addAttribute("users", userDetailsDtoForAdmins);

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", usersPage.getTotalPages());
        model.addAttribute("totalItems", usersPage.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDirection);
        model.addAttribute("reverseSortDir",
                Sort.Direction.ASC.name().equalsIgnoreCase(sortDirection) ? "desc" : "asc");
        return "list-of-users";
    }

    @GetMapping("/editAddress/{id}")
    public String editAddress(@PathVariable("id") Long deliveryId, Model model) {
        AddressOfDeliveryDto deliveryDto = deliveryService.findById(deliveryId);
        model.addAttribute("address", deliveryDto);
        model.addAttribute("userId", null);
        return "edit-create-address";
    }

    @PostMapping("/editAddress")
    public String changeUserDelivery(@Valid @ModelAttribute("address") AddressOfDeliveryDto deliveryDto,
                                     BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("address", deliveryDto);
            model.addAttribute("userId", null);
            return "edit-create-address";
        }
        deliveryService.updateAddressDelivery(deliveryDto);
        return "redirect:/allUsers?success";
    }


    @GetMapping("/createAddress/{id}")
    public String createAddress(@PathVariable("id") Long userId, Model model) {
        model.addAttribute("address", new AddressOfDeliveryDto());
        model.addAttribute("userId", userId);
        return "edit-create-address";
    }

    @PostMapping("/createAddress/{id}")
    public String createUserDelivery(@PathVariable("id") Long userId,
                                     @Valid @ModelAttribute("address") AddressOfDeliveryDto deliveryDto,
                                     BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("address", deliveryDto);
            model.addAttribute("userId", userId);
            return "redirect:/createAddress/" + userId;
        }
        deliveryService.createAddressDelivery(deliveryDto, userId);
        return "redirect:/allUsers?success";
    }

    @GetMapping("/editUser/{id}")
    public String changeUser(@PathVariable("id") Long userId, Model model) {
        UserDetailsDtoForAdmin userDto = userService.findById(userId);
        model.addAttribute("user", userDto);
        return "edit-user";
    }

    @PostMapping("/editUser")
    public String applyUserEdition(@Valid @ModelAttribute("user") UserDetailsDtoForAdmin userDto,
                                     BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "edit-user";
        }
        userService.updateUserDetails(userDto);
        return "redirect:/allUsers?success";
    }
}
