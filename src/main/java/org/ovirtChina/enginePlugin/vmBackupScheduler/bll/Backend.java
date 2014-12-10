package org.ovirtChina.enginePlugin.vmBackupScheduler.bll;

import java.util.Timer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.naming.NamingException;

import org.ovirt.engine.sdk.Api;
import org.ovirtChina.enginePlugin.vmBackupScheduler.dao.DbFacade;
import org.ovirtChina.enginePlugin.vmBackupScheduler.utils.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton(name = "Scheduler")
@Startup
public class Backend{
    private static Logger log = LoggerFactory.getLogger(Backend.class);
    private static Api api = null;

    @PostConstruct
    public void init() {
        try {
            DbFacade.locateDataSource();
        } catch (NamingException e) {
            log.error("Error locating datasource.");
        }
        api = getApi();
        Timer timer = new Timer();
        timer.schedule(new FindJobs(), 0, 5000);
        timer.schedule(new ExecuteSnapshot(api), 1000, 5000);
        timer.schedule(new ExecuteExport(api), 2000, 5000);
        timer.schedule(new DeleteExport(api), 3000, 5000);
        timer.schedule(new DeleteSnapshot(api), 4000, 5000);
    }

    @PreDestroy
    public void beforeShutdown() {
        try {
			api.close();
		} catch (Exception e) {
			log.error("Error closing api object", e);
		}
    }

    public static Api getApi() {
        try {
            return new Api(
                    ConfigProvider.getConfig().getProperty(ConfigProvider.SDK_BASE_URL),
                    ConfigProvider.getConfig().getProperty(ConfigProvider.SDK_USER),
                    ConfigProvider.getConfig().getProperty(ConfigProvider.SDK_PASSWORD),
                    null, 443, 10, true, true, false, false);
        } catch (Exception e) {
            throw new RuntimeException("api object init failed!");
        }
    }

}
