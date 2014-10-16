package org.ovirtChina.enginePlugin.vmBackupScheduler.restResource;

import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.ovirtChina.enginePlugin.vmBackupScheduler.common.AutoDeleteReservePolicy;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.BackupMethod;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.TimeOfDay;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.VmPolicy;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.WeekDays;

@Path("/vmPolicies")
@Produces({BackendApplication.ApplicationJson})
public class VmPolicyResource {

    @GET
    public VmPolicy list() {
        //This is only for test, will soon be replaced
        return new VmPolicy(UUID.randomUUID(),
                BackupMethod.Export, new TimeOfDay(23, 30),
                new WeekDays("1100110"),
                AutoDeleteReservePolicy.Day, 10);
    }
}
