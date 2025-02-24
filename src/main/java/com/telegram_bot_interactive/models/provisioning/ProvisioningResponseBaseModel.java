package com.telegram_bot_interactive.models.provisioning;

import com.telegram_bot_interactive.common.StringHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class ProvisioningResponseBaseModel {

    public Meta meta;
    public Data data;


    public static class Meta {
        @SerializedName("server_correlation_id")
        public String serverCorrelationId;

        @SerializedName("apim_correlation_id")
        public String ApimCorrelationId;
    }

    public static class Data {

        @SerializedName("error_code")
        public String errorCode;

        @SerializedName("error_message")
        public String errorMessage;

        @SerializedName("error_details")
        public String errorDetails;

        @SerializedName("additional_data")
        public JsonObject additionalData;

    }

    public boolean isSuccess() {
        return data != null
            && !StringHelper.isNullOrEmpty(data.errorCode)
            && data.errorCode.equalsIgnoreCase("0000");
    }

    protected boolean isAdditionalDataNotNull() {
        return data != null && data.additionalData != null;
    }

    public String getAdditionalDataAsString(String key) {
        if (isAdditionalDataNotNull()) {
            return data.additionalData.get(key).getAsString();
        }
        return "NULL";
    }
    public JsonObject getAdditionalDataAsObject(String key) {
        if (isAdditionalDataNotNull()) {
            return data.additionalData.getAsJsonObject(key);
        }
        return new JsonObject();
    }
    public JsonArray getAdditionalDataAsJsonArray(String key) {
        if (isAdditionalDataNotNull()) {
            return data.additionalData.getAsJsonArray(key);
        }
        return new JsonArray();
    }
}
