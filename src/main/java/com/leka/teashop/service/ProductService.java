package com.leka.teashop.service;

import com.leka.teashop.model.Product;
import com.leka.teashop.model.dto.ProductDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {

    void addProduct(ProductDto product, MultipartFile file);

    List<ProductDto> getAllProducts();

    void updateProduct(ProductDto request, MultipartFile file);

    void deleteById(Long id);

    Product findById(Long id);

}
