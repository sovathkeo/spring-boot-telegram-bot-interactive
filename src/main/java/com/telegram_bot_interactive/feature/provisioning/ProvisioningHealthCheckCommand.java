package com.telegram_bot_interactive.feature.provisioning;

import com.telegram_bot_interactive.models.base.BaseTelegramBotCommand;
import com.telegram_bot_interactive.services.bot.TelegramBotService;
import com.telegram_bot_interactive.services.provisioning.ProvisioningServiceFacade;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Mono;

@Slf4j
public class ProvisioningHealthCheckCommand extends BaseTelegramBotCommand {

    private final ProvisioningServiceFacade provisioningService;

    public ProvisioningHealthCheckCommand(
        TelegramBotService botService,
        ProvisioningServiceFacade provisioningService,
        Update update,
        Long adminId) {

        super(botService, adminId, update);
        this.provisioningService = provisioningService;
    }

    @Override
    protected Mono<Void> executeAsync() {
        return this.provisioningService.ocs
            .querySubscriberInfo("85599204681")
            .flatMap(result -> {

                var text = "❌ Provisioning Service is Down!!!!!!";

                if (result.isSuccess()) {
                    text = "✅ Provisioning Service is Healthy";
                }
                return this.sendMessageAsync(text);
            })
            .onErrorResume(err -> {
                log.error(err.getMessage());
                return Mono.empty();
            });
    }
}
