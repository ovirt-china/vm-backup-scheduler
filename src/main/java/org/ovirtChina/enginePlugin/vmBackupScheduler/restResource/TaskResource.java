package org.ovirtChina.enginePlugin.vmBackupScheduler.restResource;

import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.ovirtChina.enginePlugin.vmBackupScheduler.common.Task;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.TaskType;

@Path("/tasks")
@Produces({BackendApplication.ApplicationJson})
public class TaskResource {

    @GET
    public Task list() {
        //This is only for test, will soon be replaced
        return new Task(UUID.randomUUID(), TaskType.CreateExport, "holly");
    }
}
