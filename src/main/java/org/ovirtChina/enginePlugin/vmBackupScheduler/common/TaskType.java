package org.ovirtChina.enginePlugin.vmBackupScheduler.common;

import java.util.HashMap;

public enum TaskType {
    CreateSnapshot(0),
    CreateExport(1),
    DeleteSnapshot(2),
    DeleteExport(3);

    private int value;
    private static final HashMap<Integer, TaskType> valueToType = new HashMap<Integer, TaskType>();

    private TaskType(int value) {
        this.value = value;
    }

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
