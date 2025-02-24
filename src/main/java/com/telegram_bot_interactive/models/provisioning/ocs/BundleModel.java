package com.telegram_bot_interactive.models.provisioning.ocs;

import com.telegram_bot_interactive.common.StringHelper;
import com.telegram_bot_interactive.common.constants.OcsDateTimeConstant;
import com.telegram_bot_interactive.common.wrappers.DateTimeWrapper;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

public class BundleModel {

    @SerializedName("bundle_id")
    public String bundleId = "";

    @SerializedName("state")
    public String state = "";
    @SerializedName("start_date_time")
    public String startDateTime = "";
    @SerializedName("end_date_time")
    public String endDateTime = "";

    @SerializedName("buckets")
    public ArrayList<Bucket> buckets;


    private String getStartDateTimeAsString() {
        return this.populateDateTimeToFullLength(this.startDateTime);
    }

    public Date getStartDatetimeAsDate() {
        if (StringHelper.isNullOrEmpty(this.startDateTime)) {
            return DateTimeWrapper.defaultDate();
        }

        return DateTimeWrapper.fromString(getStartDateTimeAsString(), OcsDateTimeConstant.OCS_DATETIME_FORMAT);
    }

    public String getStartDatetimeAsString() {
        return DateTimeWrapper.toString(getStartDatetimeAsDate(), "yyyy-MM-dd HH:mm:ss");
    }

    public String getStartTimeAsString() {
        return this.getEndTimeAsString();
    }

    public String getStartDateAsString(){
        return DateTimeWrapper.toString(this.getStartDatetimeAsDate(), OcsDateTimeConstant.OCS_DATE_FORMAT);
    }

    public String getEndDateTime() {
        return populateDateTimeToFullLength(this.endDateTime);
    }

    public Date getEndDatetimeAsDate() {
        if (StringHelper.isNullOrEmpty(this.endDateTime)) {
            return DateTimeWrapper.defaultDate();
        }
        return DateTimeWrapper.fromString(getEndDateTime(), OcsDateTimeConstant.OCS_DATETIME_FORMAT);
    }

    public String getEndDateTimeAsString(String format) {
        if (StringHelper.isNullOrEmpty(format)) {
            format = "yyyy-MM-dd HH:mm:ss.SSS";
        }
        return DateTimeWrapper.toString(this.getEndDatetimeAsDate(), format);
    }

    public String getEndDateAsString() {
        return DateTimeWrapper.toString(this.getEndDatetimeAsDate(), OcsDateTimeConstant.OCS_DATE_FORMAT);
    }

    public String getEndTimeAsString(){
        return DateTimeWrapper.toString(this.getEndDatetimeAsDate(), OcsDateTimeConstant.OCS_TIME_FORMAT);
    }


    private String populateDateTimeToFullLength(String dateToPopulate) {
        var missedLength = OcsDateTimeConstant.OCS_DATETIME_LENGTH - dateToPopulate.length();
        if (missedLength < 1) {
            return dateToPopulate;
        }
        return populateDateTimeToFullLength(dateToPopulate.concat("0"));
    }


    public static class Bucket {
        @SerializedName("bucket_id")
        public String bucketId = "";
        @SerializedName("value")
        public String value = "";
        @SerializedName("type")
        public String type = "";
    }
}
