package com.telegram_bot_interactive.services.provisioning;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProvisioningServiceFacade {

    @Autowired
    public ProvisioningOcsService ocs;
}