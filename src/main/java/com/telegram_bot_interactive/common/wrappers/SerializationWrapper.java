package com.telegram_bot_interactive.common.wrappers;

import com.google.gson.Gson;

public class SerializationWrapper {
    private static final Gson gson = new Gson();

    public static  <T> T deserialize(String json, Class<T> targetClass) {
        return gson.fromJson(json, targetClass);
    }

    public static  <T> T deserialize(Object obj, Class<T> targetClass) {
        return gson.fromJson(serialize(obj), targetClass);
    }

    public static  String serialize(Object obj) {
        return gson.toJson(obj);
    }
}
