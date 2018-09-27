package com.myslackbot.first.controllers.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myslackbot.first.services.GoogleSheetService;
import com.myslackbot.first.services.SlackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


/**
 * Slack Events API присылает все на один адрес.
 */
@RestController
public class EventsController {

    private final GoogleSheetService sheetService;
    private final SlackService slackService;


    @Autowired
    public EventsController(GoogleSheetService sheetService,
                            SlackService slackService) {
        this.sheetService = sheetService;
        this.slackService = slackService;
    }

    @PostMapping(value = "/slack")
    public ResponseEntity<String> checkConnect(@RequestBody String body) {
        try {
            System.out.println(body);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(body);

            //валидация ссылки куда Слак будет слать запросы. Выполняется 1 раз при смене ссылки в Слак
            //https://api.slack.com/apps/ACLA3QY72/event-subscriptions?
            //не забывать нажимать кнопку Save Changes.
            JsonNode challenge = jsonNode.get("challenge");
            if (challenge != null) {
                return new ResponseEntity<>(challenge.asText(), HttpStatus.OK);
            }

            //обрабатываем событие на вход юзера на канал.
            String hashUserName = jsonNode.get("event").get("user").asText();
            if (hashUserName != null) {
                System.out.println(hashUserName);
                sheetService.appendToSheet(slackService.receiveUserNameByGetRequest(hashUserName));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
