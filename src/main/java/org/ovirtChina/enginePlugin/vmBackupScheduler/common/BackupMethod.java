package org.ovirtChina.enginePlugin.vmBackupScheduler.common;

import java.util.HashMap;

public enum BackupMethod {
	Snapshot(0),
	Export(1);

	private int value;
    private static final HashMap<Integer, BackupMethod> valueToType = new HashMap<Integer, BackupMethod>();

    private BackupMethod(int value) {
        this.value = value;
    }

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
