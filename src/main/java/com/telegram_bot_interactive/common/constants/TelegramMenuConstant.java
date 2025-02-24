package com.telegram_bot_interactive.common.constants;

import com.telegram_bot_interactive.common.enums.ExhaustionCommands;
import com.telegram_bot_interactive.common.enums.TelegramBotMainMenu;
import com.telegram_bot_interactive.models.TelegramInlineKeyboardButtonModel;

public abstract class TelegramMenuConstant {

    public static TelegramInlineKeyboardButtonModel ExhaustionMenu = new TelegramInlineKeyboardButtonModel(
        "Exhaustion Chart",
        "Menu;" + TelegramBotMainMenu.ExhaustionChart
    );

    public static TelegramInlineKeyboardButtonModel ProvisioningHealthCheck = new TelegramInlineKeyboardButtonModel(
        "Provisioning Health Check",
        "Command;" + TelegramBotMainMenu.ProvisioningHealthCheck
    );

    public static TelegramInlineKeyboardButtonModel MainServicesHealthCheck = new TelegramInlineKeyboardButtonModel(
        "Main Services Health Check",
        "Menu;" + TelegramBotMainMenu.MainServicesHealthCheck
    );

    public static class ExhaustionChartMenuConstant {

        public static TelegramInlineKeyboardButtonModel Today = new TelegramInlineKeyboardButtonModel(
            "Today", "Command;%s;%s".formatted(TelegramBotMainMenu.ExhaustionChart, ExhaustionCommands.Today)
        );
        public static TelegramInlineKeyboardButtonModel Last2Day = new TelegramInlineKeyboardButtonModel(
            "Last 2 Day", "Command;%s;%s".formatted(TelegramBotMainMenu.ExhaustionChart, ExhaustionCommands.Last2Day)
        );
        public static TelegramInlineKeyboardButtonModel Last3Day = new TelegramInlineKeyboardButtonModel(
            "Last 3 Day", "Command;%s;%s".formatted(TelegramBotMainMenu.ExhaustionChart, ExhaustionCommands.Last3Day)
        );
    }

    public static class ProvisioningMenuConstant {
        public static TelegramInlineKeyboardButtonModel HealthCheck = new TelegramInlineKeyboardButtonModel(
            "Today", "Command;%s;%s".formatted(TelegramBotMainMenu.ExhaustionChart, ExhaustionCommands.Today)
        );
    }
}
