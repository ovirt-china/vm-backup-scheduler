package org.ovirtChina.enginePlugin.vmBackupScheduler.bll;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.ovirt.engine.sdk.decorators.VM;
import org.ovirt.engine.sdk.exceptions.ServerException;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.Task;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.TaskType;

public class DeleteSnapshot extends DeleteSDKTask {

    public DeleteSnapshot() {
        super(TaskType.CreateSnapshot.getValue());
    }

    @Override
    protected void deleteTask(Task task) throws ClientProtocolException, ServerException, IOException {
        VM vm = api.getVMs().get(task.getVmID());
        if (vm.getStatus().getState().equals("down")) {
            try{
                vm.getSnapshots().getById(task.getBackupName()).delete();
            } catch (ServerException e) {
                deleteTaskRecord(task);
            }
            deleteTaskRecord(task);
            log.info("delete snapshot: " + task.getBackupName() + " for vm: " + task.getVmID() + " has initiated.");
        } else {
            log.debug("cancel deletion of snapshot: " + task.getBackupName() + " for vm: " + task.getVmID() + ", because vm is not down.");
        }
    }

}
