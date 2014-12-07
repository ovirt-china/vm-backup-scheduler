package org.ovirtChina.enginePlugin.vmBackupScheduler.bll;

import java.io.IOException;
import java.util.Date;
import java.util.TimerTask;
import java.util.UUID;

import org.apache.http.client.ClientProtocolException;
import org.ovirt.engine.sdk.Api;
import org.ovirt.engine.sdk.decorators.StorageDomain;
import org.ovirt.engine.sdk.decorators.VM;
import org.ovirt.engine.sdk.entities.Snapshot;
import org.ovirt.engine.sdk.exceptions.ServerException;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.Task;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.TaskStatus;
import org.ovirtChina.enginePlugin.vmBackupScheduler.dao.DbFacade;
import org.slf4j.Logger;

public abstract class TimerSDKTask extends TimerTask {

    protected static Logger log;
    protected Api api = null;

    @Override
    public void run() {
        try {
            peformAction();
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

    protected void setTaskStatus(Task taskToExec, TaskStatus taskStatus) {
        taskToExec.setTaskStatus(taskStatus.getValue());
        taskToExec.setLastUpdate(new Date());
        DbFacade.getInstance().getTaskDAO().update(taskToExec);
    }

    protected StorageDomain getIsoDomainToExport() throws ClientProtocolException, ServerException, IOException {
        for (StorageDomain sd : api.getStorageDomains().list()) {
            if (sd.getType().equals("export")) {
                return sd;
            }
        }
        return null;
    }

    protected void querySnapshot(Task taskToExec, VM vm) throws ClientProtocolException, ServerException, IOException, InterruptedException {
        while(!api.getVMs().get(UUID.fromString(vm.getId())).getSnapshots().getById(taskToExec.getBackupName()).getSnapshotStatus().equals("ok")) {
            log.info("vm: " + vm.getName() + " is snapshoting, waiting for next query...");
            Thread.sleep(5000);
        }
    }

    protected VM createSnapshot(Task taskToExec) throws ClientProtocolException, ServerException, IOException {
        Snapshot snap = new Snapshot();
        VM vm = api.getVMs().get(taskToExec.getVmID());
        snap.setVm(vm);
        snap.setDescription("autoSnap");
        String snapshotId = vm.getSnapshots().add(snap).getId();
        taskToExec.setBackupName(snapshotId);
        setTaskStatus(taskToExec, TaskStatus.EXECUTING);
        return vm;
    }

    protected abstract void peformAction() throws Exception;

}
