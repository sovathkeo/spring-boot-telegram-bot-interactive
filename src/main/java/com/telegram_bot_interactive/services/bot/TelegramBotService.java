package com.telegram_bot_interactive.services.bot;

import com.telegram_bot_interactive.common.constants.TelegramMenuConstant;
import com.telegram_bot_interactive.common.enums.TelegramBotMainMenu;
import com.telegram_bot_interactive.common.enums.TelegramBotReplyCommand;
import com.telegram_bot_interactive.feature.exhaustionchart.GenerateExhaustionChartCommand;
import com.telegram_bot_interactive.feature.provisioning.ProvisioningHealthCheckCommand;
import com.telegram_bot_interactive.models.telegram.TelegramCallbackDataModel;
import com.telegram_bot_interactive.models.telegram.TelegramInlineKeyboardButtonModel;
import com.telegram_bot_interactive.models.base.BaseTelegramBotCommand;
import com.telegram_bot_interactive.services.chart.ChartService;
import com.telegram_bot_interactive.services.provisioning.ProvisioningServiceFacade;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayInputStream;
import java.util.*;

@Slf4j
@Service
@NoArgsConstructor
public class TelegramBotService extends TelegramLongPollingBot {

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.admin-id}")
    private Long adminId;

    private final Map<Long, BaseTelegramBotCommand> commands = new HashMap<>();

    @Autowired
    private ChartService chartService;

    @Autowired
    private ProvisioningServiceFacade provisioningService;

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return this.botToken;
    }


    @Override
    public void onUpdateReceived(Update update) {
        Mono.just(new TelegramCallbackDataModel(update))
            .flatMap(callbackData -> {

                if (callbackData.hasMessage()) {

                    if (!this.isAdminUser(callbackData.userId)) {
                        return sendMessageAsync(callbackData.userId, "⛔ You are not authorized to execute commands.");
                    }
                    // any text is ignored, available only menu
                    return showMainMenuAsync(callbackData.userId);

                }

                if (callbackData.hasCallbackQuery()) {

                    if (callbackData.isConfirm()) {
                        return handleConfirmationAsync(update);
                    } else {
                        return handleMenuSelectionAsync(callbackData);
                    }
                }

                return Mono.empty();
            })
            .onErrorResume(error -> {
                log.error("Error: {}", error.getMessage());
                return Mono.empty();
            })
            .subscribe();
    }

    private boolean isAdminUser(Long userId) {
        return Objects.equals(this.adminId, userId);
    }

    private Mono<Void> sendConfirmationRequestAsync(TelegramCallbackDataModel callbackData) {

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        var confirmButton = this.buildButton("✅ Confirm", TelegramBotReplyCommand.Confirm.name());

        var cancelButton = this.buildButton("❌ Cancel", TelegramBotReplyCommand.Canceled.name());

        keyboard.add(Collections.singletonList(confirmButton));
        keyboard.add(Collections.singletonList(cancelButton));

        markup.setKeyboard(keyboard);

        return sendMessageAsync(callbackData.chatId, "⚠️ Please Confirm To Proceed The Command!\n\n`" + callbackData.rawData + "`", markup);
    }

    private Mono<Void> handleConfirmationAsync(Update update) {
        return Mono.just(new TelegramCallbackDataModel(update))
            .flatMap(data -> {

                if (!commands.containsKey(data.userId)) {
                    return sendMessageAsync(data.chatId, "⛔ No pending command found!");
                }

                if (data.isConfirm()) {
                    var command = this.commands.get(data.userId);
                    return command.executeCommandAsync();
                }
                return sendMessageAsync(data.chatId, "❌ Command execution canceled.");
            });
    }

    public Mono<Void> sendMessageAsync(Long chatId, String text) {
        return sendMessageAsync(chatId, text, null);
    }

    public Mono<Void> sendImageAsync(Long chatId, byte[] imageBytes) {
        return Mono.fromCallable( () -> {

            var file = new InputFile(new ByteArrayInputStream(imageBytes), "image.jpg");

            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(chatId);
            sendPhoto.setPhoto(file);
            try {
                execute(sendPhoto);
            } catch (TelegramApiException e) {
                return null;
            }
            return  null;
        })
        .subscribeOn(Schedulers.boundedElastic())
        .onErrorResume(err -> {
            log.error(err.getMessage());
            return Mono.empty();
        })
        .then();
    }

    private Mono<Void> sendMessageAsync(Long chatId, String text, InlineKeyboardMarkup markup) {
        return Mono.fromRunnable(() -> {
            SendMessage message = new SendMessage();
            message.setChatId(chatId.toString());
            message.setText(text);
            message.setParseMode("Markdown");
            if (markup != null) {
                message.setReplyMarkup(markup);
            }

            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error("Error Executing message[{}]", message, e);
            }
        })
            .subscribeOn(Schedulers.boundedElastic())
            .then();
    }

    private Mono<Void> showMainMenuAsync(Long chatId) {

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        keyboard.add(buildButtonKeyboard(TelegramMenuConstant.ExhaustionMenu));
        keyboard.add(buildButtonKeyboard(TelegramMenuConstant.MainServicesHealthCheck));
        keyboard.add(buildButtonKeyboard(TelegramMenuConstant.ProvisioningHealthCheck));

        markup.setKeyboard(keyboard);
        return sendMessageAsync(chatId, "📌 Select an option:", markup);
    }

    private Mono<Void> handleMenuSelectionAsync(TelegramCallbackDataModel callbackData) {
        return Mono.just(new TelegramCallbackDataModel(callbackData.getUpdate()))
            .flatMap( data -> {
                if (data.isMenu()) {
                    return this.showSubMenuAsync(data);
                }
                this.commands.put(data.userId, this.buildCommand(callbackData));
                // Send confirmation
                return this.sendConfirmationRequestAsync(data);
            })
            .onErrorResume(err -> {
                log.error("Error Handle Menu Selection", err);
                return Mono.empty();
            });
    }

    private Mono<Void> showSubMenuAsync(TelegramCallbackDataModel callbackDataModel) {

        if (callbackDataModel.getMenuEnum() == TelegramBotMainMenu.ExhaustionChart) {

            // show sub-menu of exhaustion chart generation
            var markup = this.buildExhaustionChartKeyboardMarkup();
            return this.sendMessageAsync(callbackDataModel.chatId, callbackDataModel.getMenu() + ", Options :", markup);
        }

        return Mono.empty();
    }

    private InlineKeyboardMarkup buildExhaustionChartKeyboardMarkup() {

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        keyboard.add(buildButtonKeyboard(TelegramMenuConstant.ExhaustionChartMenuConstant.TodayElastic));
        keyboard.add(buildButtonKeyboard(TelegramMenuConstant.ExhaustionChartMenuConstant.Last2DayElastic));
        keyboard.add(buildButtonKeyboard(TelegramMenuConstant.ExhaustionChartMenuConstant.Last3DayElastic));

        keyboard.add(buildButtonKeyboard(TelegramMenuConstant.ExhaustionChartMenuConstant.Today));
        keyboard.add(buildButtonKeyboard(TelegramMenuConstant.ExhaustionChartMenuConstant.Last2Day));
        keyboard.add(buildButtonKeyboard(TelegramMenuConstant.ExhaustionChartMenuConstant.Last3Day));

        markup.setKeyboard(keyboard);
        return markup;
    }

    private InlineKeyboardButton buildButton(String title, String callbackData) {
        var btn =  new InlineKeyboardButton();
        btn.setText(title);
        btn.setCallbackData(callbackData);
        return btn;
    }

    private InlineKeyboardButton buildButton(TelegramInlineKeyboardButtonModel button) {

        var btn =  new InlineKeyboardButton();
        btn.setText(button.title);

        var callbackData = new TelegramCallbackDataModel(button.callbackData);

        btn.setCallbackData(callbackData.toString());
        return btn;
    }

    private List<InlineKeyboardButton> buildButtonKeyboard(TelegramInlineKeyboardButtonModel button) {
        return Collections.singletonList(buildButton(button));
    }

    private BaseTelegramBotCommand buildCommand(TelegramCallbackDataModel callbackData) {

        var menu = callbackData.getMenuEnum();

        if (menu == TelegramBotMainMenu.ExhaustionChart) {
            return new GenerateExhaustionChartCommand(this.chartService, this, callbackData.getUpdate(), this.adminId);
        } else if (menu == TelegramBotMainMenu.ProvisioningHealthCheck) {
            return new ProvisioningHealthCheckCommand(this, this.provisioningService, callbackData.getUpdate(), this.adminId);
        }

        return null;
    }
}
