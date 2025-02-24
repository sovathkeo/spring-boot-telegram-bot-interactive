package com.telegram_bot_interactive.configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseMicroserviceConfig {
    public String baseUrl;
    public int requestTimeoutMillisecond;
}
