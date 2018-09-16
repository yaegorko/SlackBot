package com.myslackbot.first.controllers;

import com.myslackbot.first.config.properties.GoogleProperties;
import com.myslackbot.first.config.properties.SlackProperties;
import com.myslackbot.first.services.GoogleSheetService;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import java.io.IOException;

public class EnableEventsControllerTest {

    EnableEventsController eventsController = new EnableEventsController(new GoogleSheetService(new GoogleProperties()), new SlackProperties());

//    @Test
//    public void testGetRealUserName() {
//        try {
//            eventsController.receiveUserNameByGetRequest("U1HFA0DD5");
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//    }
//

    @Test
    public void testJsonToObj() {

    }
}