package com.telegram_bot_interactive.controllers;

import com.telegram_bot_interactive.services.chart.ChartService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;

@RestController
@RequestMapping("charts")
public class ChartController {

    private final ChartService chartService;

    public ChartController(ChartService chartService) {
        this.chartService = chartService;
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
 }
