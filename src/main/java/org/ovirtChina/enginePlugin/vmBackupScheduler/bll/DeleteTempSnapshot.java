package org.ovirtChina.enginePlugin.vmBackupScheduler.bll;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.ovirt.engine.sdk.Api;
import org.ovirt.engine.sdk.decorators.VM;
import org.ovirt.engine.sdk.exceptions.ServerException;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.Task;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.TaskStatus;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.TaskType;
import org.ovirtChina.enginePlugin.vmBackupScheduler.dao.DbFacade;

public class DeleteTempSnapshot extends TimerSDKTask {

    public DeleteTempSnapshot(Api api) {
        super(api);
    }

    @Override
    protected void peformAction() throws Exception {
        if (api != null) {
            findAndDelete();
        }
    }

    private void findAndDelete() throws ClientProtocolException, ServerException, IOException {
        Task task = DbFacade.getInstance()
                .getTaskDAO().getOldestTaskTypeWithStatus(TaskType.DeleteTmpSnapshot, TaskStatus.FINISHED);
        if (task != null) {
            VM vm =  api.getVMs().get(task.getVmID());
            if (vm.getStatus().getState().equals("down")) {
                try{
                    vm.getSnapshots().getById(task.getBackupName()).delete();
                } catch(ServerException e) {
                    setTaskStatus(task, TaskStatus.DELETED);
                }
                setTaskStatus(task, TaskStatus.DELETED);
            }
            findAndDelete();
        }
    }

}
