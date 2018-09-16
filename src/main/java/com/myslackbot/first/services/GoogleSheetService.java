package com.myslackbot.first.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.myslackbot.first.config.properties.GoogleProperties;
import com.myslackbot.first.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Сервис работы с GoogleSheet
 * 1. Создать таблицу createSheet (или предоставить вручную доступ сервисному аккаунту из serviceAccountCredentials.json)
 * 2. Работать с ней.
 * Получить доступ из браузера к созданной таблице получится только с помощью Google Drive API setAccessToSheet.
 */

@Service
//@PropertySource(value = "application.properties", ignoreResourceNotFound = true)
public class GoogleSheetService {

    private final GoogleProperties googleProperties;

    @Autowired
    public GoogleSheetService(GoogleProperties googleProperties) {
        this.googleProperties = googleProperties;
    }

    //TODO почему работает только с абсолютным путем?
    private static final String KEY_FILE_LOCATION = "/home/egor/projects/SlackBot/src/main/resources/serviceAccountCredentials.json";
    private static final List<String> SHEET_SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final List<String> DRIVE_SCOPES = Collections.singletonList(DriveScopes.DRIVE);

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();


    public void createSheet() {
        Spreadsheet requestBody = new Spreadsheet();
        Sheets sheet = initializeSheet();
        try {

            Spreadsheet response = sheet.spreadsheets().create(requestBody).execute();
            System.out.println(response);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Устанавливаем привилегии с помощью Google Drive API. Google Sheets API не умеет.
     * Доступ на чтение.
     * TODO параметры.
     */
    public void setAccessToSheetByEmail() {
        Drive drive = initializeDrive();
        Permission permission = new Permission()
                .setType("user")
                .setRole("writer")
                .setEmailAddress("yaegorko@gmail.com");
        try {
            drive.permissions().create(googleProperties.getSpreadsheetId(), permission).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Добавляем запись в конец таблицы.
     * Ссылка для тестирования запросов на запись.
     * https://developers.google.com/sheets/api/reference/rest/v4/spreadsheets.values/batchUpdate?apix=true
     */
    public void appendToSheet(User user) {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        Sheets sheet = initializeSheet();
        ValueRange appendBody = new ValueRange()
                .setValues(Arrays.asList(Arrays.asList(dtf.format(now), user.getName(), user.getEmail())));
        try {
            AppendValuesResponse appendResult = sheet.spreadsheets().values()
                    .append(googleProperties.getSpreadsheetId(), "A1", appendBody)
                    .setValueInputOption("USER_ENTERED")
                    .setInsertDataOption("INSERT_ROWS") //добавляем данные в новую строку, а не перезаписываем.
                    .setIncludeValuesInResponse(true)
                    .execute();
            System.out.println(appendResult);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void readFromSheet() {
        Sheets sheet = initializeSheet();
        try {
            ValueRange result = sheet.spreadsheets().values().get(googleProperties.getSpreadsheetId(), "A1:C").execute();
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * TODO попытаться убрать Boilerplate. Параметризовать?
     * @return Sheet
     */
    private Sheets initializeSheet() {
        HttpTransport httpTransport;
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            FileInputStream fileInputStream = new FileInputStream(KEY_FILE_LOCATION);
            GoogleCredential credential = GoogleCredential.fromStream(fileInputStream).createScoped(SHEET_SCOPES);
            return new Sheets.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(googleProperties.getAppName()).build();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Drive initializeDrive() {
        HttpTransport httpTransport;
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            FileInputStream fileInputStream = new FileInputStream(KEY_FILE_LOCATION);
            GoogleCredential credential = GoogleCredential.fromStream(fileInputStream).createScoped(DRIVE_SCOPES);
            return new Drive.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(googleProperties.getAppName()).build();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
