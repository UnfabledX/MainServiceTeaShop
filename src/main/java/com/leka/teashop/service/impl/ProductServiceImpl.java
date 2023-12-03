package com.leka.teashop.service.impl;

import com.leka.teashop.exception.NotFoundException;
import com.leka.teashop.mapper.ProductMapper;
import com.leka.teashop.model.Image;
import com.leka.teashop.model.Product;
import com.leka.teashop.model.dto.ImageDto;
import com.leka.teashop.model.dto.ProductDto;
import com.leka.teashop.repository.ImageRepository;
import com.leka.teashop.repository.ProductRepository;
import com.leka.teashop.service.MediaService;
import com.leka.teashop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    public static final String CHECKED = "on";

    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;
    private final ProductMapper productMapper;
    private final MediaService mediaService;

    @Override
    @Transactional
    public void addProduct(ProductDto dto, List<MultipartFile> files) {
        Product product = productMapper.toEntity(dto);
        if (!files.isEmpty() && !files.get(0).isEmpty()) {
            files.forEach(file -> {
                MultipartBodyBuilder builder = createFrom(file);
                ImageDto image = mediaService.uploadImageThrough(builder);
                product.addImage(new Image(image.getId(), product));
            });
        }
        productRepository.save(product);
        //todo implement adding product to Google disk as well
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> getAllProducts(Integer pageNo, Integer pageSize, String sortField,
                                           String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return productRepository.findAll(pageable)
                .map(productMapper::toDto);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Product product = findById(id);
        List<Image> images = product.getImages();
        if (!images.isEmpty()) {
            images.forEach(image -> mediaService.deleteImageById(image.getImageId()));
        }
        productRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateProduct(ProductDto updatedProduct, List<MultipartFile> files, String deleteAllImages) {
        Product product = productMapper.toEntity(updatedProduct);
        product.setId(updatedProduct.getId());

        if (!files.isEmpty() && !files.get(0).isEmpty()) {
            deleteImagesOf(updatedProduct);
            for (MultipartFile file : files) {
                MultipartBodyBuilder builder = createFrom(file);
                ImageDto imageDto = mediaService.uploadImageThrough(builder);
                product.addImage(new Image(imageDto.getId(), product));
            }
        }

        if (CHECKED.equals(deleteAllImages)) {
            deleteImagesOf(updatedProduct);
        }
        productRepository.save(product);
    }

    @Override
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product is not found"));
    }

    @Override
    public Page<ImageDto> getAllImages() {
        return mediaService.getAllImages();
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

    private void deleteImagesOf(ProductDto updatedProduct) {
        productRepository.findById(updatedProduct.getId()).ifPresent(
                oldProduct -> oldProduct.getImages()
                        .stream()
                        .map(Image::getImageId)
                        .forEach(imageId -> {
                                    mediaService.deleteImageById(imageId);
                                    imageRepository.deleteById(imageId);
                                }
                        )
        );
    }
}
