package com.telegram_bot_interactive.configuration;

import com.telegram_bot_interactive.Authenticator2faDemoApplication;
import com.telegram_bot_interactive.common.StringHelper;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "application-config")
@JsonIgnoreProperties(value = {"$$beanFactory"})
@Setter
@Getter
public class ApplicationConfiguration {
    public static final String APPLICATION_NAME = Authenticator2faDemoApplication.class.getPackageName();

    @Getter
    public GlobalConfig globalConfig = new GlobalConfig();

    public int globalRequestTimeoutMillisecond;
    public int globalConnectTimeoutMillisecond;

    @Getter
    public String maintenanceMode = "";

    public String maintenanceMessage = "";

    private String[] endpointsAuthWhitelist = new String[]{};

    public MicroservicesConfig microservices = new MicroservicesConfig();

    public Long adminId;

    public String getMaintenanceMessage() {
        return StringHelper.isNullOrEmpty(maintenanceMessage)
                ? this.globalConfig.maintenanceMessage
                : this.maintenanceMessage;
    }

}