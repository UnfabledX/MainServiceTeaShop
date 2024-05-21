package com.leka.teashop.controller;

import com.leka.teashop.event.OrderEmailEvent;
import com.leka.teashop.event.OrderEmailForAdminEvent;
import com.leka.teashop.mapper.AddressOfDeliveryMapper;
import com.leka.teashop.mapper.ProductMapper;
import com.leka.teashop.model.AddressOfDelivery;
import com.leka.teashop.model.User;
import com.leka.teashop.model.dto.AddressOfDeliveryDto;
import com.leka.teashop.model.dto.OrderDto;
import com.leka.teashop.model.dto.ProductDto;
import com.leka.teashop.model.dto.UserDetailsDto;
import com.leka.teashop.service.OrderService;
import com.leka.teashop.service.ProductService;
import com.leka.teashop.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
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
import java.util.Map;

import static com.leka.teashop.model.OrderStatus.IN_PROGRESS;

@Log4j2
@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final ProductController productController;
    private final ProductService productService;
    private final ProductMapper productMapper;
    private final AddressOfDeliveryMapper deliveryMapper;
    private final UserService userService;
    private final ApplicationEventPublisher publisher;

    @PostMapping("/addToOrder")
    public String addToOrder(@ModelAttribute("product") ProductDto productDto,
                             @RequestParam(name = "page", required = false) Integer page,
                             Model model, HttpServletRequest request, UsernamePasswordAuthenticationToken token) {
        log.info("token: {}", token);
        User currentUser = (User) token.getPrincipal();
        log.info("current user: {}", currentUser);
        String quantity = request.getParameter("quantity");
        orderService.addToOrder(currentUser, quantity, productDto);
        return productController.getAllProductsForSale(page, null, "name", "asc", null, model);
    }

    /**
     * The main idea with the order of a user is that a user can manipulate
     * with products as many times as he wishes (delete items, change item quantity,
     * add items). All these operations occur in memory without actual interaction
     * with the database. After proceeding to the next page a final version of the
     * user order stores in database through the order service. See method @{link #getDeliveryOptionsPage()}
     */
    @GetMapping("/cart")
    public String getCartPage(Model model, UsernamePasswordAuthenticationToken token) {
        User currentUser = (User) token.getPrincipal();
        OrderDto order = currentUser.getCurrentOrderDto();
        if (order != null) {
            Map<Long, Integer> productIdAndCount = order.getProductIdAndCount();
            List<ProductDto> products = productIdAndCount.keySet()
                    .stream()
                    .map(productService::findById)
                    .map(productMapper::toDto)
                    .toList();
            model.addAttribute("products", products);
            model.addAttribute("productIdAndCount", productIdAndCount);
        }
        model.addAttribute("order", order);
        return "items-cart";
    }

    @GetMapping("/increaseCounter/{id}")
    public String increaseCounter(@PathVariable("id") Long productId,
                                  UsernamePasswordAuthenticationToken token) {
        User currentUser = (User) token.getPrincipal();
        OrderDto order = currentUser.getCurrentOrderDto();
        orderService.increaseCounter(productId, order);
        return "redirect:/cart";
    }


    @GetMapping("/decreaseCounter/{id}")
    public String decreaseCounter(@PathVariable("id") Long productId,
                                  UsernamePasswordAuthenticationToken token) {
        User currentUser = (User) token.getPrincipal();
        OrderDto order = currentUser.getCurrentOrderDto();
        orderService.decreaseCounter(productId, order);
        if (order.getProductIdAndCount().isEmpty()) {
            currentUser.setCurrentOrderDto(null);
        }
        return "redirect:/cart";
    }

    @GetMapping("/deleteFromCart/{id}")
    public String deleteProductFromCart(@PathVariable("id") Long productId,
                                        UsernamePasswordAuthenticationToken token) {
        User currentUser = (User) token.getPrincipal();
        OrderDto order = currentUser.getCurrentOrderDto();
        orderService.deleteProductFromCart(productId, order);
        if (order.getProductIdAndCount().isEmpty()) {
            currentUser.setCurrentOrderDto(null);
        }
        return "redirect:/cart";
    }

    @GetMapping("/deliveryOptions")
    public String getDeliveryOptionsPage(Model model, UsernamePasswordAuthenticationToken token) {
        User currentUser = (User) token.getPrincipal();
        if (currentUser.getAddressOfDelivery() != null) {
            AddressOfDelivery addressOfDelivery = currentUser.getAddressOfDelivery();
            model.addAttribute("address", deliveryMapper.toDto(addressOfDelivery));
        } else {
            model.addAttribute("address", new AddressOfDeliveryDto());
        }
        model.addAttribute("user", currentUser);
        return "delivery-address";
    }

    @PostMapping("/applyDeliveryOptions")
    public String applyDeliveryOptions(@Valid @ModelAttribute("user") UserDetailsDto userDetailsDto, BindingResult resultUser,
                                       @Valid @ModelAttribute("address") AddressOfDeliveryDto deliveryDto, BindingResult resultDelivery,
                                       Model model, UsernamePasswordAuthenticationToken token) {
        if (resultDelivery.hasErrors() || resultUser.hasErrors()) {
            model.addAttribute("user", userDetailsDto);
            model.addAttribute("address", deliveryDto);
            return "delivery-address";
        }
        User currentUser = (User) token.getPrincipal();
        //saving to database and making order status IN_PROGRESS
        orderService.saveOrderWithStatus(currentUser.getCurrentOrderDto(), IN_PROGRESS);
        //save changes in user and delivery details to database if made any
        userService.saveUserAndDeliveryDetails(userDetailsDto, deliveryDto, currentUser);
        //send the completed order to user email
        publisher.publishEvent(new OrderEmailEvent(currentUser));
        //notify admin for the new user order by email.
        publisher.publishEvent(new OrderEmailForAdminEvent(currentUser));
        //setting current order to null, because completed order is in progress and new one is not started yet.
        currentUser.setCurrentOrderDto(null);
        //redirecting to home page with successful message about order completion
        return "redirect:/?successOrderCompletion";
    }

}
