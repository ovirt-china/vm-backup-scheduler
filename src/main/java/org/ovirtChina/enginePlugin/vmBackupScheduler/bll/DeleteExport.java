package org.ovirtChina.enginePlugin.vmBackupScheduler.bll;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.ovirt.engine.sdk.decorators.StorageDomain;
import org.ovirt.engine.sdk.exceptions.ServerException;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.Task;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.TaskType;
import org.ovirtChina.enginePlugin.vmBackupScheduler.dao.DbFacade;

public class DeleteExport extends DeleteSDKTask {

    public DeleteExport() {
        super(TaskType.CreateExport.getValue());
    }

    @Override
    protected void deleteTask(Task task) throws ClientProtocolException, ServerException, IOException {
        StorageDomain isoDoaminToExport = getIsoDomainToExport();
        if (isoDoaminToExport != null) {
            isoDoaminToExport.getVMs().getById(task.getBackupName()).delete();
            DbFacade.getInstance().getTaskDAO().delete(task.getVmID(), task.getBackupName());
            log.info("delete export: " + task.getBackupName() + " for vm: " + task.getVmID() + " has initiated.");
        }
    }

}
