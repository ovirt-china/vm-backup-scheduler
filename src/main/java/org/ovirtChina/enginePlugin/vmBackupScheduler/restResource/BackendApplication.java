package org.ovirtChina.enginePlugin.vmBackupScheduler.restResource;

import java.util.HashSet;
import java.util.Set;

import javax.naming.NamingException;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.ovirtChina.enginePlugin.vmBackupScheduler.dao.DbFacade;

@ApplicationPath("/")
public class BackendApplication extends Application {

    private final Set<Object> singletons = new HashSet<Object>();

    @Override
    public Set<Object> getSingletons () {
        return singletons;
    }

    public BackendApplication() {
        singletons.add(new TasksResource());
        singletons.add(new VmPolicyResource());
    }

}
