package org.ovirtChina.enginePlugin.vmBackupScheduler.bll;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.ovirtChina.enginePlugin.vmBackupScheduler.common.BackupMethod;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.Task;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.TaskStatus;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.TaskType;
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
            if (!policy.getWeekDays().isEmpty() && policy.getWeekDays().charAt(dayOfWeek-1) != '0'
                    || policy.getWeekDays().isEmpty()) {
                if (policy.isEnabled()
                        && isTimeToSchedule(now, policy)
                        && istaskExecuting(policy)) {
                    TaskType taskType = policy.getBackupMethod() == BackupMethod.Export.getValue() ?
                            TaskType.CreateExport : TaskType.CreateSnapshot;
                    DbFacade.getInstance().getTaskDAO().save(
                            new Task(policy.getVmID(), TaskStatus.WAITING.getValue(),
                                    taskType.getValue(), null, now, now));
                    log.info("created a new task " + taskType + " for vm: " + policy.getVmID());
                }
            }
        }
    }

    private boolean istaskExecuting(VmPolicy policy) {
        Task task = DbFacade.getInstance().getTaskDAO().getExecutingTaskForVm(policy.getVmID());
        return task == null;
    }

    private boolean isTimeToSchedule(Date now, VmPolicy policy) {
        String[] policyTimeString = policy.getTimeOfDay().split(":");
        Date policyTime = new Date();
        policyTime.setHours(Integer.parseInt(policyTimeString[0]));
        policyTime.setMinutes(Integer.parseInt(policyTimeString[1]));
        policyTime.setSeconds(0);
        log.info("time diff: " + (now.getTime() - policyTime.getTime()));
        if (Math.abs(now.getTime() - policyTime.getTime()) < 30000L) {
            Task task = DbFacade.getInstance().getTaskDAO().getOldestTaskTypeWithStatus(
                    policy.getBackupMethod() == BackupMethod.Export.getValue() ?
                            TaskType.CreateExport : TaskType.CreateSnapshot,
                            TaskStatus.FINISHED);
            if (task == null || task != null && Math.abs(task.getCreateTime().getTime() - now.getTime()) > 60000L) {
                return true;
            }
        }
        return false;
    }

}
