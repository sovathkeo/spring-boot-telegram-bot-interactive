package com.telegram_bot_interactive.feature.provisioning;

import com.telegram_bot_interactive.models.base.BaseTelegramBotCommand;
import com.telegram_bot_interactive.services.bot.TelegramBotService;
import com.telegram_bot_interactive.services.provisioning.ProvisioningServiceFacade;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

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
    protected void execute() {

        var text = "❌ Provisioning Service is Down!!!!!!";
        try {
            var result = this.provisioningService.ocs
                .querySubscriberInfo("85599204681")
                .block();
            assert result != null;

            if (result.isSuccess()) {
                text = "✅ Provisioning Service is Healthy";
            }

        } catch (Exception e) {
            text = "⚠️ Error From Provisioning : \n" + e.getMessage();
        }

        this.sendMessage(text);
    }
}
