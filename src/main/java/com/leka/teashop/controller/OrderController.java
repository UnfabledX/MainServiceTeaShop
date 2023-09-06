package com.leka.teashop.controller;

import com.leka.teashop.mapper.AddressOfDeliveryMapper;
import com.leka.teashop.mapper.ProductMapper;
import com.leka.teashop.model.AddressOfDelivery;
import com.leka.teashop.model.User;
import com.leka.teashop.model.dto.OrderDto;
import com.leka.teashop.model.dto.ProductDto;
import com.leka.teashop.service.OrderService;
import com.leka.teashop.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final ProductController productController;
    private final ProductService productService;
    private final ProductMapper productMapper;
    private final AddressOfDeliveryMapper deliveryMapper;

    @PostMapping("/addToOrder")
    public String addToOrder(@ModelAttribute("product") ProductDto productDto,
                             @RequestParam(name = "page", required = false) Integer page,
                             Model model, HttpServletRequest request, UsernamePasswordAuthenticationToken token) {
        System.out.println("token: " + token);
        User currentUser = (User) token.getPrincipal();
        System.out.println("current user: " + currentUser);
        String quantity = request.getParameter("quantity");
        orderService.addToOrder(currentUser, quantity, productDto);
        return productController.getAllProducts(page, null, null, null, model, request);
    }

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
        AddressOfDelivery addressOfDelivery = currentUser.getAddressOfDelivery();
        model.addAttribute("address", deliveryMapper.toDto(addressOfDelivery));
        return "";
    }
}
