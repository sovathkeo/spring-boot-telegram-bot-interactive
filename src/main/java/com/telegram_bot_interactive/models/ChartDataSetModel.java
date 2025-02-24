package com.telegram_bot_interactive.models;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public class ChartDataSetModel {

    //dataset.addValue(1, "Today", "00");
    public List<DataModel> data = new ArrayList<>();

    @AllArgsConstructor
    public static class DataModel {
        public int value;
        public String rowKey;
        public String columnKey;
    }
}


