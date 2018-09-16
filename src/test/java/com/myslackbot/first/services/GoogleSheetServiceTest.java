//package com.myslackbot.first.services;
//
//import com.myslackbot.first.config.properties.GoogleProperties;
//import com.myslackbot.first.models.User;
//import org.junit.Test;
//
//import static org.junit.Assert.*;
//
//public class GoogleSheetServiceTest {
//
//    GoogleSheetService sheetService = new GoogleSheetService(new GoogleProperties());
//
//    @Test
//    public void testReadFromSheet() {
//        sheetService.readFromSheet();
//        System.out.println();
//    }
//
//    @Test
//    public void testCreateNewSheet() {
//        sheetService.createSheet();
//        System.out.println();
//    }
//
//    @Test
//    public void testChangeRole() {
//        sheetService.setAccessToSheetByURL();
//        System.out.println();
//    }
//
//    @Test
//    public void testAppendToSheet() {
//        sheetService.appendToSheet(new User("Egor", "yaegorko@gmail.com"));
//        System.out.println();
//    }
//}
