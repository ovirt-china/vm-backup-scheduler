package org.ovirtChina.enginePlugin.vmBackupScheduler.bll;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.ovirt.engine.sdk.Api;
import org.ovirt.engine.sdk.decorators.VM;
import org.ovirt.engine.sdk.exceptions.ServerException;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.EngineEventSeverity;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.Task;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.TaskStatus;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.TaskType;
import org.ovirtChina.enginePlugin.vmBackupScheduler.dao.DbFacade;
import org.slf4j.LoggerFactory;

public class ExecuteSnapshot extends TimerSDKTask {

    public ExecuteSnapshot(Api api) {
        super(api);
        log = LoggerFactory.getLogger(ExecuteSnapshot.class);
    }

    protected void peformAction() throws ClientProtocolException, ServerException, IOException, InterruptedException {
        Task taskToExec = DbFacade.getInstance().getTaskDAO().getOldestTaskTypeWithStatus(TaskType.CreateSnapshot, TaskStatus.EXECUTING);
        if (taskToExec == null) {
            taskToExec = DbFacade.getInstance().getTaskDAO().getOldestTaskTypeWithStatus(TaskType.CreateSnapshot, TaskStatus.WAITING);
        }
        if (taskToExec == null) {
            taskToExec = DbFacade.getInstance().getTaskDAO().getOldestTaskTypeWithStatus(TaskType.CreateSnapshot, TaskStatus.RETRYING);
        }
        if (taskToExec == null) {
            log.debug("There is no snapshot task to execute.");
            return;
        } else if (System.currentTimeMillis() - taskToExec.getCreateTime().getTime() > taskTimeoutMin * 60000L) {
            log.warn("Task: " + TaskType.forValue(taskToExec.getTaskType()) + " for vm: "
                    + taskToExec.getVmID() + " has timed out, set to failed status.");
            setTaskStatus(taskToExec, TaskStatus.FAILED);
            return;
        }
        if (api != null) {
            if (taskToExec.getTaskStatus() == TaskStatus.WAITING.getValue()
                    || taskToExec.getTaskStatus() == TaskStatus.RETRYING.getValue()) {
                VM vm = createSnapshot(taskToExec, "Auto Backup");
                if (vm == null) {
                    return;
                }
                try {
                    querySnapshot(taskToExec, vm);
                } catch (InterruptedException e) {
                    log.error("Error while snapshoting vm: " + vm.getName(), e);
                    setTaskStatus(taskToExec, TaskStatus.FAILED);
                }
                setTaskStatus(taskToExec, TaskStatus.FINISHED);
                String message = "Execution of task Snapshot for vm: " + vm.getName() + " succeeded.";
                log.info(message);
                addEngineEvent(EngineEventSeverity.normal, message);
            }
        }
    }
}
