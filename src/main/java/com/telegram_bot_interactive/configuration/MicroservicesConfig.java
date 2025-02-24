package com.telegram_bot_interactive.configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MicroservicesConfig {
    public ProvisioningServiceConfig provisioningService = new ProvisioningServiceConfig();
}
