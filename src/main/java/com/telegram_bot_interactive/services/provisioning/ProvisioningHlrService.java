package com.telegram_bot_interactive.services.provisioning;

import com.telegram_bot_interactive.common.wrappers.SerializationWrapper;
import com.telegram_bot_interactive.common.wrappers.WebClientWrapper;
import com.telegram_bot_interactive.configuration.ApplicationConfiguration;
import com.telegram_bot_interactive.models.provisioning.ProvisioningPayloadModel;
import com.telegram_bot_interactive.models.provisioning.ProvisioningResponseBaseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ProvisioningHlrService {

    @Autowired
    private WebClientWrapper webClient;

    private final String BASE_URL ;

    @Autowired
    public ProvisioningHlrService(ApplicationConfiguration appSetting) {
        BASE_URL = appSetting.microservices.provisioningService.baseUrl;
    }


    public Mono<ProvisioningResponseBaseModel> querySubscriberInfo(String accountId) {
        var payload = ProvisioningPayloadModel.Hlr.querySubscriberInfo(accountId);
        return this.webClient
            .postJsonAsync(BASE_URL, payload)
            .flatMap(res -> {
                var data = SerializationWrapper.deserialize(res.getBody(), ProvisioningResponseBaseModel.class);
                return Mono.just(data);
            });
    }
}
