package com.myslackbot.first.controllers;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class EnableEventsController {

    @PostMapping(value = "/")
    public ResponseEntity<String> checkConnect(@RequestBody String body) {
        String challenge = " ";
        try {
            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject) parser.parse(body);
            challenge = (String) object.get("challenge");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(challenge, HttpStatus.OK);
    }
}
