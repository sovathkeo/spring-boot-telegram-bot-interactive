package com.telegram_bot_interactive.models.provisioning.ocs;

import com.telegram_bot_interactive.common.StringHelper;
import com.telegram_bot_interactive.common.wrappers.SerializationWrapper;
import com.telegram_bot_interactive.models.provisioning.ProvisioningResponseBaseModel;
import jakarta.annotation.Nullable;

import java.util.Arrays;
import java.util.Optional;

public class ProvisioningOcsResponseModel extends ProvisioningResponseBaseModel {

    public Optional<BundleModel> getBundle(@Nullable  String name) {
        if (isNoBundle()) {
            return Optional.empty();
        }
        if (StringHelper.isNullOrEmpty(name)) {
            return Optional.of(this.getBundles()[0]);
        }
        return Arrays.stream(this.getBundles())
            .filter(b -> b.bundleId.equalsIgnoreCase(name))
            .findFirst();
    }

    public BundleModel[] getBundles() {
        if (isAdditionalDataNotNull()) {
            return SerializationWrapper.deserialize(
                SerializationWrapper.serialize(data.additionalData.getAsJsonArray("bundles")),
                BundleModel[].class);
        }
        return new BundleModel[0];
    }

    public boolean isNoBundle() {
        return data == null || data.additionalData == null || getBundles().length < 1;
    }
}