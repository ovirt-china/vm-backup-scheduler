package org.ovirtChina.enginePlugin.vmBackupScheduler.common;

import java.util.UUID;

public class Task {
    private UUID vmID;
    private int taskStatus;
    private int taskType;
    private String backupName;

    public UUID getVmID() {
        return vmID;
    }

    public void setVmID(UUID vmID) {
        this.vmID = vmID;
    }

    public int getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
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

    public Task(UUID vmID, int taskStatus, int taskType, String backupName) {
        super();
        this.vmID = vmID;
        this.taskStatus = taskStatus;
        this.taskType = taskType;
        this.backupName = backupName;
    }
}
