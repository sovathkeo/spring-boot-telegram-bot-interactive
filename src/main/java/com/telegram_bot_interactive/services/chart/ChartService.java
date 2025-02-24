package com.telegram_bot_interactive.services.chart;

import com.telegram_bot_interactive.common.wrappers.SerializationWrapper;
import com.telegram_bot_interactive.models.ChartDataSetModel;
import com.telegram_bot_interactive.models.ExhaustionChartDatasetModel;
import com.telegram_bot_interactive.repository.StoreProcedureRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

@Service
public class ChartService {

    @Autowired
    private StoreProcedureRepository repository;


    private final ObjectMapper mapper = new ObjectMapper();

    public byte[] generateChartFromJson(ChartDataSetModel dataSetModel) throws IOException {

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
    }

    public byte[] generateExhaustionChartForLastNDays(int lastNumberOfDays) throws IOException {

        var procedureName = "proc_get_exhaustion_data";

        var result = repository.Execute(procedureName, new HashMap<>(){ {put("last_n_days", lastNumberOfDays);} });

        var dataList = SerializationWrapper.deserialize(result.get(), ExhaustionChartDatasetModel[].class);

        var chartDataSet = new ChartDataSetModel();

        for(var dataSet : dataList) {
            for(var data : dataSet.getJsonData()) {
                chartDataSet.data.add(new ChartDataSetModel.DataModel(data.total, dataSet.date, data.hour));
            }
        }

        return generateChartFromJson(chartDataSet);
    }
}
