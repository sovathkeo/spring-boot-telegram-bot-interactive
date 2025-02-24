package com.telegram_bot_interactive.feature.exhaustionchart;

import com.telegram_bot_interactive.common.enums.ExhaustionCommands;
import com.telegram_bot_interactive.models.base.BaseTelegramBotCommand;
import com.telegram_bot_interactive.services.bot.TelegramBotService;
import com.telegram_bot_interactive.services.chart.ChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;

@Component
public class GenerateExhaustionChartCommand extends BaseTelegramBotCommand {

    @Autowired
    private ChartService chartService;

    private final ExhaustionCommands command;

    public GenerateExhaustionChartCommand() {
        super(new TelegramBotService(), 0L, new Update());
        this.command = null;
    }

    public GenerateExhaustionChartCommand(
        ChartService chartService,
        TelegramBotService botService,
        Update update,
        Long adminId) {

        super(botService, adminId, update);

        var cmd = update.getCallbackQuery().getData();
        var cmds = cmd.split(";");
        this.command = ExhaustionCommands.valueOf(cmds[2]);
        this.chartService = chartService;
    }

    @Override
    public void execute() {

        var day = this.getNumberOfDay(command);
        try {

            this.sendMessage("Collecting Exhaustion Data For Last %s Days. Please Wait.....".formatted(day));
            var image = this.chartService.generateExhaustionChartForLastNDays(day);
            this.sendPhoto(image);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int getNumberOfDay(ExhaustionCommands command) {
        return switch (command) {
            case Today -> 1;
            case Last2Day -> 2;
            case Last3Day -> 3;
        };
    }
}
