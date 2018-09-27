package com.myslackbot.first.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Profile {

    @JsonProperty("real_name")
    private String name;
    @JsonProperty("email")
    private String email;
}
