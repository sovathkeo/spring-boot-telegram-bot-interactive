package com.telegram_bot_interactive.models.exhaustion;

import com.telegram_bot_interactive.common.wrappers.SerializationWrapper;
import com.google.gson.annotations.SerializedName;
import com.telegram_bot_interactive.models.chart.HourTotalModel;
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
