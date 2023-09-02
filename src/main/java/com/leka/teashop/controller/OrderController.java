package com.leka.teashop.controller;

import com.leka.teashop.model.User;
import com.leka.teashop.model.dto.ProductDto;
import com.leka.teashop.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final ProductController productController;

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
}
