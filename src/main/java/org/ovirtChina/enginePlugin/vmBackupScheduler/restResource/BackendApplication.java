package org.ovirtChina.enginePlugin.vmBackupScheduler.restResource;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/")
public class BackendApplication extends Application {
    public final static String ApplicationJson = "application/json";

    private final Set<Object> singletons = new HashSet<Object>();

    @Override
    public Set<Object> getSingletons () {
        return singletons;
    }

    public BackendApplication() {
        singletons.add(new TaskResource());
        singletons.add(new VmPolicyResource());
    }

}
