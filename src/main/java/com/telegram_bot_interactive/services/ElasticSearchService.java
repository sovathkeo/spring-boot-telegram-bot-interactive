package com.telegram_bot_interactive.services;

import com.telegram_bot_interactive.common.wrappers.DateTimeWrapper;
import com.telegram_bot_interactive.common.wrappers.SerializationWrapper;
import com.telegram_bot_interactive.common.wrappers.WebClientWrapper;
import com.telegram_bot_interactive.configuration.ApplicationConfiguration;
import com.telegram_bot_interactive.models.elasticsearch.ElasticSearchQueryRequestModel;
import com.telegram_bot_interactive.models.elasticsearch.ElasticsearchAggregateResultModel;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ElasticSearchService {

    private final ApplicationConfiguration appSetting;
    private final WebClientWrapper webClientWrapper;

    public ElasticSearchService(WebClientWrapper webClientWrapper, ApplicationConfiguration appSetting) {
        this.webClientWrapper = webClientWrapper;
        this.appSetting = appSetting;
        this.webClientWrapper
            .useBasicAuthentication(appSetting.elasticsearch.username, appSetting.elasticsearch.password);
    }


    /*
    *   Generate graph, breakdown by hour for last n-days and plot as comparison chart
    * */
    public Mono<ElasticsearchAggregateResultModel> queryAggregateByTimeForEachDays(int numberOfDays) {
        var payload = ElasticSearchQueryRequestModel.buildQueryAggregate(numberOfDays);
        var url = appSetting.elasticsearch.buildBusinessSearchUrl();
        return this.webClientWrapper.postJsonAsync(url, payload)
            .flatMap(res -> {
                var result = SerializationWrapper.deserialize(res.getBody(), ElasticsearchAggregateResultModel.class);
                //var d = DateTimeWrapper.format(result.aggregations.byTime.getKeyDate(), "yyyy-MM-dd HH:mm:ss.SSS");
                return Mono.just(result);
            });
    }

}
