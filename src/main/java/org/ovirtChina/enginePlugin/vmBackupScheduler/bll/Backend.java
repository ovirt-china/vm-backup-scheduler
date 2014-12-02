package org.ovirtChina.enginePlugin.vmBackupScheduler.bll;

import java.util.Timer;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.naming.NamingException;

import org.ovirtChina.enginePlugin.vmBackupScheduler.dao.DbFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton(name = "Scheduler")
@Startup
public class Backend{
    private static Logger log = LoggerFactory.getLogger(Backend.class);

    @PostConstruct
    public void init() {
        try {
            DbFacade.locateDataSource();
        } catch (NamingException e) {
            log.error("Error locating datasource.");
        }
        Timer timer = new Timer();
        timer.schedule(new FindJobs(), 0, 5000);
        timer.schedule(new ExecuteSnapshot(), 1000, 5000);
        timer.schedule(new ExecuteExport(), 2000, 5000);
        timer.schedule(new DeleteExport(), 3000, 5000);
        timer.schedule(new DeleteSnapshot(), 4000, 5000);
    }

}
