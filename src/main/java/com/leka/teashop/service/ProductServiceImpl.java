package com.leka.teashop.service;

import com.leka.teashop.exceptions.NotFoundException;
import com.leka.teashop.mapper.ProductMapper;
import com.leka.teashop.model.Product;
import com.leka.teashop.model.dto.ImageDto;
import com.leka.teashop.model.dto.ProductDto;
import com.leka.teashop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final MediaService mediaService;

    @Override
    public void addProduct(ProductDto dto, MultipartFile file) {
        ImageDto image = null;
        if (file.getSize() != 0) {
            MultipartBodyBuilder builder = createFrom(file);
            image = mediaService.uploadImageThrough(builder);
        }
        Product product = productMapper.toEntity(dto);
        if (image != null) {
            product.setImageId(image.getId());
        }
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
        Product product = findById(id);
        Long imageId;
        if ((imageId = product.getImageId()) != null) {
            mediaService.deleteImageById(imageId);
        }
        productRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateProduct(ProductDto updatedProduct, MultipartFile file) {
        ImageDto image = null;
        if (file.getSize() != 0) {
            MultipartBodyBuilder builder = createFrom(file);
            Long imageId = updatedProduct.getImageId();
            image = mediaService.updateImageById(imageId, builder);
        }
        Product product = productMapper.toEntity(updatedProduct);
        product.setId(updatedProduct.getId());
        if (image != null) {
            product.setImageId(image.getId());
        }
        productRepository.save(product);
    }

    @Override
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product is not found"));
    }

    private MultipartBodyBuilder createFrom(MultipartFile file) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        try {
            if (file != null && file.getOriginalFilename() != null) {
                builder.part("file",
                        new ByteArrayResource(file.getBytes())).filename(file.getOriginalFilename());
            } else {
                throw new NotFoundException("The file is not provided");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return builder;
    }
}
