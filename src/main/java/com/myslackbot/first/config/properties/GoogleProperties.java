package com.myslackbot.first.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Ламбок тут не включать, с ним не достаются значения из application.properties.
 */
@Configuration
@PropertySource("classpath:application.properties")
@ConfigurationProperties("google")
public class GoogleProperties {

    private String appName;
    private String spreadsheetId;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getSpreadsheetId() {
        return spreadsheetId;
    }

    public void setSpreadsheetId(String spreadsheetId) {
        this.spreadsheetId = spreadsheetId;
    }
}
