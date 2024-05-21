package com.leka.teashop.service.impl;

import com.google.api.services.drive.model.File;
import com.leka.teashop.model.Image;
import com.leka.teashop.model.Product;
import com.leka.teashop.model.ProductType;
import com.leka.teashop.model.dto.ImageDto;
import com.leka.teashop.repository.ImageRepository;
import com.leka.teashop.repository.ProductRepository;
import com.leka.teashop.service.GoogleService;
import com.leka.teashop.service.MediaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.leka.teashop.model.ProductStatus.PRESENT;

@Log4j2
@Service
@RequiredArgsConstructor
public class InitLoadService {

    private final GoogleService googleService;
    private final MediaService mediaService;
    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;

    @Async
    @Transactional
    public void loadProductsFromGoogleDrive() throws IOException {
        List<List<Object>> values = googleService.loadTableOfProducts();
        List<File> files = googleService.getAllImagesOfProducts();
        mediaService.deleteAllImages();
        imageRepository.deleteAll();
        Product product;    ByteArrayOutputStream os;
        for (List<Object> row : values) {
            List<File> filesForProduct = getFilesByProductNumber(row.get(0).toString(), files);
            product = getProduct(row);
            for (File file: filesForProduct) {
                os  = new ByteArrayOutputStream();
                googleService.downloadImageByFileId(file.getId(), os);
                MultipartBodyBuilder builder = createBuilderFrom(os, file);
                ImageDto image = mediaService.uploadImageThrough(builder);
                product.addImage(new Image(image.getId(), product));
            }
            Product saved = productRepository.save(product);
            log.info("Saved product {}", saved);
        }
    }

    private Product getProduct(List<Object> row) {
        return Product.builder()
                .id(Long.parseLong(row.get(0).toString()))
                .name(row.get(1).toString())
                .description(row.get(2).toString())
                .priceUA(new BigDecimal(String.valueOf(row.get(3))))
                .priceEU(new BigDecimal(String.valueOf(row.get(4))))
                .type(ProductType.valueOf(row.get(5).toString().toUpperCase()))
                .images(new ArrayList<>())
                .status(PRESENT)
                .build();
    }

    private List<File> getFilesByProductNumber(String productNumber, List<File> files) {
        return files.stream()
                .filter(file -> hasNumber(productNumber, file.getName()))
                .sorted(Comparator.comparing(File::getName))
                .toList();
    }

    private boolean hasNumber(String productNumber, String fileName) {
        int pointIndex = fileName.indexOf(".");
        if (pointIndex != -1) {
            String number = fileName.substring(0, pointIndex);
            return productNumber.equals(number);
        } else {
            return false;
        }
    }

    private MultipartBodyBuilder createBuilderFrom(ByteArrayOutputStream baos, File googleFile) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new ByteArrayResource(baos.toByteArray()))
                .filename(googleFile.getName());
        return builder;
    }


}
