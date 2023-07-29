package com.leka.teashop.service;

import com.leka.teashop.model.Product;
import com.leka.teashop.model.dto.ProductDto;

import java.util.List;

public interface ProductService {

    void addProduct(ProductDto product);

    List<ProductDto> getAllProducts();

    void updateProduct(ProductDto request);

    void deleteById(Long id);

    Product findById(Long id);

}
