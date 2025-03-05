package com.telegram_bot_interactive.models.elasticsearch;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.telegram_bot_interactive.common.enums.TimeZones;
import com.telegram_bot_interactive.common.wrappers.DateTimeWrapper;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;

@Data
public class ElasticsearchAggregateResultModel {

    @SerializedName("time_out")
    public boolean timeout;

    @SerializedName("aggregations")
    public Aggregations aggregations = new Aggregations();

    @Data
    public static class Aggregations {

        @SerializedName("by_time")
        public ByTime byTime = new ByTime();

    }

    @Data
    public static class ByTime {

        @SerializedName("buckets")
        public JsonObject[] buckets = new JsonObject[0];

        public Date getKeyDateStringFromBucket(int index) {
            var d =  buckets[index].get("key_as_string").getAsString();
            return DateTimeWrapper.fromString(d, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", TimeZones.Utc.getKey());
        }

        public String getKeyDateStringFromBucket(int index, String format) {
            return DateTimeWrapper.format(getKeyDateStringFromBucket(index), format);
        }
    }
}