package com.telegram_bot_interactive.configuration;

import lombok.Data;

import java.util.HashMap;

@Data
public class ElasticsearchConfig {
    public String baseUrl = "";
    public String username = "";
    public String password = "";
    public HashMap<String, String> indexes = new HashMap<>();

    public String buildBusinessSearchUrl() {
        var index = indexes.get("business-namespace");
        return baseUrl.replace("{INDEX}", index);
    }
}