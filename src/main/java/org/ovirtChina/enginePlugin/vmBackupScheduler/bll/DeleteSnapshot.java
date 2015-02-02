package org.ovirtChina.enginePlugin.vmBackupScheduler.bll;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.ovirt.engine.sdk.Api;
import org.ovirt.engine.sdk.decorators.VM;
import org.ovirt.engine.sdk.exceptions.ServerException;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.EngineEventSeverity;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.Task;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.TaskType;

public class DeleteSnapshot extends DeleteOldBackupSDKTask {

    public DeleteSnapshot(Api api) {
        super(api, TaskType.CreateSnapshot.getValue());
    }

    @Override
    protected void deleteTask(Task task) throws ClientProtocolException, ServerException, IOException, InterruptedException {
        VM vm = api.getVMs().get(task.getVmID());
        if (vm.getStatus().getState().equals("down")) {
            String message = "delete CreateSnapshot backup for vm: " + vm.getName() + " has initiated.";
            try{
                vm.getSnapshots().getById(task.getBackupName()).delete();
            } catch (ServerException e) {
                deleteTaskRecord(EngineEventSeverity.normal, message, task);
            }
            deleteTaskRecord(EngineEventSeverity.normal, message, task);
        } else {
            log.debug("cancel deletion of snapshot: " + task.getBackupName() + " for vm: " + task.getVmID() + ", because vm is not down.");
        }
    }

}
