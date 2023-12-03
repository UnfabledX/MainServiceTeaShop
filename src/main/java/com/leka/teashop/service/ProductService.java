package com.leka.teashop.service;

import com.leka.teashop.model.Product;
import com.leka.teashop.model.dto.ImageDto;
import com.leka.teashop.model.dto.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {

    void addProduct(ProductDto product, List<MultipartFile> files);

    Page<ProductDto> getAllProducts(Integer pageNo, Integer pageSize, String sortField, String sortDirection);

    void updateProduct(ProductDto request, List<MultipartFile> files, String deleteImage);

    void deleteById(Long id);

    Product findById(Long id);

    Page<ImageDto> getAllImages();
}
