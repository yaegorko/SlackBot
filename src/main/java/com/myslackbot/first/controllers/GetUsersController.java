package com.myslackbot.first.controllers;

import com.myslackbot.first.services.SlackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
public class GetUsersController {

    private final SlackService slackService;

    @Autowired
    public GetUsersController(SlackService slackService) {
        this.slackService = slackService;
    }

    @GetMapping
    public void getAllUserFromGeneral() {
        try {
            slackService.saveAllUserFromGeneral(slackService.receiveUsers());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
