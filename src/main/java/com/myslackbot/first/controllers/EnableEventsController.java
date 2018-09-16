package com.myslackbot.first.controllers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myslackbot.first.config.properties.SlackProperties;
import com.myslackbot.first.models.User;
import com.myslackbot.first.services.GoogleSheetService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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
        String challenge;
        try {
            System.out.println(body);
            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject) parser.parse(body);

            //валидация ссылки куда Слак будет слать запросы. Выполняется 1 раз при смене ссылки в Слак
            //https://api.slack.com/apps/ACLA3QY72/event-subscriptions?
            //не забывать нажимать кнопку Save Changes.
            challenge = (String) object.get("challenge");
            if (challenge != null) {
                return new ResponseEntity<>(challenge, HttpStatus.OK);
            }

            //обрабатываем событие на вход юзера на канал.
            JSONObject event = (JSONObject) object.get("event");
            String hashUserName = (String) event.get("user");
            if (hashUserName != null) {
                System.out.println(hashUserName);
                User user = receiveUserNameByGetRequest(hashUserName);
                sheetService.appendToSheet(user);
            }

        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Получаем реальное имя пользователя по хешированному.
     * TODO: способ deprecated, после подумать про другой способ.
     *
     * @param hashUserName хешированное имя от Slack
     * @throws IOException    ошибка
     * @throws ParseException ошибка
     */
    private User receiveUserNameByGetRequest(String hashUserName) throws IOException, ParseException {

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
