package com.leka.teashop.controller;

import com.leka.teashop.mapper.ProductMapper;
import com.leka.teashop.model.dto.AddressOfDeliveryDto;
import com.leka.teashop.model.dto.OrderDto;
import com.leka.teashop.model.dto.ProductDto;
import com.leka.teashop.model.dto.UserDetailsDtoForAdmin;
import com.leka.teashop.service.AddressOdDeliveryService;
import com.leka.teashop.service.OrderService;
import com.leka.teashop.service.ProductService;
import com.leka.teashop.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AdminController {

    @Value("${web.pageable.default-page-size}")
    private int defaultPageSize;
    private final UserService userService;
    private final AddressOdDeliveryService deliveryService;
    private final OrderService orderService;
    private final ProductService productService;
    private final ProductMapper productMapper;

    @GetMapping("/adminPanel")
    public String getAdminPanel() {
        return "admin-panel";
    }

    @GetMapping("/allUsers")
    public String getAllUsers(@RequestParam(name = "page", defaultValue = "1") Integer pageNo,
                              @RequestParam(name = "size", required = false) Integer pageSize,
                              @RequestParam(name = "sort", defaultValue = "createdAt") String sortField,
                              @RequestParam(name = "dir", defaultValue = "asc") String sortDirection,
                              Model model) {
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

    @GetMapping("/allOrders")
    public String getAllOrdersFromAllUsers(@RequestParam(name = "page", defaultValue = "1") Integer pageNo,
                                           @RequestParam(name = "size", defaultValue = "10") Integer pageSize,
                                           @RequestParam(name = "sort", defaultValue = "createdAt") String sortField,
                                           @RequestParam(name = "dir", defaultValue = "asc") String sortDirection,
                                           Model model) {
        Page<OrderDto> orderDtoPage = orderService.getAllOrders(pageNo, pageSize, sortField, sortDirection);
        List<OrderDto> orders = orderDtoPage.getContent();
        List<UserDetailsDtoForAdmin> users = orders.stream()
                .map(OrderDto::getUserId)
                .map(userService::findById)
                .toList();
        List<AddressOfDeliveryDto> addresses = users.stream()
                .map(UserDetailsDtoForAdmin::getAddressOfDelivery)
                .toList();
        List<List<ProductDto>> products = orders.stream()
                .map(OrderDto::getProductIdAndCount)
                .map(m -> m.keySet()
                        .stream()
                        .map(productService::findById)
                        .map(productMapper::toDto)
                        .toList())
                .toList();
        List<Map<Long, Integer>> listOfProductIdAndCount = orders.stream()
                .map(OrderDto::getProductIdAndCount)
                .toList();

        model.addAttribute("productsListForEachOrder", products);
        model.addAttribute("listOfProductIdAndCount", listOfProductIdAndCount);

        model.addAttribute("orders", orders);
        model.addAttribute("users", users);
        model.addAttribute("addresses", addresses);

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", orderDtoPage.getTotalPages());
        model.addAttribute("totalItems", orderDtoPage.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDirection);
        model.addAttribute("reverseSortDir",
                Sort.Direction.ASC.name().equalsIgnoreCase(sortDirection) ? "desc" : "asc");
        return "list-of-orders-for-admin";
    }


    @GetMapping("/changeOrderStatus/{id}")
    public String changeOrderStatus(@PathVariable("id") Long orderId,
                                    @RequestParam("status") String orderStatus,
                                    @RequestParam(name = "page", defaultValue = "1") Integer pageNo,
                                    @RequestParam(name = "size", defaultValue = "10") Integer pageSize,
                                    @RequestParam(name = "sort", defaultValue = "createdAt") String sortField,
                                    @RequestParam(name = "dir", defaultValue = "asc") String sortDirection) {
        orderService.updateOrderByIdAndWithStatus(orderId, orderStatus);
        return "redirect:/allOrders?page=" + pageNo + "&size=" + pageSize +
               "&sort=" + sortField + "&dir=" + sortDirection;
    }
}
