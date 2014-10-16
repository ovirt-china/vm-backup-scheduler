package org.ovirtChina.enginePlugin.vmBackupScheduler.common;

import java.util.HashMap;

public enum BackupMethod {
	Snapshot,
	Export;

	private int value;
    private static final HashMap<Integer, BackupMethod> valueToType = new HashMap<Integer, BackupMethod>();

    static {
        for (BackupMethod status : values()) {
            valueToType.put(status.getValue(), status);
        }
    }

    public int getValue() {
        return value;
    }

    public static BackupMethod forValue(int value) {
        return valueToType.get(value);
    }
}
