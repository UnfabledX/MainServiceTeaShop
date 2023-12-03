package com.leka.teashop.service.impl;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.leka.teashop.config.GoogleConfig.GoogleProperties;
import com.leka.teashop.service.GoogleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.leka.teashop.config.GoogleAuthorizeUtil.HTTP_TRANSPORT;
import static com.leka.teashop.config.GoogleAuthorizeUtil.JSON_FACTORY;
import static com.leka.teashop.config.GoogleAuthorizeUtil.credentials;

@Service
@RequiredArgsConstructor
public class GoogleServiceImpl implements GoogleService {

    private final GoogleProperties googleProperties;
    private final Drive googleDriveService;

    @Override
    public List<List<Object>> loadTableOfProducts() throws IOException {
        Sheets service =
                new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credentials())
                        .setApplicationName("Teashop")
                        .build();
        ValueRange response = service.spreadsheets().values()
                .get(googleProperties.spreadsheetId(), googleProperties.rangeOfColumns())
                .execute();
        return response.getValues();
    }

    @Override
    public List<File> getAllImagesOfProducts() throws IOException {
        List<File> files = new ArrayList<>();
        String query = "'" + googleProperties.folderId() + "' in parents";
        String pageToken = null;
        do {
            FileList result = googleDriveService.files().list()
                    .setQ(query)
                    .setSpaces("drive")
                    .setPageSize(10)
                    .setFields("nextPageToken, files(id, name)")
                    .setPageToken(pageToken)
                    .execute();
            files.addAll(result.getFiles());

            pageToken = result.getNextPageToken();
        } while (pageToken != null);

        return files;
    }

    @Override
    public void downloadImageByFileId(String fileId, ByteArrayOutputStream outputStream) throws IOException {
        googleDriveService.files()
                .get(fileId)
                .executeMediaAndDownloadTo(outputStream);
    }

}
