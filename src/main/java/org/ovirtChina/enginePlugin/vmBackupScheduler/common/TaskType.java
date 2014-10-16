package org.ovirtChina.enginePlugin.vmBackupScheduler.common;

import java.util.HashMap;

public enum TaskType {
    CreateSnapshot,
    CreateExport,
    DeleteSnapshot,
    DeleteExport;

    private int value;
    private static final HashMap<Integer, TaskType> valueToType = new HashMap<Integer, TaskType>();

    static {
        for (TaskType status : values()) {
            valueToType.put(status.getValue(), status);
        }
    }

    public int getValue() {
        return value;
    }

    public static TaskType forValue(int value) {
        return valueToType.get(value);
    }
}
