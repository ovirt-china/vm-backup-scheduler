package org.ovirtChina.enginePlugin.vmBackupScheduler.bll;

import java.util.Timer;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.ovirt.engine.sdk.Api;

@Singleton(name = "Scheduler")
@Startup
public class Backend{

    private static Api api = null;

    @PostConstruct
    public void init() {
        initApi();
        Timer timer = new Timer();
        timer.schedule(new FindJobs(), 0, 5000);
        timer.schedule(new ExecuteSnapshot(), 1000, 5000);
        timer.schedule(new ExecuteExport(), 2000, 5000);
    }

    private void initApi() {
        try {
            // TODO make this configurable
            // TODO and the certificate
            api = new Api(
                    "https://192.168.3.226/api",
                    "admin@internal",
                    "abc123",
                    null, 443, 10, true, true, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Api getApi() {
        return api;
    }

}
