package com.telegram_bot_interactive.common.enums;

import lombok.Getter;

@Getter
public enum ExhaustionCommands {

    Today("TODAY"),
    Last2Day("LAST_2_DAY"),
    Last3Day("LAST_3_DAY");

    private final String key;

    ExhaustionCommands(String key) {
        this.key = key;
    }
}
