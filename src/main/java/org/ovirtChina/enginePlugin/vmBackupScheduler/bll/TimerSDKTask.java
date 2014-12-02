package org.ovirtChina.enginePlugin.vmBackupScheduler.bll;

import java.io.IOException;
import java.util.Date;
import java.util.TimerTask;

import org.apache.http.client.ClientProtocolException;
import org.ovirt.engine.sdk.Api;
import org.ovirt.engine.sdk.decorators.StorageDomain;
import org.ovirt.engine.sdk.exceptions.ServerException;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.Task;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.TaskStatus;
import org.ovirtChina.enginePlugin.vmBackupScheduler.dao.DbFacade;

public abstract class TimerSDKTask extends TimerTask {

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

    protected abstract void peformAction() throws Exception;

}
