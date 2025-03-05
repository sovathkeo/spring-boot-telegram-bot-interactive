package com.telegram_bot_interactive.common.enums;

import lombok.Getter;

@Getter
public enum TimeZones {
    AsiaBangKok("Asia/Bangkok"), // "Asia/Bangkok",
    Utc("GMT");

    final String key;

    TimeZones(String key) { this.key = key;}
}
