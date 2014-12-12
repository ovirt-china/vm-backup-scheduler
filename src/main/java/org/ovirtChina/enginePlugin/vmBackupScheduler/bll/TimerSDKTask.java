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
import org.ovirtChina.enginePlugin.vmBackupScheduler.utils.ConfigProvider;
import org.slf4j.Logger;

public abstract class TimerSDKTask extends TimerTask {

    protected static Logger log;
    protected static Api api = null;
    protected int interval = 10000;

    public TimerSDKTask(Api api1) {
        super();
        api = api1;
        interval = Integer.parseInt(ConfigProvider.getConfig().getProperty(ConfigProvider.QUERY_INTERVAL_M));
    }

    @Override
    public void run() {
        try {
            peformAction();
        } catch (Exception e) {
            e.printStackTrace();
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
            Thread.sleep(interval);
        }
    }

    protected VM createSnapshot(Task taskToExec, String desc) throws ClientProtocolException, ServerException, IOException {
        Snapshot snap = new Snapshot();
        VM vm = api.getVMs().get(taskToExec.getVmID());
        snap.setVm(vm);
        snap.setDescription(desc);
        String snapshotId = null;
        try{
            snapshotId = vm.getSnapshots().add(snap).getId();
        } catch (ServerException e) {
            if (new Date().getTime() - taskToExec.getCreateTime().getTime() <
                    Long.parseLong(ConfigProvider.getConfig().getProperty(ConfigProvider.SNAPSHOT_DELAY_MIN)) * 60000L) {
                log.warn("snapshot: " + desc + " of vm: " + vm.getName() + " has failed, will try in next schedule.");
                setTaskStatus(taskToExec, TaskStatus.RETRYING);
            } else {
                log.error("snapshot: " + desc + " of vm: " + vm.getName() + " has failed and exceeded delay config, mark as failed.");
                setTaskStatus(taskToExec, TaskStatus.FAILED);
            }
            return null;
        }
        taskToExec.setBackupName(snapshotId);
        setTaskStatus(taskToExec, TaskStatus.EXECUTING);
        return vm;
    }

    protected abstract void peformAction() throws Exception;

}
