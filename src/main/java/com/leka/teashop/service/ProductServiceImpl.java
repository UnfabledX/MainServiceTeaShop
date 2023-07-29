package com.leka.teashop.service;

import com.leka.teashop.exceptions.NotFoundException;
import com.leka.teashop.mapper.ProductMapper;
import com.leka.teashop.model.Product;
import com.leka.teashop.model.dto.ProductDto;
import com.leka.teashop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public void addProduct(ProductDto dto) {
        Product product = productMapper.toEntity(dto);
        productRepository.save(product);
    }

    @Override
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateProduct(ProductDto updatedProduct) {
        productRepository.save(productMapper.toEntity(updatedProduct));
    }

    @Override
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product is not found"));
    }
}
