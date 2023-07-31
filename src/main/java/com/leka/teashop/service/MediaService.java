package com.leka.teashop.service;

import com.leka.teashop.model.dto.ImageDto;
import org.springframework.http.client.MultipartBodyBuilder;

public interface MediaService {

    ImageDto uploadImageThrough(MultipartBodyBuilder builder);

    void deleteImageById(Long imageId);

    ImageDto updateImageById(Long imageId, MultipartBodyBuilder builder);
}
