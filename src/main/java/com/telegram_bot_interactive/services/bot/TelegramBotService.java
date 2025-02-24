package com.telegram_bot_interactive.services.bot;

import com.telegram_bot_interactive.common.constants.TelegramMenuConstant;
import com.telegram_bot_interactive.common.enums.TelegramBotMainMenu;
import com.telegram_bot_interactive.common.enums.TelegramBotReplyCommand;
import com.telegram_bot_interactive.feature.exhaustionchart.GenerateExhaustionChartCommand;
import com.telegram_bot_interactive.feature.provisioning.ProvisioningHealthCheckCommand;
import com.telegram_bot_interactive.models.TelegramCallbackDataModel;
import com.telegram_bot_interactive.models.TelegramInlineKeyboardButtonModel;
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

        if (update.hasMessage() && update.getMessage().hasText()) {

            Long userId = update.getMessage().getFrom().getId();

            if (!this.isAdminUser(userId)) {
                sendMessage(userId, "⛔ You are not authorized to execute commands.");
                return;
            }

            showMainMenu(userId);

        } else if (update.hasCallbackQuery()) {

            var callbackData = new TelegramCallbackDataModel(update);

            if (callbackData.isConfirm()) {
                handleConfirmation(update);
            } else {
                handleMenuSelection(update);
            }

        }
    }

    private boolean isAdminUser(Long userId) {
        return Objects.equals(this.adminId, userId);
    }

    private void sendConfirmationRequest(Long chatId, String command) {

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        var confirmButton = this.buildButton("✅ Confirm", TelegramBotReplyCommand.Confirm.name());

        var cancelButton = this.buildButton("❌ Cancel", TelegramBotReplyCommand.Canceled.name());

        keyboard.add(Collections.singletonList(confirmButton));
        keyboard.add(Collections.singletonList(cancelButton));

        markup.setKeyboard(keyboard);

        sendMessage(chatId, "⚠️ Please Confirm To Proceed The Command!\n\n`" + command + "`", markup);
    }

    private void handleConfirmation(Update update) {

        var callbackData = new TelegramCallbackDataModel(update);
        Long userId = update.getCallbackQuery().getFrom().getId();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        if (!commands.containsKey(userId)) {
            sendMessage(chatId, "⛔ No pending command found!");
            return;
        }

        if (callbackData.isConfirm()) {
            var command = this.commands.get(userId);
            command.executeCommand();
        } else {
            sendMessage(chatId, "❌ Command execution canceled.");
        }
    }

    public void sendMessage(Long chatId, String text) {
        sendMessage(chatId, text, null);
    }

    public void sendImage(Long chatId, byte[] imageBytes) {

        var file = new InputFile(new ByteArrayInputStream(imageBytes), "image.jpg");

        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(file);
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMessage(Long chatId, String text, InlineKeyboardMarkup markup) {

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
            log.error("Error Executing message[%s]".formatted(message),e);
        }
    }

    private void showMainMenu(Long chatId) {

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        keyboard.add(buildButtonKeyboard(TelegramMenuConstant.ExhaustionMenu));
        keyboard.add(buildButtonKeyboard(TelegramMenuConstant.MainServicesHealthCheck));
        keyboard.add(buildButtonKeyboard(TelegramMenuConstant.ProvisioningHealthCheck));

        markup.setKeyboard(keyboard);
        sendMessage(chatId, "📌 Select an option:", markup);

    }

    private void handleMenuSelection(Update update) {

        try {

            String callbackData = update.getCallbackQuery().getData();
            var callbackModel = new TelegramCallbackDataModel(update);

            Long userId = update.getCallbackQuery().getFrom().getId();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (callbackModel.isMenu()) {
                // show sub-menu
                this.showSubMenu(update);
                return;
            }
            if (callbackModel.isCommand()) {
                // Store command in queue
                this.commands.put(userId, this.buildCommand(update));
                // Send confirmation
                this.sendConfirmationRequest(chatId, callbackData);
            }

        } catch (Exception ignored) {}
    }

    private void showSubMenu(Update update) {

        Long userId = update.getCallbackQuery().getFrom().getId();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        var callbackData = new TelegramCallbackDataModel(update);

        if (callbackData.getMenuEnum() == TelegramBotMainMenu.ExhaustionChart) {

            // show sub-menu of exhaustion chart generation
            var markup = this.buildExhaustionChartKeyboardMarkup();
            this.sendMessage(chatId, callbackData.getMenu() + ", Options :", markup);

        }
    }

    private InlineKeyboardMarkup buildExhaustionChartKeyboardMarkup() {

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

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

    private BaseTelegramBotCommand buildCommand(Update update) {

        var callbackData = update.getCallbackQuery().getData();
        var commands = callbackData.split(";");
        var menu = TelegramBotMainMenu.valueOf(commands[1]);

        if (menu == TelegramBotMainMenu.ExhaustionChart) {
            return new GenerateExhaustionChartCommand(this.chartService, this, update, this.adminId);
        } else if (menu == TelegramBotMainMenu.ProvisioningHealthCheck) {
            return new ProvisioningHealthCheckCommand(this, this.provisioningService, update, this.adminId);
        }

        return null;
    }
}
