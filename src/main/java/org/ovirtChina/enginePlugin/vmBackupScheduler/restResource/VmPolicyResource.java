package org.ovirtChina.enginePlugin.vmBackupScheduler.restResource;

import java.util.List;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.ovirtChina.enginePlugin.vmBackupScheduler.common.VmPolicy;
import org.ovirtChina.enginePlugin.vmBackupScheduler.dao.DbFacade;

@Path("/vmPolicies")
public class VmPolicyResource {

    @POST
    public Response add(VmPolicy vmPolicy) {
		return addOrUpdateVmPolicy(vmPolicy);
    }

    @GET
    @Path("{id}")
    public VmPolicy getVmPolicyById(@PathParam("id") String id, @QueryParam("backupMethod") String backupMethod) {
        if (backupMethod.isEmpty()) {
            return null;
        }
        return DbFacade.getInstance().getVmPolicyDAO().get(UUID.fromString(id), Integer.parseInt(backupMethod));
    }

    @PUT
    public Response updateVmPolicy(VmPolicy vmPolicy) {
        return addOrUpdateVmPolicy(vmPolicy);
    }

    @GET
    public List<VmPolicy> getPagedVmPolicies(@QueryParam("page") String page) {
        String[] index = page.split("-");
        return DbFacade.getInstance().getVmPolicyDAO()
                .getPagedVmPolicies(Integer.parseInt(index[0]), Integer.parseInt(index[1]));
    }

    private Response addOrUpdateVmPolicy(VmPolicy vmPolicy) {
        VmPolicy tmpVmPolicy = DbFacade.getInstance().getVmPolicyDAO().get(vmPolicy.getVmID(), vmPolicy.getBackupMethod());
        if (tmpVmPolicy != null) {
            DbFacade.getInstance().getVmPolicyDAO().update(vmPolicy);
        } else {
            DbFacade.getInstance().getVmPolicyDAO().save(vmPolicy);
        }
        return null;
    }
}
