package org.ovirtChina.enginePlugin.vmBackupScheduler.common;

import java.util.UUID;

public class VmPolicy {
    private UUID vmID;
    private boolean enabled;
    private int backupMethod;
    private String timeOfDay;
    private String weekDays;
    private int autoDeleteReservePolicy;
    private int autoDeleteReserveAmount;

    public UUID getVmID() {
        return vmID;
    }

    public void setVmID(UUID vmID) {
        this.vmID = vmID;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getBackupMethod() {
        return backupMethod;
    }

    public void setBackupMethod(int backupMethod) {
        this.backupMethod = backupMethod;
    }

    public String getTimeOfDay() {
        return timeOfDay.toString();
    }

    public void setTimeOfDay(String timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    public String getWeekDays() {
        return weekDays.toString();
    }

    public void setWeekDays(String weekDays) {
        this.weekDays = weekDays;
    }

    public int getAutoDeleteReservePolicy() {
        return autoDeleteReservePolicy;
    }

    public void setAutoDeleteReservePolicy(
            int autoDeleteReservePolicy) {
        this.autoDeleteReservePolicy = autoDeleteReservePolicy;
    }

    public int getAutoDeleteReserveAmount() {
        return autoDeleteReserveAmount;
    }

    public void setAutoDeleteReserveAmount(int autoDeleteReserveAmount) {
        this.autoDeleteReserveAmount = autoDeleteReserveAmount;
    }

    public VmPolicy() {
    }

    public VmPolicy(UUID vmID, boolean enabled, int backupMethod, String timeOfDay,
            String weekDays, int autoDeleteReservePolicy,
            int autoDeleteReserveAmount) {
        super();
        this.vmID = vmID;
        this.enabled = enabled;
        this.backupMethod = backupMethod;
        this.timeOfDay = timeOfDay;
        this.weekDays = weekDays;
        this.autoDeleteReservePolicy = autoDeleteReservePolicy;
        this.autoDeleteReserveAmount = autoDeleteReserveAmount;
    }
}
