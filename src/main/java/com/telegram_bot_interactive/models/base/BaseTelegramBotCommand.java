package com.telegram_bot_interactive.models.base;

import com.telegram_bot_interactive.services.bot.TelegramBotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Objects;

@Slf4j
@Component
public abstract class BaseTelegramBotCommand {

    private final Long adminId;
    public Long userId;
    public Long chatId;

    private final TelegramBotService botService;

    protected BaseTelegramBotCommand(TelegramBotService botService, Long adminId, Update update) {

        this.adminId = adminId;
        this.botService = botService;

        if (update != null && update.getCallbackQuery() != null) {
            this.userId = update.getCallbackQuery().getFrom().getId();
            this.chatId = update.getCallbackQuery().getMessage().getChatId();
        }

    }

    protected abstract void execute();

    public void executeCommand() {
        if (!isAdminUser()) {
            log.error("You are not authorized to execute commands.");
            return;
        }
        this.execute();
    }

    protected void sendMessage(String text) {
        this.botService.sendMessage(this.chatId, text);
    }

    protected void sendPhoto(byte[] imageBytes) {
        this.botService.sendImage(this.chatId, imageBytes);
    }

    protected  boolean isAdminUser() {
        return Objects.equals(adminId, userId);
    }
}
