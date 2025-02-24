package com.telegram_bot_interactive.configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GlobalConfig {
    public String maintenanceMode = "";
    public String availableAt = "";
    public String maintenanceMessage = "";
}
