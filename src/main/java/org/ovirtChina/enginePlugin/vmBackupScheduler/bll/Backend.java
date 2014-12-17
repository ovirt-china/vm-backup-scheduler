package org.ovirtChina.enginePlugin.vmBackupScheduler.bll;

import java.io.IOException;
import java.net.NoRouteToHostException;
import java.util.Timer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.naming.NamingException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.HttpHostConnectException;
import org.ovirt.engine.sdk.Api;
import org.ovirt.engine.sdk.exceptions.ServerException;
import org.ovirt.engine.sdk.exceptions.UnsecuredConnectionAttemptError;
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
    public void init()
            throws ClientProtocolException, ServerException, UnsecuredConnectionAttemptError, IOException, InterruptedException {
        try {
            DbFacade.locateDataSource();
        } catch (NamingException e) {
            log.error("Error locating datasource.");
        }

        int interval = Integer.parseInt(ConfigProvider.getConfig().getProperty(ConfigProvider.QUERY_INTERVAL_M));
        api = getApi(interval);
        Timer timer = new Timer();
        timer.schedule(new FindJobs(), 0, interval);
        timer.schedule(new ExecuteSnapshot(api), 1000, interval);
        timer.schedule(new ExecuteExport(api), 2000, interval);
        timer.schedule(new DeleteExport(api), 3000, interval);
        timer.schedule(new DeleteSnapshot(api), 4000, interval);
    }

    @PreDestroy
    public void beforeShutdown() {
        try {
			api.close();
		} catch (Exception e) {
			log.error("Error closing api object", e);
		}
    }

    public static Api getApi(int interval)
            throws ClientProtocolException, ServerException, UnsecuredConnectionAttemptError, IOException, InterruptedException{
        try {
            return new Api(
                    ConfigProvider.getConfig().getProperty(ConfigProvider.SDK_BASE_URL),
                    ConfigProvider.getConfig().getProperty(ConfigProvider.SDK_USER),
                    ConfigProvider.getConfig().getProperty(ConfigProvider.SDK_PASSWORD),
                    null, 443, 10, true, true, false, false);
        } catch (HttpHostConnectException|NoRouteToHostException|ServerException e) {
            log.error("Refused while getting the api connection, retrying in " + interval/1000 + " seconds...");
            Thread.sleep(interval);
            return getApi(interval + 10000);
        }
    }

}
