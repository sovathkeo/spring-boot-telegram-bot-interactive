package com.telegram_bot_interactive.models;

import com.telegram_bot_interactive.common.wrappers.SerializationWrapper;
import com.google.gson.annotations.SerializedName;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ExhaustionChartDatasetModel {

    @SerializedName("date")
    public String date;

    @SerializedName("json_data")
    public String jsonData;

    public HourTotalModel[] getJsonData() {
        return SerializationWrapper.deserialize(this.jsonData, HourTotalModel[].class);
    }
}
