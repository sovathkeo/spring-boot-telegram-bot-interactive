package com.telegram_bot_interactive.controllers;

import com.telegram_bot_interactive.models.elasticsearch.ElasticSearchQueryRequestModel;
import com.telegram_bot_interactive.models.elasticsearch.ElasticsearchAggregateResultModel;
import com.telegram_bot_interactive.services.ElasticSearchService;
import com.telegram_bot_interactive.services.chart.ChartService;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.IOException;

@RestController
@RequestMapping(value = "charts", produces = MediaType.APPLICATION_JSON_VALUE)
public class ChartController {

    private final ChartService chartService;

    private final ElasticSearchService elasticSearchService;

    public ChartController(ChartService chartService, ElasticSearchService elasticSearchService) {
        this.chartService = chartService;
        this.elasticSearchService = elasticSearchService;
    }

    @PostMapping("/generate")
    public Mono<ResponseEntity<byte[]>> generateChartFromJson(@RequestParam int day) throws IOException {
        return chartService
            .generateExhaustionChartForLastNDays(day)
            .flatMap(img -> Mono.just(ResponseEntity.ok()
                                                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=chart.png")
                                                .contentType(MediaType.IMAGE_PNG)
                                                .body(img))
            );
    }

    @GetMapping("elastic/generate-payload")
    public Mono<String> generateElasticPayload(@RequestParam int numberOfDays) {
        return Mono.just(ElasticSearchQueryRequestModel.buildQueryAggregate(numberOfDays));
    }

    @GetMapping("/test")
    public Mono<ResponseEntity<byte[]>> test(@RequestParam int numberOfDays) {
        return chartService.generateExhaustionChartForLastNDaysELK(numberOfDays)
            .flatMap(img -> Mono.just(ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=chart.png")
                .contentType(MediaType.IMAGE_PNG)
                .body(img))
            );
    }
 }
