package org.ovirtChina.enginePlugin.vmBackupScheduler.restResource;

import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.ovirtChina.enginePlugin.vmBackupScheduler.common.Task;
import org.ovirtChina.enginePlugin.vmBackupScheduler.dao.DbFacade;

@Path("/tasks")
@Produces({BackendApplication.ApplicationJson})
public class TasksResource {

    @POST
    @Consumes({BackendApplication.ApplicationJson})
    public Response add(Task task) {
        DbFacade.getInstance().getTaskDAO().save(task);
        return Response.status(Response.Status.ACCEPTED).build();
    }

    @GET
    @Path("{id}")
    public Task getTaskByID(@PathParam("id") String id) {
        return DbFacade.getInstance().getTaskDAO().get(UUID.fromString(id));
    }

    @PUT
    @Consumes({BackendApplication.ApplicationJson})
    public Response updateTask(Task task) {
        DbFacade.getInstance().getTaskDAO().update(task);
        return Response.status(Response.Status.OK).build();
    }

    @DELETE
    @Path("{id}")
    public Response removeTask(@PathParam("id") String id) {
        DbFacade.getInstance().getTaskDAO().delete(UUID.fromString(id));
        return Response.status(Response.Status.OK).build();
    }
}
