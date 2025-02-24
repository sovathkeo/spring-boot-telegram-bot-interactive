package com.telegram_bot_interactive.common.enums;

import lombok.Getter;

@Getter
public enum TelegramBotMainMenu {
    None("NONE"),
    ExhaustionChart("EXHAUSTION_CHART"),
    MainServicesHealthCheck("MAIN_SERVICES_HEALTH_CHECK"),
    ProvisioningHealthCheck("PROVISIONING_HEALTH_CHECK");

    private final String key;
    TelegramBotMainMenu(String key) {
        this.key = key;
    }
}
