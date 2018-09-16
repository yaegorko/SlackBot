package com.myslackbot.first.controllers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myslackbot.first.config.properties.SlackProperties;
import com.myslackbot.first.models.User;
import com.myslackbot.first.services.GoogleSheetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Slack Events API присылает все на один адрес.
 * TODO оставить 1 парсер json
 */
@RestController
public class EnableEventsController {

    private final GoogleSheetService sheetService;
    private final SlackProperties slackProperties;


    @Autowired
    public EnableEventsController(GoogleSheetService sheetService, SlackProperties slackProperties) {
        this.sheetService = sheetService;
        this.slackProperties = slackProperties;
    }

    @PostMapping(value = "/")
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
                sheetService.appendToSheet(receiveUserNameByGetRequest(hashUserName));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Получаем реальное имя пользователя по хешированному.
     * TODO: способ deprecated, после подумать про другой способ.
     *
     * @param hashUserName хешированное имя от Slack
     * @throws IOException ошибка
     */
    private User receiveUserNameByGetRequest(String hashUserName) throws IOException {
        String url = "https://slack.com/api/users.info?token=" + slackProperties.getLegacyToken() + "&user=" + hashUserName;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JsonNode actualObj = objectMapper.readTree(response.toString()).get("user").get("profile");
        return objectMapper.treeToValue(actualObj, User.class);
    }
}
