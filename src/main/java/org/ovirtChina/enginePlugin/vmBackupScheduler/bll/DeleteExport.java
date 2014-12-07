package org.ovirtChina.enginePlugin.vmBackupScheduler.bll;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.ovirt.engine.sdk.decorators.StorageDomain;
import org.ovirt.engine.sdk.exceptions.ServerException;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.Task;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.TaskType;

public class DeleteExport extends DeleteSDKTask {

    public DeleteExport() {
        super(TaskType.CreateExport.getValue());
    }

    @Override
    protected void deleteTask(Task task) throws ClientProtocolException, ServerException, IOException {
        StorageDomain isoDoaminToExport = getIsoDomainToExport();
        if (isoDoaminToExport != null) {
            try{
                isoDoaminToExport.getVMs().getById(task.getBackupName()).delete();
            } catch (ServerException e) {
                deleteTaskRecord(task);
            }
            deleteTaskRecord(task);
            log.info("delete export: " + task.getBackupName() + " for vm: " + task.getVmID() + " has initiated.");
        }
    }

}
