package com.myslackbot.first.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    @JsonProperty("real_name")
    private String name;
    @JsonProperty("email")
    private String email;
//
//    @JsonProperty("real_name")
//    public String getName() {
//        return name;
//    }
//    @JsonProperty("real_name")
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    @JsonProperty("email")
//    public String getEmail() {
//        return email;
//    }
//    @JsonProperty("email")
//    public void setEmail(String email) {
//        this.email = email;
//    }
}
