package com.leka.teashop.service;

import com.leka.teashop.model.Product;
import com.leka.teashop.model.dto.ImageDto;
import com.leka.teashop.model.dto.ProductDto;
import com.leka.teashop.utils.PageContext;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ProductService {

    void addProduct(ProductDto product, List<MultipartFile> files);

    Page<ProductDto> getAllProductsForAdmin(PageContext pageContext);

    void updateProduct(ProductDto request, List<MultipartFile> files, String deleteImage);

    void deleteById(Long id);

    Product findById(Long id);

    Page<ImageDto> getAllImages();

    void activateById(Long id);

    Page<ProductDto> getAllProductsBySearch(String search, PageContext pageContext);

    Page<ProductDto> getAllProductsForSale(PageContext pageContext, Map<String, Boolean> filters);

    Page<ProductDto> getAllProductsForSale(PageContext pageContext);
}
