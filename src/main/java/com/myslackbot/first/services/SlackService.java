package com.myslackbot.first.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myslackbot.first.config.properties.SlackProperties;
import com.myslackbot.first.models.Member;
import com.myslackbot.first.models.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Service
public class SlackService {

    private final SlackProperties slackProperties;
    private final GoogleSheetService googleSheetService;

    @Autowired
    public SlackService(SlackProperties slackProperties, GoogleSheetService googleSheetService) {
        this.slackProperties = slackProperties;
        this.googleSheetService = googleSheetService;
    }

    /**
     * Получаем реальное имя пользователя по хешированному.
     * TODO: способ deprecated???, после подумать про другой способ.
     *
     * @param hashUserName хешированное имя от Slack
     * @throws IOException ошибка
     */
    public Profile receiveUserNameByGetRequest(String hashUserName) throws IOException {
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
        return objectMapper.treeToValue(actualObj, Profile.class);
    }

    /**
     * Получаем список пользователей и "имеилов" с general.
     */
    public List<Member> receiveUsers() throws IOException {
        String url = "https://slack.com/api/users.list?token="+ slackProperties.getLegacyToken() + "&pretty=1";
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
        JsonNode actualObj = objectMapper.readTree(response.toString()).get("members");
        return objectMapper.readValue(actualObj.toString(), new TypeReference<List<Member>>() {});
    }

    public void saveAllUserFromGeneral(List<Member> userList) {
        for (Member member: userList) {
            googleSheetService.appendToSheet(member.getProfile());
        }
    }
}
