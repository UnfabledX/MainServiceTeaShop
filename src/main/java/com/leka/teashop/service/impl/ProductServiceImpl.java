package com.leka.teashop.service.impl;

import com.leka.teashop.exception.NotFoundException;
import com.leka.teashop.mapper.ProductMapper;
import com.leka.teashop.model.Image;
import com.leka.teashop.model.Product;
import com.leka.teashop.model.dto.ImageDto;
import com.leka.teashop.model.dto.ProductDto;
import com.leka.teashop.repository.ImageRepository;
import com.leka.teashop.repository.ProductRepository;
import com.leka.teashop.repository.predicate.ProductPredicatesBuilder;
import com.leka.teashop.service.GoogleService;
import com.leka.teashop.service.MediaService;
import com.leka.teashop.service.ProductService;
import com.leka.teashop.utils.PageContext;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.*;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

import static com.leka.teashop.model.ProductStatus.DELETED;
import static com.leka.teashop.model.ProductStatus.PRESENT;

@Log4j2
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    public static final String CHECKED = "on";

    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;
    private final ProductMapper productMapper;
    private final MediaService mediaService;
    private final GoogleService googleService;

    @Override
    @Transactional
    public void addProduct(ProductDto dto, List<MultipartFile> files) {
        log.info("Add product [{}]", dto);
        Product product = productMapper.toEntity(dto);
        if (!files.isEmpty() && !files.get(0).isEmpty()) {
            files.forEach(file -> {
                MultipartBodyBuilder builder = createFrom(file);
                ImageDto image = mediaService.uploadImageThrough(builder);
                product.addImage(new Image(image.getId(), product));
            });
        }
        productRepository.save(product);
        googleService.insertProductIntoGoogleSheets(product);
        googleService.insertImagesOfProductIntoGoogleDrive(product, files);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> getAllProductsForAdmin(PageContext context) {
        log.info("Getting All Products For admin");
        Pageable pageable = getPageableWithSort(context);
        return productRepository.findAll(pageable).map(productMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> getAllProductsForSale(PageContext context, Map<String, Boolean> filters) {
        log.info("Getting all the products for sale by filters=[{}]", filters);
        Pageable pageable = getPageableWithSort(context);
        BooleanExpression expression = filters.entrySet()
                .stream().filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .map(k -> getProductPredicatesBuilder().with("type", ":", k))
                .map(ProductPredicatesBuilder::build)
                .reduce(BooleanExpression::or)
                .orElse(getProductPredicatesBuilder().build());
        return productRepository.findAll(expression, pageable).map(productMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> getAllProductsForSale(PageContext context) {
        log.info("Getting all the products for sale. No filters");
        Pageable pageable = getPageableWithOutSorting(context);
        return productRepository.findAllProductsOrderedByType(pageable).map(productMapper::toDto);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        productRepository.findById(id).ifPresent(product -> product.setStatus(DELETED));
    }

    @Override
    @Transactional
    public void activateById(Long id) {
        productRepository.findById(id).ifPresent(product -> product.setStatus(PRESENT));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> getAllProductsBySearch(String search, PageContext context) {
        log.info("Getting all the products by search=[{}]", search);
        Pageable pageable = getPageableWithOutSorting(context);
        return productRepository.findAllProductsBySearch(search, pageable)
                .map(productMapper::toDto);
    }

    @Override
    @Transactional
    public void updateProduct(ProductDto updatedProduct, List<MultipartFile> files,
                              String deleteAllImages) {
        log.info("Updating the product=[{}] with options: files=[{}], deleteAllImages=[{}]",
                updatedProduct, files, deleteAllImages);
        Product product = productMapper.toEntity(updatedProduct);
        product.setId(updatedProduct.getId());

        if (!files.isEmpty() && !files.get(0).isEmpty()) {
            deleteImagesOf(updatedProduct);
            googleService.deleteImagesOnDriveOf(updatedProduct);
            for (MultipartFile file : files) {
                MultipartBodyBuilder builder = createFrom(file);
                ImageDto imageDto = mediaService.uploadImageThrough(builder);
                product.addImage(new Image(imageDto.getId(), product));
            }
            googleService.insertImagesOfProductIntoGoogleDrive(product, files);
        }

        if (CHECKED.equals(deleteAllImages)) {
            deleteImagesOf(updatedProduct);
            googleService.deleteImagesOnDriveOf(updatedProduct);
        }
        productRepository.save(product);
        String result = googleService.updateProductRecordInGoogleSheets(product);
        log.info("GoogleService: {}", result);
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

    private static ProductPredicatesBuilder getProductPredicatesBuilder() {
        return new ProductPredicatesBuilder()
                .with("status", ":", PRESENT.name());
    }

    private static Pageable getPageableWithSort(PageContext context) {
        Sort sort = context.getSortDirection().equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(context.getSortField()).ascending() : Sort.by(context.getSortField()).descending();
        return PageRequest.of(context.getPageNo() - 1, context.getPageSize(), sort);
    }

    private static PageRequest getPageableWithOutSorting(PageContext context) {
        return PageRequest.of(context.getPageNo() - 1, context.getPageSize());
    }
}
