package org.ovirtChina.enginePlugin.vmBackupScheduler.bll;

import java.io.IOException;
import java.util.UUID;

import org.apache.http.client.ClientProtocolException;
import org.ovirt.engine.sdk.decorators.VM;
import org.ovirt.engine.sdk.entities.Snapshot;
import org.ovirt.engine.sdk.exceptions.ServerException;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.Task;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.TaskStatus;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.TaskType;
import org.ovirtChina.enginePlugin.vmBackupScheduler.dao.DbFacade;
import org.ovirtChina.enginePlugin.vmBackupScheduler.utils.OVirtEngineSDKUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecuteSnapshot extends TimerSDKTask {
    private static Logger log = LoggerFactory.getLogger(ExecuteSnapshot.class);

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
                Snapshot snap = new Snapshot();
                VM vm = api.getVMs().get(taskToExec.getVmID());
                snap.setVm(vm);
                snap.setDescription("autoSnap");
                String snapshotId = vm.getSnapshots().add(snap).getId();
                taskToExec.setBackupName(snapshotId);
                setTaskStatus(taskToExec, TaskStatus.EXECUTING);
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

    private void querySnapshot(Task taskToExec, VM vm) throws ClientProtocolException, ServerException, IOException, InterruptedException {
        while(!api.getVMs().get(UUID.fromString(vm.getId())).getSnapshots().getById(taskToExec.getBackupName()).getSnapshotStatus().equals("ok")) {
            log.info("vm: " + vm.getName() + " is snapshoting, waiting for next query...");
            Thread.sleep(5000);
        }
    }
}
