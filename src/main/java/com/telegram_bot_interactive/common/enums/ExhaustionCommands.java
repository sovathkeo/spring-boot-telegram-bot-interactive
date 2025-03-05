package com.telegram_bot_interactive.common.enums;

import lombok.Getter;

@Getter
public enum ExhaustionCommands {

    TodayElk("TODAY-ELASTIC"),
    Last2DayElk("LAST_2_DAY-ELASTIC"),
    Last3DayElk("LAST_3_DAY-ELASTIC"),

    Today("TODAY"),
    Last2Day("LAST_2_DAY"),
    Last3Day("LAST_3_DAY");

    private final String key;

    ExhaustionCommands(String key) {
        this.key = key;
    }
}
