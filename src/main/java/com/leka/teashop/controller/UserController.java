package com.leka.teashop.controller;


import com.leka.teashop.mapper.ProductMapper;
import com.leka.teashop.mapper.UserMapperImpl;
import com.leka.teashop.model.AddressOfDelivery;
import com.leka.teashop.model.User;
import com.leka.teashop.model.dto.AddressOfDeliveryDto;
import com.leka.teashop.model.dto.OrderDto;
import com.leka.teashop.model.dto.ProductDto;
import com.leka.teashop.model.dto.UserDetailsDto;
import com.leka.teashop.model.dto.UserDto;
import com.leka.teashop.service.OrderService;
import com.leka.teashop.service.ProductService;
import com.leka.teashop.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapperImpl userMapper;
    private final OrderService orderService;
    private final ProductService productService;
    private final ProductMapper productMapper;

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

    @GetMapping("/userOrders")
    public String getAlUserOrders(@RequestParam(name = "page", defaultValue = "1") Integer pageNo,
                                  @RequestParam(name = "size", defaultValue = "10") Integer pageSize,
                                  @RequestParam(name = "sort", defaultValue = "createdAt") String sortField,
                                  @RequestParam(name = "dir", defaultValue = "asc") String sortDirection,
                                  Model model, UsernamePasswordAuthenticationToken token) {
        User user = (User) token.getPrincipal();
        OrderDto actualOrder = user.getCurrentOrderDto();
        if (actualOrder != null){
            orderService.updateStartedOrderToActualState(actualOrder);
        }

        Page<OrderDto> allOrdersPage = orderService.getAllOrdersByUserId(user.getId(), pageNo,
                pageSize, sortField, sortDirection);
        List<OrderDto> orderDtoList = allOrdersPage.getContent();
        model.addAttribute("orders", orderDtoList);


        if (!orderDtoList.isEmpty()) {
            List<List<ProductDto>> products = orderDtoList.stream()
                    .map(OrderDto::getProductIdAndCount)
                    .map(m -> m.keySet()
                            .stream()
                            .map(productService::findById)
                            .map(productMapper::toDto)
                            .toList())
                    .toList();
            List<Map<Long, Integer>> listOfProductIdAndCount = orderDtoList.stream()
                    .map(OrderDto::getProductIdAndCount)
                    .toList();

            model.addAttribute("productsListForEachOrder", products);
            model.addAttribute("listOfProductIdAndCount", listOfProductIdAndCount);

            model.addAttribute("currentPage", pageNo);
            model.addAttribute("totalPages", allOrdersPage.getTotalPages());
            model.addAttribute("totalItems", allOrdersPage.getTotalElements());

            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDir", sortDirection);
            model.addAttribute("reverseSortDir",
                    Sort.Direction.ASC.name().equalsIgnoreCase(sortDirection) ? "desc" : "asc");
        }
        return "user-orders";
    }


}
