package com.telegram_bot_interactive.services.chart;

import com.telegram_bot_interactive.common.wrappers.SerializationWrapper;
import com.telegram_bot_interactive.models.ChartDataSetModel;
import com.telegram_bot_interactive.models.ExhaustionChartDatasetModel;
import com.telegram_bot_interactive.repository.StoreProcedureRepository;
import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

@Slf4j
@Service
public class ChartService {

    @Autowired
    private StoreProcedureRepository repository;

    public Mono<byte[]> generateChartFromJson(ChartDataSetModel dataSetModel) {
        return Mono.fromCallable( () -> {
            // Create dataset
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for (var data : dataSetModel.data) {
                dataset.addValue(data.value, data.rowKey, data.columnKey);
            }

            JFreeChart lineChart = ChartFactory.createLineChart(
                "Exhausted Count By Hour",
                "Hour of the Day",
                "Exhausted Count",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
            );

            var plot = lineChart.getCategoryPlot();
            var render = new LineAndShapeRenderer();
            render.setDefaultStroke(new BasicStroke(1.5f));

            plot.setRenderer(render);

            lineChart.setBackgroundPaint(Color.WHITE);
            lineChart.getTitle().setPaint(Color.BLACK);

            // Convert chart to PNG image
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ChartUtils.writeChartAsPNG(outputStream, lineChart, 800, 500);

            return outputStream.toByteArray();
        } )
        .subscribeOn(Schedulers.boundedElastic())
        .onErrorResume(err -> {
            log.error(err.getMessage());
            return Mono.empty();
        });
    }

    public Mono<byte[]> generateExhaustionChartForLastNDays(int lastNumberOfDays) {
        return Mono.fromCallable( () -> {
            var procedureName = "proc_get_exhaustion_data";

            var result = repository.Execute(procedureName, new HashMap<>(){ {put("last_n_days", lastNumberOfDays);} });

            var dataList = SerializationWrapper.deserialize(result.get(), ExhaustionChartDatasetModel[].class);

            var chartDataSet = new ChartDataSetModel();

            for(var dataSet : dataList) {
                for(var data : dataSet.getJsonData()) {
                    chartDataSet.data.add(new ChartDataSetModel.DataModel(data.total, dataSet.date, data.hour));
                }
            }
            return chartDataSet;
        } )
        .subscribeOn(Schedulers.boundedElastic())
            .flatMap(this::generateChartFromJson)
        .onErrorResume(err -> {
            log.error(err.getMessage());
            return Mono.empty();
        });
    }
}
