package com.telegram_bot_interactive.models.base;

import com.telegram_bot_interactive.services.bot.TelegramBotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Mono;

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

    protected abstract Mono<Void> executeAsync();

    public Mono<Void> executeCommandAsync() {
        return isAdminUser()
            .flatMap(isAdmin -> {

                if (!isAdmin) {
                    log.error("You are not authorized to execute commands.");
                    return Mono.empty();
                }

                return this.executeAsync();
            });
    }

    protected Mono<Void> sendMessageAsync(String text) {
        return this.botService.sendMessageAsync(this.chatId, text);
    }

    protected Mono<Void> sendPhotoAsync(byte[] imageBytes) {
        return this.botService.sendImageAsync(this.chatId, imageBytes);
    }

    protected  Mono<Boolean> isAdminUser() {
        return Mono.just(Objects.equals(adminId, userId));
    }
}
