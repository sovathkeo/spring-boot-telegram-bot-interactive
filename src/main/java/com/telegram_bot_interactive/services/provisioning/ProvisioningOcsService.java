package com.telegram_bot_interactive.services.provisioning;


import com.telegram_bot_interactive.common.wrappers.SerializationWrapper;
import com.telegram_bot_interactive.common.wrappers.WebClientWrapper;
import com.telegram_bot_interactive.configuration.ApplicationConfiguration;
import com.telegram_bot_interactive.models.provisioning.ProvisioningPayloadModel;
import com.telegram_bot_interactive.models.provisioning.ocs.ProvisioningOcsResponseModel;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ProvisioningOcsService {

    @Autowired
    ApplicationConfiguration appSetting;

    private String BASE_URL ;

    @Autowired
    private WebClientWrapper webClient;

    @PostConstruct
    private void init() {
        BASE_URL = appSetting.microservices.provisioningService.baseUrl;
        this.webClient.setREQUEST_TIME_OUT(appSetting.microservices.provisioningService.requestTimeoutMillisecond);
    }

    public Mono<ProvisioningOcsResponseModel> querySubscriberInfo(String accountId) {

        var subscriberInfoPayload = ProvisioningPayloadModel.OcsPayloadModel.OcsSubscriberInfo(accountId);
        return this.webClient
            .postJsonAsync(BASE_URL, subscriberInfoPayload)
            .flatMap(res -> {
                var data = SerializationWrapper.deserialize(res.getBody(), ProvisioningOcsResponseModel.class);
                return Mono.just(data);
            });
    }

    public Mono<ProvisioningOcsResponseModel> queryBundle(String accountId) {
        var subscriberInfoPayload = ProvisioningPayloadModel.OcsPayloadModel.queryBundles(accountId);
        return this.webClient
            .postJsonAsync(BASE_URL, subscriberInfoPayload)
            .flatMap(res -> {
                var data = SerializationWrapper.deserialize(res.getBody(), ProvisioningOcsResponseModel.class);
                return Mono.just(data);
            });
    }
}
