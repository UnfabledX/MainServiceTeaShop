package com.leka.teashop.service;

import com.leka.teashop.exceptions.NotFoundException;
import com.leka.teashop.mapper.ProductMapper;
import com.leka.teashop.model.Product;
import com.leka.teashop.model.dto.ImageDto;
import com.leka.teashop.model.dto.ProductDto;
import com.leka.teashop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final WebClient webClient;

    @Override
    public void addProduct(ProductDto dto, MultipartFile file) {
        MultipartBodyBuilder builder = createFrom(file);
        ImageDto image = webClient.post()
                .uri("/api/v1/images/upload")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(ImageDto.class)
                .blockOptional()
                .orElseThrow(() -> new NotFoundException("Media service is unavailable"));
        Product product = productMapper.toEntity(dto);
        product.setImageId(image.getId());
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
        Long imageId = null;
        if ((imageId = product.getImageId()) != null) {
            webClient.delete()
                    .uri("/api/v1/images/delete/{id}", imageId)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        }
        productRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateProduct(ProductDto updatedProduct, MultipartFile file) {
        MultipartBodyBuilder builder = createFrom(file);
        webClient.put()
                .uri("/api/v1/images/update/{id}", updatedProduct.getImageId())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(ImageDto.class)
                .blockOptional()
                .orElseThrow(() -> new NotFoundException("Media service is unavailable"));
        Product product = productMapper.toEntity(updatedProduct);
        product.setId(updatedProduct.getId());
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
                builder.part("file", new ByteArrayResource(file.getBytes())).filename(file.getOriginalFilename());
            } else {
                throw new NotFoundException("The file is not provided");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return builder;
    }
}
