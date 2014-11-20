package org.ovirtChina.enginePlugin.vmBackupScheduler.bll;

import java.io.IOException;
import java.util.Date;
import java.util.TimerTask;

import org.apache.http.client.ClientProtocolException;
import org.ovirt.engine.sdk.Api;
import org.ovirt.engine.sdk.decorators.StorageDomain;
import org.ovirt.engine.sdk.decorators.VM;
import org.ovirt.engine.sdk.entities.Action;
import org.ovirt.engine.sdk.exceptions.ServerException;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.BackupMethod;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.Task;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.TaskStatus;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.TaskType;
import org.ovirtChina.enginePlugin.vmBackupScheduler.dao.DbFacade;
import org.ovirtChina.enginePlugin.vmBackupScheduler.utils.OVirtEngineSDKUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecuteExport extends TimerTask {
    private static Logger log = LoggerFactory.getLogger(TimerTask.class);

    @Override
    public void run() {
        Api api = null;
        try {
            peformAction(api);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (api != null) {
                try {
                    api.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void peformAction(Api api) throws ClientProtocolException, ServerException, IOException, InterruptedException {
        Task taskToExec = DbFacade.getInstance().getTaskDAO().getOldestTaskTypeWithStatus(TaskType.CreateExport, TaskStatus.EXECUTING);
        if (taskToExec == null) {
            taskToExec = DbFacade.getInstance().getTaskDAO().getOldestTaskTypeWithStatus(TaskType.CreateExport, TaskStatus.WAITING);
        }
        if (taskToExec == null) {
            log.info("There is no export task to execute.");
            return;
        }
        api = OVirtEngineSDKUtils.getApi();
        if (api != null) {
            StorageDomain isoDoaminToExport = getIsoDomainToExport(api);
            if (isoDoaminToExport != null) {
                if (taskToExec.getTaskStatus() == TaskStatus.WAITING.getValue()) {
                    VM vm = api.getVMs().get(taskToExec.getVmID());
                    if (vm.getStatus().getState().equals("down")) {
                        Action action = new Action();
                        action.setStorageDomain(isoDoaminToExport);
                        log.info("Start executing task" + BackupMethod.forValue(taskToExec.getTaskType()) + " for vm: " + vm.getName());
                        vm.exportVm(action);
                        setTaskStatus(taskToExec, TaskStatus.EXECUTING);
                        try{
                            queryExport(api, taskToExec, vm);
                        } catch (Exception e) {
                            log.error("Error while exporting vm: " + vm.getName(), e);
                            setTaskStatus(taskToExec, TaskStatus.FAILED);
                        }
                        setTaskStatus(taskToExec, TaskStatus.FINISHED);
                        log.info("Execution of task" + BackupMethod.forValue(taskToExec.getTaskType()) + " for vm: " + vm.getName() + " succeeded.");
                    } else {
                        setTaskStatus(taskToExec, TaskStatus.FAILED);
                        log.warn("Exporting vm failed, vm: " + vm.getId() + " is not down.");
                    }

                }
            }
        }
    }

    private void setTaskStatus(Task taskToExec, TaskStatus taskStatus) {
        taskToExec.setTaskStatus(taskStatus.getValue());
        taskToExec.setLastUpdate(new Date());
        DbFacade.getInstance().getTaskDAO().update(taskToExec);
    }

    private void queryExport(Api api, Task taskToExec, VM vm) throws ClientProtocolException, ServerException, IOException, InterruptedException {
        while (!api.getVMs().get(taskToExec.getVmID()).getStatus().getState().equals("down")) {
            log.info("vm: " + vm.getName() + " is exporting, waiting for next query...");
            Thread.sleep(5000);
        }
    }

    private StorageDomain getIsoDomainToExport(Api api) throws ClientProtocolException, ServerException, IOException {
        for (StorageDomain sd : api.getStorageDomains().list()) {
            if (sd.getType().equals("export")) {
                return sd;
            }
        }
        return null;
    }

}
