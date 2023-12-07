package com.leka.teashop.controller;

import com.leka.teashop.model.Product;
import com.leka.teashop.service.GoogleService;
import com.leka.teashop.service.impl.InitLoadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Controller
@RequiredArgsConstructor
public class InitLoadController {

    private final InitLoadService initLoadService;
    private final GoogleService googleService;

    @GetMapping("/load")
    public String loadProducts() throws IOException {
        initLoadService.loadProductsFromGoogleDrive();
        return "redirect:/allProducts?waiting";
    }

    /**
     * Service endpoint for testing purposes.
     */
    @ResponseBody
    @PostMapping(value = "/load", consumes = {APPLICATION_JSON_VALUE, MULTIPART_FORM_DATA_VALUE})
    public void loadToGoogle(@RequestPart("product") Product product, @RequestPart("files") List<MultipartFile> files) {
        googleService.insertProductIntoGoogleSheets(product);
        googleService.insertImagesOfProductIntoGoogleDrive(product, files);
    }



}
