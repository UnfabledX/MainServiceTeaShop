package com.leka.teashop.controller;

import com.leka.teashop.model.dto.ProductDto;
import com.leka.teashop.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("product")
    public String getProduct(@ModelAttribute("request") ProductDto request){
        return "product";
    }

    @PostMapping("product")
    public String addProduct(@Valid @ModelAttribute("request") ProductDto request, BindingResult result){
        if (result.hasErrors()){
            return "product";
        }
        productService.addProduct(request);
        return "redirect:product";
    }

    @GetMapping("allproducts")
    public String getAllProducts(Model model){
        List<ProductDto> dtoList = productService.getAllProducts();
        model.addAttribute("products", dtoList);
        return "list-of-products";
    }

}
