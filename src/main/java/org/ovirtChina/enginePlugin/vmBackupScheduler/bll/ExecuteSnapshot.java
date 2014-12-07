package org.ovirtChina.enginePlugin.vmBackupScheduler.bll;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.ovirt.engine.sdk.decorators.VM;
import org.ovirt.engine.sdk.exceptions.ServerException;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.Task;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.TaskStatus;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.TaskType;
import org.ovirtChina.enginePlugin.vmBackupScheduler.dao.DbFacade;
import org.ovirtChina.enginePlugin.vmBackupScheduler.utils.OVirtEngineSDKUtils;
import org.slf4j.LoggerFactory;

public class ExecuteSnapshot extends TimerSDKTask {

    public ExecuteSnapshot() {
        log = LoggerFactory.getLogger(ExecuteSnapshot.class);
    }

    protected void peformAction() throws ClientProtocolException, ServerException, IOException {
        Task taskToExec = DbFacade.getInstance().getTaskDAO().getOldestTaskTypeWithStatus(TaskType.CreateSnapshot, TaskStatus.EXECUTING);
        if (taskToExec == null) {
            taskToExec = DbFacade.getInstance().getTaskDAO().getOldestTaskTypeWithStatus(TaskType.CreateSnapshot, TaskStatus.WAITING);
        }
        if (taskToExec == null) {
            log.debug("There is no snapshot task to execute.");
            return;
        }
        api = OVirtEngineSDKUtils.getApi();
        if (api != null) {
            if (taskToExec.getTaskStatus() == TaskStatus.WAITING.getValue()) {
                VM vm = createSnapshot(taskToExec);
                try {
                    querySnapshot(taskToExec, vm);
                } catch (InterruptedException e) {
                    log.error("Error while snapshoting vm: " + vm.getName(), e);
                    setTaskStatus(taskToExec, TaskStatus.FAILED);
                }
                setTaskStatus(taskToExec, TaskStatus.FINISHED);
            }
        }
    }
}
