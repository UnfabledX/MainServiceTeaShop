package com.leka.teashop.service;

import com.google.api.services.drive.model.File;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public interface GoogleService {

    List<List<Object>> loadTableOfProducts() throws IOException;

    List<File> getAllImagesOfProducts() throws IOException;

    void downloadImageByFileId(String fileId, ByteArrayOutputStream outputStream) throws IOException;
}
