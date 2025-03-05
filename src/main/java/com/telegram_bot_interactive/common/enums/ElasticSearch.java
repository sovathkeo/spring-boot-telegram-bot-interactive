package com.telegram_bot_interactive.common.enums;

import lombok.Getter;

@Getter
public enum ElasticSearch {
    KeyAsStringDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    final String value;

    ElasticSearch(String value) { this.value = value;}
}
