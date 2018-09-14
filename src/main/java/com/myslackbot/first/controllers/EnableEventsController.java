package com.myslackbot.first.controllers;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
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
 */
@RestController
@PropertySource("application.properties")
public class EnableEventsController {

    @Value("${slack.legacy.token}")
    private String LEGACY_TOKEN;

    @PostMapping(value = "/")
    public ResponseEntity<String> checkConnect(@RequestBody String body) {
        String challenge;
        try {
            System.out.println(body);
            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject) parser.parse(body);
            challenge = (String) object.get("challenge");

            //валидация ссылки куда Слак будет слать запросы. Выполняется 1 раз при смене ссылки в Слак
            //https://api.slack.com/apps/ACLA3QY72/event-subscriptions?
            if (challenge != null) {
                return new ResponseEntity<>(challenge, HttpStatus.OK);
            }

            //обрабатываем событие на вход юзера на канал.
            JSONObject event = (JSONObject) object.get("event");
            String hashUserName = (String) event.get("user");
            if (hashUserName != null) {
                System.out.println(hashUserName);
                receiveUserNameByGetRequest(hashUserName);
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Получаем реальное имя пользователя по хешированному.
     * TODO: способ deprecated, после найти другой способ.
     * @param hashUserName хешированное имя от Slack
     * @throws IOException ошибка
     * @throws ParseException ошибка
     */
    private void receiveUserNameByGetRequest(String hashUserName) throws IOException, ParseException {

        String url = "https://slack.com/api/users.info?token=" + LEGACY_TOKEN + "&user=" + hashUserName;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer(); // TODO заменить на StringBuilder
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        System.out.println(response.toString());
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(response.toString());
        JSONObject user = (JSONObject) object.get("user");
        String userName = (String) user.get("real_name");
        System.out.println(userName);
    }
}
