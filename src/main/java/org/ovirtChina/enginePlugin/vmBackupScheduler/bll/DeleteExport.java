package org.ovirtChina.enginePlugin.vmBackupScheduler.bll;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.ovirt.engine.sdk.Api;
import org.ovirt.engine.sdk.decorators.StorageDomain;
import org.ovirt.engine.sdk.decorators.StorageDomainVM;
import org.ovirt.engine.sdk.exceptions.ServerException;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.EngineEventSeverity;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.Task;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.TaskType;

public class DeleteExport extends DeleteOldBackupSDKTask {

    public DeleteExport(Api api) {
        super(api, TaskType.CreateExport.getValue());
    }

    @Override
    protected void deleteTask(Task task) throws ClientProtocolException, ServerException, IOException, InterruptedException {
        StorageDomain isoDoaminToExport = getIsoDomainToExport();
        StorageDomainVM vm = null;
        if (isoDoaminToExport != null) {
            String message = "delete CreateExport backup: " + vm.getName() + " for vm: " + api.getVMs().get(task.getVmID()) + " has initiated.";
            try{
                vm = isoDoaminToExport.getVMs().getById(task.getBackupName());
                vm.delete();
            } catch (ServerException e) {
                deleteTaskRecord(EngineEventSeverity.normal, message, task);
            }
            deleteTaskRecord(EngineEventSeverity.normal, message, task);
        }
    }



}
