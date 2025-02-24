package com.telegram_bot_interactive.models;

import com.telegram_bot_interactive.common.enums.TelegramBotMainMenu;
import com.telegram_bot_interactive.common.enums.TelegramBotReplyCommand;
import org.telegram.telegrambots.meta.api.objects.Update;


public class TelegramCallbackDataModel {

    public Long userId;
    public Long chatId;
    public String rawData;

    public TelegramCallbackDataModel(String callbackData) {
        this.rawData = callbackData;
    }

    public TelegramCallbackDataModel(Update update) {
        this.rawData = update.getCallbackQuery().getData();
        this.userId = update.getCallbackQuery().getFrom().getId();
        this.chatId = update.getCallbackQuery().getMessage().getChatId();
    }

    public boolean isMenu() {
        return this.rawData.startsWith("Menu;");
    }

    public boolean isCommand() {
        return this.rawData.startsWith("Command;");
    }

    public boolean isConfirm() {
        return "CONFIRM".equalsIgnoreCase(this.rawData);
    }

    public String getMenu() {
        return this.rawData.split(";")[1];
    }

    public TelegramBotMainMenu getMenuEnum() {
        try {
            return TelegramBotMainMenu.valueOf(this.getMenu());
        } catch (Exception ignored) {
            return TelegramBotMainMenu.None;
        }
    }

    public TelegramBotReplyCommand getCommandEnum() {
        try {
            return TelegramBotReplyCommand.valueOf(this.rawData);
        } catch (Exception ignored) {
            return TelegramBotReplyCommand.None;
        }
    }

    public String getCommand() {
        return this.rawData.split(";")[2];
    }

    @Override
    public String toString() {
        return this.rawData;
    }
}
