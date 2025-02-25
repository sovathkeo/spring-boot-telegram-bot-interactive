package com.telegram_bot_interactive.models;

import com.telegram_bot_interactive.common.enums.TelegramBotMainMenu;
import com.telegram_bot_interactive.common.enums.TelegramBotReplyCommand;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.Update;


public class TelegramCallbackDataModel {

    @Getter
    private Update update;
    public Long userId;
    public Long chatId;
    public String rawData;

    public TelegramCallbackDataModel(String callbackData) {
        this.rawData = callbackData;
    }

    public TelegramCallbackDataModel(Update update) {

        if (update == null) {
            return;
        }
        this.update = update;
        if (update.getCallbackQuery() == null) {
            this.rawData = update.getMessage().getText();
            this.userId = update.getMessage().getFrom().getId();
            this.chatId = update.getMessage().getChatId();
            return;
        }

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

    public boolean hasMessage() {
        return update != null && update.hasMessage() && update.getMessage().hasText();
    }

    public boolean hasCallbackQuery() {
        return this.update.hasCallbackQuery();
    }

    @Override
    public String toString() {
        return this.rawData;
    }
}
