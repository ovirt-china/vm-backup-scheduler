package org.ovirtChina.enginePlugin.vmBackupScheduler.utils;

import org.ovirt.engine.sdk.Api;

public class OVirtEngineSDKUtils {

    public static Api getApi() {
        try {
            return new Api(
                    ConfigProvider.getConfig().getProperty(ConfigProvider.SDK_BASE_URL),
                    ConfigProvider.getConfig().getProperty(ConfigProvider.SDK_USER),
                    ConfigProvider.getConfig().getProperty(ConfigProvider.SDK_PASSWORD),
                    null, 443, 10, true, true, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
