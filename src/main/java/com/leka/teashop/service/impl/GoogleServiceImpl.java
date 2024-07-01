package com.leka.teashop.service.impl;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.leka.teashop.config.GoogleConfig.GoogleProperties;
import com.leka.teashop.model.Product;
import com.leka.teashop.model.dto.ProductDto;
import com.leka.teashop.service.GoogleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class GoogleServiceImpl implements GoogleService {

    private final GoogleProperties googleProperties;
    private final Drive googleDriveService;
    private final Sheets googleSheetsService;

    @Override
    public List<List<Object>> loadTableOfProducts() {
        log.info("Loading of the table content is started.");
        ValueRange response = null;
        try {
            response = googleSheetsService.spreadsheets().values()
                    .get(googleProperties.spreadsheetId(), googleProperties.rangeOfColumns())
                    .execute();
        } catch (Exception e) {
            log.error("Error in loadTableOfProducts(), ", e);
        }
        log.info("Loading of the table content is successfully finished.");
        return response != null ? response.getValues() : Collections.emptyList();
    }

    @Override
    public List<File> getAllImagesOfProducts() {
        log.info("Obtaining the list of all images of products");
        List<File> files = new ArrayList<>();
        String query = "'" + googleProperties.folderId() + "' in parents";
        String pageToken = null;
        do {
            FileList result = null;
            try {
                result = googleDriveService.files().list()
                        .setQ(query)
                        .setSpaces("drive")
                        .setPageSize(10)
                        .setFields("nextPageToken, files(id, name)")
                        .setPageToken(pageToken)
                        .execute();
            } catch (Exception e) {
                log.error("Error in getAllImagesOfProducts(). Can't get FileList result, ", e);
            }
            if (result != null) {
                files.addAll(result.getFiles());
                pageToken = result.getNextPageToken();
            }
        } while (pageToken != null);
        log.info("The list [size={}] of all images of products is formed.", files.size());
        return files;
    }

    @Override
    public void downloadImageByFileId(String fileId, ByteArrayOutputStream outputStream) {
        log.info("Image [id={}] downloading to the outputStream is started.", fileId);
        try {
            googleDriveService.files()
                    .get(fileId)
                    .executeMediaAndDownloadTo(outputStream);
        } catch (Exception e) {
            log.error("Error in downloadImageByFileId(), ", e);
        }
        log.info("Image [id={}] downloading to the outputStream is successfully finished.", fileId);
    }

    @Override
    public void insertImagesOfProductIntoGoogleDrive(Product product, List<MultipartFile> files) {
        log.info("Starting uploading files");
        MultipartFile firstFile = files.get(0);
        if (!firstFile.isEmpty()) {
            int i = 0;
            for (MultipartFile file : files) {
                try {
                    String fileName = product.getId() + "." + i + "-" + file.getOriginalFilename();
                    File fileMetadata = new File();
                    fileMetadata.setParents(Collections.singletonList(googleProperties.folderId()));
                    fileMetadata.setName(fileName);
                    File uploadedFile = googleDriveService.files()
                            .create(fileMetadata, new InputStreamContent(
                                    file.getContentType(),
                                    new ByteArrayInputStream(file.getBytes()))
                            )
                            .setFields("id").execute();
                    i++;
                    log.info("Id of uploaded file = [{}]", uploadedFile.getId());
                } catch (Exception e) {
                    log.error("Error in insertImagesOfProductIntoGoogleDrive(): ", e);
                }
            }
        }
    }

    @Override
    public void insertProductIntoGoogleSheets(Product product) {
        log.info("Inserting the product [{}] in the end of a Google sheet", product);
        List<Object> productRow = List.of(product.getId(), product.getName(), product.getDescription(),
                product.getPriceUA(), product.getPriceEU(), product.getType().name());
        List<List<Object>> values = List.of(productRow);
        try {
            ValueRange body = new ValueRange().setValues(values);
            AppendValuesResponse result = googleSheetsService.spreadsheets()
                    .values()
                    .append(googleProperties.spreadsheetId(), "A1", body)
                    .setValueInputOption("USER_ENTERED")
                    .setInsertDataOption("INSERT_ROWS")
                    .execute();
            log.info("The insertion operation is successfully finished. Affected rows = {}. {} cells appended.",
                    result.getUpdates().getUpdatedRows(), result.getUpdates().getUpdatedCells());
        } catch (Exception e) {
            log.error("Error in insertProductIntoGoogleSheets(): ", e);
        }
    }

    @Override
    public void deleteImagesOnDriveOf(ProductDto updatedProduct) {
        log.info("Starting images deletion of product [{}]", updatedProduct);
        try {
            List<File> files = getAllImagesOfProducts();
            List<String> idsList = files.stream()
                    .filter(file -> file.getName().startsWith(updatedProduct.getId() + "."))
                    .map(File::getId)
                    .toList();
            for (String id : idsList) {
                googleDriveService.files().delete(id).execute();
                log.info("File [id={}] is deleted", id);
            }
            log.info("All delete operations on Google Drive are successfully finished!");
        } catch (Exception e) {
            log.error("Error in deleteImagesOnDriveOf(updatedProduct): ", e);
        }
    }

    @Override
    public String updateProductRecordInGoogleSheets(Product product) {
        log.info("Updating product [{}] in Google Sheets", product);
        BatchUpdateValuesResponse response = null;
        try {
            List<Object> productRow = List.of(product.getId(), product.getName(),
                    product.getDescription(), product.getPriceUA(), product.getPriceEU(), product.getType().name());
            List<List<Object>> valueToUpdate = List.of(productRow);
            List<List<Object>> allPresentValues = loadTableOfProducts();

            String range = allPresentValues.stream()
                    .filter(row -> row.get(0).toString().equals(product.getId().toString()))
                    .map(allPresentValues::indexOf)
                    .map(rowNumber -> "Products!A" + (rowNumber + 2) + ":F" + (rowNumber + 2))
                    .findFirst()
                    .orElse(null);
            List<ValueRange> data = new ArrayList<>();
            data.add(new ValueRange().setRange(range).setValues(valueToUpdate));
            BatchUpdateValuesRequest request = new BatchUpdateValuesRequest()
                    .setValueInputOption("USER_ENTERED")
                    .setData(data);
            response = googleSheetsService.spreadsheets()
                    .values()
                    .batchUpdate(googleProperties.spreadsheetId(), request)
                    .execute();
            log.info("Product update is successful. {} cells updated.", response.getTotalUpdatedCells());
        } catch (Exception ex) {
            log.error("Error in updateProductRecordInGoogleSheets(product): ", ex);
        }
        String result = "Some error appeared";
        if (response != null) {
            result = String.format("Total updated rows - [%d], total updated cells - [%d]",
                    response.getTotalUpdatedRows(), response.getTotalUpdatedCells());
        }
        return result;
    }
}
