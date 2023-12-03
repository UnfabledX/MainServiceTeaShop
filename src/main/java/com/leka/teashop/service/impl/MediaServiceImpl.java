package com.leka.teashop.service.impl;

import com.leka.teashop.exception.NotFoundException;
import com.leka.teashop.model.dto.ImageDto;
import com.leka.teashop.service.MediaService;
import com.leka.teashop.utils.WebClientPageImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

    private final WebClient mediaWebClient;

    @Override
    public ImageDto uploadImageThrough(MultipartBodyBuilder builder) {
        return mediaWebClient.post()
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
        mediaWebClient.delete()
                .uri("/api/v1/images/delete/{id}", imageId)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    /**
     * The method updates image by its id. There are cases when product is present
     * but without an image (imageId = null). In such case, method receives
     * "-1" as id, so media service interprets this like just simple image
     * creation.
     *
     * @param imageId Id of the updated image
     * @param builder builder with uploaded file itself
     *                representing the file resource with byte array
     * @return dto of the image.
     */
    @Override
    public ImageDto updateImageById(Long imageId, MultipartBodyBuilder builder) {
        return mediaWebClient.put()
                .uri("/api/v1/images/update/{id}", imageId != null ? imageId : "-1")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(ImageDto.class)
                .blockOptional()
                .orElseThrow(() -> new NotFoundException("Media service is unavailable"));
    }

    @Override
    public Page<ImageDto> getAllImages() {
        return mediaWebClient.get()
                .uri("/api/v1/images/all")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<WebClientPageImpl<ImageDto>>() {})
                .blockOptional()
                .orElseThrow(() -> new NotFoundException("Media service is unavailable"));
    }

    @Override
    public ImageDto getImageByIdWithData(Long id) {
        ImageDto dto;
        if (id != null) {
            dto = mediaWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/images/{id}")
                            .queryParam("data", true)
                            .build(id))
                    .retrieve()
                    .bodyToMono(ImageDto.class)
                    .blockOptional()
                    .orElseThrow(() -> new NotFoundException("Media service is unavailable"));
        } else {
            dto = ImageDto.builder().build();
        }
        return dto;
    }

    @Override
    public void deleteAllImages() {
        mediaWebClient.delete()
                .uri("/api/v1/images/delete")
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
