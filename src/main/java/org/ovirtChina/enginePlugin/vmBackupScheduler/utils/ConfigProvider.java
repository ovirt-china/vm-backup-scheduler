package org.ovirtChina.enginePlugin.vmBackupScheduler.utils;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;

public class ConfigProvider {
    static private Logger log = org.slf4j.LoggerFactory.getLogger(ConfigProvider.class);
    private static Properties config = null;

    public static final String SDK_BASE_URL = "org.ovirtChina.enginePlugin.vmBackupScheduler.engineSdkBaseUrl";
    public static final String SDK_USER = "org.ovirtChina.enginePlugin.vmBackupScheduler.engineSdkUser";
    public static final String SDK_PASSWORD = "org.ovirtChina.enginePlugin.vmBackupScheduler.engineSdkPassword";

    public static Properties getConfig() {
        if (config == null) {
            config = new Properties();
            try {
                config.load(new FileReader("/etc/ovirt-vm-backup/ovirt-vm-backup.properties"));
            } catch (IOException ex) {
                log.error("error reading config file.", ex);
            }
        }
        return config;
    }
}
