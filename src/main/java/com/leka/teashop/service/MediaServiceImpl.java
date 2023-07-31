package com.leka.teashop.service;

import com.leka.teashop.exceptions.NotFoundException;
import com.leka.teashop.model.dto.ImageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

    private final WebClient webClient;

    @Override
    public ImageDto uploadImageThrough(MultipartBodyBuilder builder) {
        return webClient.post()
                .uri("/api/v1/images/upload")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(ImageDto.class)
                .blockOptional()
                .orElseThrow(() -> new NotFoundException("Media service is unavailable"));
    }

    @Override
    public void deleteImageById(Long imageId) {
        webClient.delete()
                .uri("/api/v1/images/delete/{id}", imageId)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    /**
     * Updates image by its id. There are cases when product is present
     * but without an image (imageId = null). In such case, method receives
     * "-1" as id, so media service interprets this like just simple image
     * creation.
     * @param imageId Id of the updated image
     * @param builder builder with uploaded file itself
     *                representing the file resource with byte array
     * @return dto of the image.
     */
    @Override
    public ImageDto updateImageById(Long imageId, MultipartBodyBuilder builder) {
        return webClient.put()
                .uri("/api/v1/images/update/{id}", imageId != null ? imageId : "-1")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(ImageDto.class)
                .blockOptional()
                .orElseThrow(() -> new NotFoundException("Media service is unavailable"));;
    }
}
