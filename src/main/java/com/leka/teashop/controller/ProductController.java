package com.leka.teashop.controller;

import com.leka.teashop.mapper.ProductMapper;
import com.leka.teashop.model.Product;
import com.leka.teashop.model.dto.ProductDto;
import com.leka.teashop.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @GetMapping("product")
    public String getProduct(@ModelAttribute("request") ProductDto dto) {
        return "add-product";
    }

    @GetMapping({"allProducts", "showAllProductsForSale"})
    public String getAllProducts(@RequestParam(name = "page", required = false) Integer pageNo,
                                 @RequestParam(name = "size", required = false) Integer pageSize,
                                 @RequestParam(name = "sort", required = false) String sortField,
                                 @RequestParam(name = "dir", required = false) String sortDirection,
                                 Model model, HttpServletRequest httpServletRequest) {
        String urlPath = httpServletRequest.getRequestURI();
        urlPath = urlPath.substring(urlPath.lastIndexOf('/') + 1);
        if (sortDirection == null) {
            sortDirection = "asc";
        }
        if (sortField == null) {
            sortField = "name";
        }
        if (pageNo == null) {
            pageNo = 1;
        }
        if (pageSize == null) {
            pageSize = 3;
        }
        Page<ProductDto> dtoList = productService.getAllProducts(pageNo, pageSize, sortField, sortDirection);
        List<ProductDto> productDtoList = dtoList.getContent();

        model.addAttribute("products", productDtoList);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", dtoList.getTotalPages());
        model.addAttribute("totalItems", dtoList.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDirection);
        model.addAttribute("reverseSortDir",
                Sort.Direction.ASC.name().equalsIgnoreCase(sortDirection) ? "desc" : "asc");
        return urlPath.equals("allProducts") ? "list-of-products" : "products-for-sale";
    }

    @GetMapping("edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id,
                                 Model model) {
        Product productFromDB = productService.findById(id);
        model.addAttribute("request", productMapper.toDto(productFromDB));
        return "update-product";
    }

    @PostMapping("addProduct")
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
    public String updateProduct(@Valid @ModelAttribute("request") ProductDto request,
                                BindingResult result, Model model,
                                @PathVariable(name = "id") Long id,
                                @RequestParam(name = "file", required = false) MultipartFile file,
                                HttpServletRequest httpServletRequest) {
        if (result.hasErrors()) {
            model.addAttribute("errors", result.getAllErrors());
            return showUpdateForm(id, model);
        }
        String deleteImage = httpServletRequest.getParameter("deleteImage");
        productService.updateProduct(request, file, deleteImage);
        return "redirect:/allProducts";
    }
}
