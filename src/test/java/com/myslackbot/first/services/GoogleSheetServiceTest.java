package com.myslackbot.first.services;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class GoogleSheetServiceTest {

    GoogleSheetService sheetService = new GoogleSheetService();

    @Test
    public void testReadFromSheet() {
        sheetService.readFromSheet();
        System.out.println();
    }

    @Test
    public void testCreateNewSheet() {
        sheetService.createSheet();
        System.out.println();
    }

    @Test
    public void testChangeRole() {
        sheetService.setAccessToSheetByURL();
        System.out.println();
    }

}