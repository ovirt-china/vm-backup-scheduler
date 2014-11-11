package org.ovirtChina.enginePlugin.vmBackupScheduler.bll;

import java.util.Timer;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton(name = "Scheduler")
@Startup
public class Backend{

    @PostConstruct
    public void init() {
        Timer timer = new Timer();
        timer.schedule(new FindJobs(), 0, 5000);
        timer.schedule(new ExecuteSnapshot(), 1000, 5000);
        timer.schedule(new ExecuteExport(), 2000, 5000);
    }

}
