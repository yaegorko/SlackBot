package com.myslackbot.first.controllers;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.springframework.http.HttpHeaders.USER_AGENT;

/**
 * Slack Events API присылает все на один адрес.
 */
@RestController
public class EnableEventsController {

    @PostMapping(value = "/")
    public ResponseEntity<String> checkConnect(@RequestBody String body) {

        String challenge = null;
        try {
            System.out.println(body);
            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject) parser.parse(body);
            challenge = (String) object.get("challenge");

            //валидация ссылки куда Слак будет слать запросы.
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

    private void receiveUserNameByGetRequest(String hashUserName) throws IOException, ParseException {

        String url = "https://slack.com/api/users.info?token=xoxp-51509784804-51520013447-428862694260-0a0d32025725593dfd3c152b247f284a&user=" + hashUserName;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
       // con.setRequestProperty("User-Agent", USER_AGENT);
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
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
