package org.ovirtChina.enginePlugin.vmBackupScheduler.bll;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;
import java.util.UUID;

import org.apache.http.client.ClientProtocolException;
import org.ovirt.engine.sdk.Api;
import org.ovirt.engine.sdk.decorators.StorageDomain;
import org.ovirt.engine.sdk.decorators.VM;
import org.ovirt.engine.sdk.decorators.VMDisk;
import org.ovirt.engine.sdk.entities.Action;
import org.ovirt.engine.sdk.entities.Snapshots;
import org.ovirt.engine.sdk.exceptions.ServerException;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.EngineEventSeverity;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.Task;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.TaskStatus;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.TaskType;
import org.ovirtChina.enginePlugin.vmBackupScheduler.dao.DbFacade;
import org.ovirtChina.enginePlugin.vmBackupScheduler.utils.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecuteExport extends TimerSDKTask {

    public ExecuteExport(Api api) {
		super(api);
	}

	private static Logger log = LoggerFactory.getLogger(TimerTask.class);
    DateFormat df = new SimpleDateFormat("MM-dd-yyyy_HH-mm-ss");

    protected void peformAction() throws ClientProtocolException, ServerException, IOException, InterruptedException {
        Task taskToExec = DbFacade.getInstance().getTaskDAO().getOldestTaskTypeWithStatus(TaskType.CreateExport, TaskStatus.EXECUTING);
        if (taskToExec == null) {
            taskToExec = DbFacade.getInstance().getTaskDAO().getOldestTaskTypeWithStatus(TaskType.CreateExport, TaskStatus.WAITING);
        }
        if (taskToExec == null) {
            taskToExec = DbFacade.getInstance().getTaskDAO().getOldestTaskTypeWithStatus(TaskType.CreateExport, TaskStatus.RETRYING);
        }
        if (taskToExec == null) {
            log.debug("There is no export task to execute.");
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
                VM vm = api.getVMs().get(taskToExec.getVmID());
                VM vmCopy = null;

                if (vm.getStatus().getState().equals("down")) {
                    vmCopy = copyVm(taskToExec, vm);
                    if (vmCopy == null) {
                        return;
                    }
                } else {
                    VM vmFrom = createSnapshot(taskToExec, "temp");
                    if (vmFrom == null) {
                        return;
                    }
                    Date now = new Date();
                    Task deleteTmpTask = new Task(UUID.fromString(vm.getId()), TaskStatus.EXECUTING.getValue(),
                            TaskType.DeleteTmpSnapshot.getValue(), taskToExec.getBackupName(), now, now);
                    DbFacade.getInstance().getTaskDAO().save(deleteTmpTask);
                    try {
                        querySnapshot(taskToExec, vmFrom);
                    } catch (InterruptedException e) {
                        log.error("Error while snapshoting vm: " + vmFrom.getName(), e);
                        setTaskStatus(deleteTmpTask, TaskStatus.FAILED);
                        setTaskStatus(taskToExec, TaskStatus.FAILED);
                    }
                    setTaskStatus(deleteTmpTask, TaskStatus.FINISHED);
                    vmCopy = cloneVmFromSnapshot(vmFrom, taskToExec);
                }

                taskToExec.setBackupName(vmCopy.getId());
                setTaskStatus(taskToExec, TaskStatus.EXECUTING);
                try{
                    queryVmForDown(vmCopy, "Copying");
                } catch (Exception e) {
                    log.error("Error while copying vm: " + vmCopy.getName(), e);
                    setTaskStatus(taskToExec, TaskStatus.FAILED);
                    deleteVmCopy(vmCopy);
                    return;
                }
                Action action = new Action();
                action.setStorageDomain(getIsoDomainToExport(vm));
                if (action.getStorageDomain() == null) {
                    String message = "There is no export domain in the data center of vm: " + vm.getName() + ", aborting export backup.";
                    log.error(message);
                    addEngineEvent(EngineEventSeverity.error, message);
                    setTaskStatus(taskToExec, TaskStatus.FAILED);
                    deleteVmCopy(vmCopy);
                    return;
                }
                log.info("Start executing task Export for vm: " + vmCopy.getName());
                api.getVMs().get(vmCopy.getName()).exportVm(action);
                try{
                    queryVmForDown(vmCopy, "Exporting");
                } catch (Exception e) {
                    log.error("Error while exporting vm: " + vmCopy.getName(), e);
                    setTaskStatus(taskToExec, TaskStatus.FAILED);
                    return;
                } finally {
                    deleteVmCopy(vmCopy);
                }
                setTaskStatus(taskToExec, TaskStatus.FINISHED);
                String message = "Execution of task Export for vm: " + vm.getName() + " succeeded.";
                log.info(message);
                addEngineEvent(EngineEventSeverity.normal, message);
            }
        }
    }

    private VM cloneVmFromSnapshot(VM vmFrom, Task taskToExec) throws ClientProtocolException, ServerException, IOException {
        VM clone = new VM(null);
        clone.setName(getCopyVmNme(vmFrom));
        clone.setCluster(vmFrom.getCluster());
        Snapshots snapsshots = new Snapshots();
        snapsshots.getSnapshots().add(vmFrom.getSnapshots().getById(taskToExec.getBackupName()));
        clone.setSnapshots(snapsshots);
        clone = api.getVMs().add(clone);
        log.info("clone vm from snapshot of vm: " + vmFrom.getId() + " has initiated.");
        return clone;
    }

    private void queryVmForDown(VM vmCopy, String action) throws ClientProtocolException, ServerException, IOException, InterruptedException {
        while(!api.getVMs().get(vmCopy.getName()).getStatus().getState().equals("down")) {
            log.info("vm: " + vmCopy.getName() + " is " + action + ", waiting for next query...");
            Thread.sleep(interval);
        }
        boolean copyingDisks = true;
        while(copyingDisks) {
            copyingDisks = false;
            List<VMDisk> disks = api.getVMs().get(vmCopy.getName()).getDisks().list();
            for(VMDisk disk : disks) {
                if(!disk.getStatus().getState().equals("ok")) {
                    copyingDisks = true;
                    log.info("vm: " + vmCopy.getName() + "'s disk " + disk.getName() + " is being cloned, waiting for next query...");
                    Thread.sleep(interval);
                    break;
                }
            }
        }
    }

    private void deleteVmCopy(VM vmCopy) throws ClientProtocolException, ServerException, IOException {
        api.getVMs().get(vmCopy.getName()).delete();
        log.info("vm: " + vmCopy.getName() + " has deleted.");
    }

    private VM copyVm(Task taskToExec, VM vm) throws ClientProtocolException, ServerException, IOException, InterruptedException {
        VM copyVm = new VM(null);
        String copyVmName = getCopyVmNme(vm);
        copyVm.setName(copyVmName);
        Action action = new Action();
        action.setVm(copyVm);
        try{
            api.getVMs().getById(vm.getId()).clone(action);
        } catch (ServerException e) {
            if (new Date().getTime() - taskToExec.getCreateTime().getTime() <
                    Long.parseLong(ConfigProvider.getConfig().getProperty(ConfigProvider.SNAPSHOT_DELAY_MIN)) * 60000L) {
                log.warn("clone of vm: " + vm.getName() + " has failed, will try in next schedule.");
                setTaskStatus(taskToExec, TaskStatus.RETRYING);
            } else {
                log.error("clone of vm: " + vm.getName() + " has failed and exceeded delay config, mark as failed.");
                setTaskStatus(taskToExec, TaskStatus.FAILED);
            }
            return null;
        }
        copyVm = api.getVMs().get(copyVmName);
        log.info("copying vm: " + vm.getName() + " for export...");

        return copyVm;
    }

    private String getCopyVmNme(VM vm) {
        return vm.getName() + "_Backup_" + df.format(new Date());
    }

}
