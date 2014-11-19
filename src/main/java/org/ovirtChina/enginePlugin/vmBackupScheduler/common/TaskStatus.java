package org.ovirtChina.enginePlugin.vmBackupScheduler.common;

import java.util.HashMap;

public enum TaskStatus {
    WAITING(0),
    EXECUTING(1),
    FINISHED(2),
    FAILED(3);

    private int value;
    private static final HashMap<Integer, TaskStatus> valueToType = new HashMap<Integer, TaskStatus>();

    private TaskStatus(int value) {
        this.value = value;
    }

    static {
        for (TaskStatus status : values()) {
            valueToType.put(status.getValue(), status);
        }
    }

    public int getValue() {
        return value;
    }

    public static TaskStatus forValue(int value) {
        return valueToType.get(value);
    }
}
