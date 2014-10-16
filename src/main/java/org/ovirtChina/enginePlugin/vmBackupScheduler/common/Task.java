package org.ovirtChina.enginePlugin.vmBackupScheduler.common;

import java.util.UUID;

public class Task {
    private UUID vmID;
    private TaskType taskType;
    private String backupName;

    public UUID getVmID() {
        return vmID;
    }

    public void setVmID(UUID vmID) {
        this.vmID = vmID;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public String getBackupName() {
        return backupName;
    }

    public void setBackupName(String backupName) {
        this.backupName = backupName;
    }

    public Task(UUID vmID, TaskType taskType, String backupName) {
        super();
        this.vmID = vmID;
        this.taskType = taskType;
        this.backupName = backupName;
    }
}
