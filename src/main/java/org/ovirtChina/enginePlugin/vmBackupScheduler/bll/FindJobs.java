package org.ovirtChina.enginePlugin.vmBackupScheduler.bll;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.ovirtChina.enginePlugin.vmBackupScheduler.common.BackupMethod;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.Task;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.TaskStatus;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.VmPolicy;
import org.ovirtChina.enginePlugin.vmBackupScheduler.dao.DbFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FindJobs extends TimerTask {
    private static Logger log = LoggerFactory.getLogger(TimerTask.class);

    @Override
    public void run() {
        List<VmPolicy> policies = DbFacade.getInstance().getVmPolicyDAO().getScheduleVms();
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        for (VmPolicy policy : policies) {
            if (!policy.getWeekDays().isEmpty() && policy.getWeekDays().indexOf(dayOfWeek) != '0'
                    || policy.getWeekDays().isEmpty()) {
                if (policy.isEnabled()
                        && DbFacade.getInstance().getTaskDAO().get(policy.getVmID()) == null) {
                    DbFacade.getInstance().getTaskDAO().save(
                            new Task(policy.getVmID(), TaskStatus.WAITING.getValue(), policy.getBackupMethod(), null, now, now));
                    log.info("created a new task " + BackupMethod.forValue(policy.getBackupMethod()) + " for vm: " + policy.getVmID());
                }
            }
        }
    }

}
