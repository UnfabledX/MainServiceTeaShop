package com.leka.teashop.service;

import com.google.api.services.drive.model.File;
import com.leka.teashop.model.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public interface GoogleService {

    List<List<Object>> loadTableOfProducts() throws IOException;

    List<File> getAllImagesOfProducts() throws IOException;

    void downloadImageByFileId(String fileId, ByteArrayOutputStream outputStream) throws IOException;

    void insertImagesOfProductIntoGoogleDrive(Product product, List<MultipartFile> files);

    void insertProductIntoGoogleSheets(Product product);
}
