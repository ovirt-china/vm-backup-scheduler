package org.ovirtChina.enginePlugin.vmBackupScheduler.common;

import java.util.UUID;

public class VmPolicy {
    private UUID vmID;
    private BackupMethod backupMethod;
    private TimeOfDay timeOfDay;
    private WeekDays weekDays;
    private AutoDeleteReservePolicy autoDeleteReservePolicy;
    private int autoDeleteReserveAmount;

    public UUID getVmID() {
        return vmID;
    }

    public void setVmID(UUID vmID) {
        this.vmID = vmID;
    }

    public BackupMethod getBackupMethod() {
        return backupMethod;
    }

    public void setBackupMethod(BackupMethod backupMethod) {
        this.backupMethod = backupMethod;
    }

    public String getTimeOfDay() {
        return timeOfDay.toString();
    }

    public void setTimeOfDay(String timeOfDay) {
        this.timeOfDay = TimeOfDay.parseTimeOfDay(timeOfDay);
    }

    public String getWeekDays() {
        return weekDays.toString();
    }

    public void setWeekDays(String weekDays) {
        this.weekDays = WeekDays.parseWeeekDays(weekDays);
    }

    public AutoDeleteReservePolicy getAutoDeleteReservePolicy() {
        return autoDeleteReservePolicy;
    }

    public void setAutoDeleteReservePolicy(
            AutoDeleteReservePolicy autoDeleteReservePolicy) {
        this.autoDeleteReservePolicy = autoDeleteReservePolicy;
    }

    public int getAutoDeleteReserveAmount() {
        return autoDeleteReserveAmount;
    }

    public void setAutoDeleteReserveAmount(int autoDeleteReserveAmount) {
        this.autoDeleteReserveAmount = autoDeleteReserveAmount;
    }

    public VmPolicy(UUID vmID, BackupMethod backupMethod, TimeOfDay timeOfDay,
            WeekDays weekDays, AutoDeleteReservePolicy autoDeleteReservePolicy,
            int autoDeleteReserveAmount) {
        super();
        this.vmID = vmID;
        this.backupMethod = backupMethod;
        this.timeOfDay = timeOfDay;
        this.weekDays = weekDays;
        this.autoDeleteReservePolicy = autoDeleteReservePolicy;
        this.autoDeleteReserveAmount = autoDeleteReserveAmount;
    }
}
