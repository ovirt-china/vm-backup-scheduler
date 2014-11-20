package org.ovirtChina.enginePlugin.vmBackupScheduler.bll;

import java.util.Date;
import java.util.TimerTask;

import org.ovirt.engine.sdk.Api;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.Task;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.TaskStatus;
import org.ovirtChina.enginePlugin.vmBackupScheduler.dao.DbFacade;

public abstract class TimerSDKTask extends TimerTask {

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

    protected void setTaskStatus(Task taskToExec, TaskStatus taskStatus) {
        taskToExec.setTaskStatus(taskStatus.getValue());
        taskToExec.setLastUpdate(new Date());
        DbFacade.getInstance().getTaskDAO().update(taskToExec);
    }

    protected abstract void peformAction(Api api) throws Exception;

}
