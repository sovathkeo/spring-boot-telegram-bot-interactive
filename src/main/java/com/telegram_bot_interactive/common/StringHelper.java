package com.telegram_bot_interactive.common;

public abstract class StringHelper {

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.isBlank();
    }

    public static String getValueOrEmpty(String value) {
        return isNullOrEmpty(value) ? "" : value;
    }

    public static String firstCharToLowerCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        char firstChar = Character.toLowerCase(input.charAt(0));
        if (input.length() == 1) {
            return String.valueOf(firstChar);
        }

        return firstChar + input.substring(1);
    }


}
