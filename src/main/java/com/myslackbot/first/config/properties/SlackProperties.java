package com.myslackbot.first.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@PropertySource("classpath:application.properties")
@ConfigurationProperties("slack")
public class SlackProperties {

    private String legacyToken;

}
