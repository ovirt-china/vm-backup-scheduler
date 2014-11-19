package org.ovirtChina.enginePlugin.vmBackupScheduler.common;

import java.util.HashMap;

public enum AutoDeleteReservePolicy {
    Quantity(0),
    Day(1);

    private int value;
    private static final HashMap<Integer, AutoDeleteReservePolicy> valueToType = new HashMap<Integer, AutoDeleteReservePolicy>();

    private AutoDeleteReservePolicy(int value) {
        this.value = value;
    }

    static {
        for (AutoDeleteReservePolicy status : values()) {
            valueToType.put(status.getValue(), status);
        }
    }

    public int getValue() {
        return value;
    }

    public static AutoDeleteReservePolicy forValue(int value) {
        return valueToType.get(value);
    }
}
