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
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

/**
 * TODO: не рабобтают значения из @Value, после разобраться.
 * Сервис работы с GoogleSheet
 * 1. Создать таблицу createSheet (или предоставить вручную доступ сервисному аккаунту из serviceAccountCredentials.json)
 * 2. Работать с ней.
 * Получить доступ из браузера к созданной таблице получится только с помощью Google Drive API setAccessToSheet.
 */

@Service
@PropertySource(value = "application.properties", ignoreResourceNotFound = true)
public class GoogleSheetService {

    @Value("${google.spreadsheet.id}")
    private String SHEET_ID = "1K4P6N1v3deXj-xd7ed-QNX5LlEVUruJyhAUzg6Yhu_o";
    @Value("${app.name}")
    private String APPLICATION_NAME = "SlackBot";

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
    public void setAccessToSheetByURL() {
        Drive drive = initializeDrive();
        Permission permission = new Permission()
                .setType("user")
                .setRole("reader")
                .setEmailAddress("yaegorko@gmail.com");
        try {
            drive.permissions().create(SHEET_ID, permission).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeToSheet() {

    }

    public void readFromSheet() {
            Sheets sheet = initializeSheet();
        try {
            ValueRange result = sheet.spreadsheets().values().get(SHEET_ID, "A1:C").execute();
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * TODO попытаться убрать Boilerplate. Параметризовать?
     * @return
     */
    private Sheets initializeSheet() {
        HttpTransport httpTransport = null;
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            FileInputStream fileInputStream = new FileInputStream(KEY_FILE_LOCATION);
            GoogleCredential credential = GoogleCredential.fromStream(fileInputStream).createScoped(SHEET_SCOPES);
            return new Sheets.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Drive initializeDrive() {
        HttpTransport httpTransport = null;
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            FileInputStream fileInputStream = new FileInputStream(KEY_FILE_LOCATION);
            GoogleCredential credential = GoogleCredential.fromStream(fileInputStream).createScoped(DRIVE_SCOPES);
            return new Drive.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
