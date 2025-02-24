package com.telegram_bot_interactive.controllers;

import com.telegram_bot_interactive.common.wrappers.SerializationWrapper;
import com.telegram_bot_interactive.models.ChartDataSetModel;
import com.telegram_bot_interactive.models.ExhaustionChartDatasetModel;
import com.telegram_bot_interactive.repository.StoreProcedureRepository;
import com.telegram_bot_interactive.services.chart.ChartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;

@RestController
@RequestMapping("charts")
public class ChartController {

    private final ObjectMapper mapper = new ObjectMapper();
    private final ChartService chartService;
    private final StoreProcedureRepository repository;

    public ChartController(ChartService chartService, StoreProcedureRepository repository) {
        this.chartService = chartService;
        this.repository = repository;
    }

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generateChartFromJson(@RequestBody String jsonData) throws IOException {

        var procedureName = "proc_get_exhaustion_data";

        var result = repository.Execute(procedureName, new HashMap<>(){ {put("last_n_days", "1");} });

        var dataList = SerializationWrapper.deserialize(result.get(), ExhaustionChartDatasetModel[].class);

        var chartDataSet = new ChartDataSetModel();

        for(var dataSet : dataList) {
            for(var data : dataSet.getJsonData()) {
                chartDataSet.data.add(new ChartDataSetModel.DataModel(data.total, dataSet.date, data.hour));
            }
        }

        byte[] chartImage = chartService.generateChartFromJson(chartDataSet);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=chart.png")
            .contentType(MediaType.IMAGE_PNG)
            .body(chartImage);
    }
 }
