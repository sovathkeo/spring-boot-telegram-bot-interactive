package com.telegram_bot_interactive.feature.exhaustionchart;

import com.telegram_bot_interactive.common.enums.ExhaustionCommands;
import com.telegram_bot_interactive.models.base.BaseTelegramBotCommand;
import com.telegram_bot_interactive.services.bot.TelegramBotService;
import com.telegram_bot_interactive.services.chart.ChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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
    public Mono<Void> executeAsync() {
        return this.getNumberOfDay(command)
            .flatMap(day ->
                this.sendMessageAsync("Collecting Exhaustion Data For Last %s Days. Please Wait.....".formatted(day))
                    .then(
                        isElkCommand(command)
                            ? chartService.generateExhaustionChartForLastNDaysELK(day)
                            : chartService.generateExhaustionChartForLastNDays(day)
                    )
                    .flatMap(this::sendPhotoAsync)

            );
    }

    private Mono<Integer> getNumberOfDay(ExhaustionCommands command) {
        return Mono.just(switch (command) {
            case Today, TodayElk -> 1;
            case Last2Day, Last2DayElk -> 2;
            case Last3Day, Last3DayElk -> 3;
        });
    }

    private boolean isElkCommand(ExhaustionCommands command) {
        return (command == ExhaustionCommands.TodayElk ||
            command == ExhaustionCommands.Last2DayElk ||
            command == ExhaustionCommands.Last3DayElk);
    }
}
