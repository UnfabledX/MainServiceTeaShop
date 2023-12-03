package com.leka.teashop.controller;

import com.leka.teashop.service.impl.InitLoadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class InitLoadController {

    private final InitLoadService initLoadService;

    @GetMapping("/load")
    public String loadProducts() throws IOException {
        initLoadService.loadProductsFromGoogleDrive();
        return "redirect:/allProducts?waiting";
    }

}
