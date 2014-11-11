package org.ovirtChina.enginePlugin.vmBackupScheduler.common;

import java.util.HashMap;

public enum TaskStatus {
    WAITING,
    EXECUTING,
    FINISHED,
    FAILED;

    private int value;
    private static final HashMap<Integer, TaskStatus> valueToType = new HashMap<Integer, TaskStatus>();

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
