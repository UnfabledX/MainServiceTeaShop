package com.leka.teashop.controller;

import com.leka.teashop.mapper.ProductMapper;
import com.leka.teashop.model.Product;
import com.leka.teashop.model.dto.ProductDto;
import com.leka.teashop.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @GetMapping("product")
    public String getProduct(@ModelAttribute("request") ProductDto request) {
        return "add-product";
    }

    @GetMapping("allProducts")
    public String getAllProducts(Model model) {
        List<ProductDto> dtoList = productService.getAllProducts();
        model.addAttribute("products", dtoList);
        return "list-of-products";
    }

    @GetMapping("edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id,
                                 Model model) {
        Product productFromDB = productService.findById(id);
        model.addAttribute("productDto", productMapper.toDto(productFromDB));
        return "update-product";
    }

    @GetMapping("showAllProductsForSale")
    public String showAllProductsForSale (Model model){
        List<ProductDto> dtoList = productService.getAllProducts();
        model.addAttribute("products", dtoList);
        return "products-for-sale";
    }


    @PostMapping("product")
    public String addProduct(@RequestParam("file") MultipartFile file,
            @Valid @ModelAttribute("request") ProductDto request, BindingResult result) {
        if (result.hasErrors()) {
            return "add-product";
        }
        productService.addProduct(request, file);
        return "redirect:/product";
    }

    @GetMapping("delete/{id}")
    public String deleteProduct(@PathVariable(name = "id") Long id) {
        productService.deleteById(id);
        return "redirect:/allProducts";
    }

    //BindingResult should be immediate to @Valid object.
    @PostMapping("updateProduct/{id}")
    public String updateProduct(@PathVariable(name = "id") Long id,
                                @Valid @ModelAttribute("request") ProductDto request,
                                BindingResult result, Model model,
                                @RequestParam("file") MultipartFile file) {
        if (result.hasErrors()) {
            return showUpdateForm(id, model);
        }
        productService.updateProduct(request, file);
        return "redirect:/allProducts";
    }
}
