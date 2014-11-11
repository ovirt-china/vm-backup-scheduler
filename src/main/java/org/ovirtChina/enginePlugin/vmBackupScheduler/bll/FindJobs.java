package org.ovirtChina.enginePlugin.vmBackupScheduler.bll;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.ovirtChina.enginePlugin.vmBackupScheduler.common.Task;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.TaskStatus;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.VmPolicy;
import org.ovirtChina.enginePlugin.vmBackupScheduler.dao.DbFacade;

public class FindJobs extends TimerTask {

    @Override
    public void run() {
        List<VmPolicy> policies = DbFacade.getInstance().getVmPolicyDAO().getScheduleVms();
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        System.out.println("today is: " + dayOfWeek);
        for (VmPolicy policy : policies) {
            if (!policy.getWeekDays().isEmpty() && policy.getWeekDays().indexOf(dayOfWeek) != '0'
                    || policy.getWeekDays().isEmpty()) {
                if (policy.isEnabled()) {
                    // check prev task status
                    DbFacade.getInstance().getTaskDAO().save(
                            new Task(policy.getVmID(), TaskStatus.WAITING.getValue(), policy.getBackupMethod(), null));
                }
            }
        }
    }

}
