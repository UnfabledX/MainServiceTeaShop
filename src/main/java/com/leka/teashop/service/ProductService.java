package com.leka.teashop.service;

import com.leka.teashop.model.dto.ProductDto;

import java.util.List;

public interface ProductService {

    void addProduct(ProductDto product);

    List<ProductDto> getAllProducts();
}
