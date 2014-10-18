package org.ovirtChina.enginePlugin.vmBackupScheduler.common;

import java.util.UUID;

public class Task {
    private UUID vmID;
    private int taskType;
    private String backupName;

    public UUID getVmID() {
        return vmID;
    }

    public void setVmID(UUID vmID) {
        this.vmID = vmID;
    }

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public String getBackupName() {
        return backupName;
    }

    public void setBackupName(String backupName) {
        this.backupName = backupName;
    }

    public Task() {
    }

    public Task(UUID vmID, int taskType, String backupName) {
        super();
        this.vmID = vmID;
        this.taskType = taskType;
        this.backupName = backupName;
    }
}
