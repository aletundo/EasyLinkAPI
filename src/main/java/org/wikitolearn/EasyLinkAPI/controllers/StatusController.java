package org.wikitolearn.EasyLinkAPI.controllers;

import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.wikitolearn.EasyLinkAPI.controllers.utils.TaskStateAbstract;

@Path("/status")
public class StatusController {

	@Context
	private ServletContext application;

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Object getStatus(@PathParam("id") UUID id) {
		@SuppressWarnings("unchecked")
		Map<UUID, TaskStateAbstract> activeThreads = (Map<UUID, TaskStateAbstract>) application.getAttribute("activeThreads");
		// if("Progress".equals(activeThreads.get(id).getStatus()))
        TaskStateAbstract t = activeThreads.get(id);
        return activeThreads.get(id);

	}

	@DELETE
	@Path("{id}")
	public Response deleteRequest(@PathParam("id") UUID id) {
		@SuppressWarnings("unchecked")
		Map<UUID, TaskStateAbstract> activeThreads = (Map<UUID, TaskStateAbstract>) application.getAttribute("activeThreads");
		activeThreads.get(id).getExecutorService().shutdownNow();
		activeThreads.remove(id);
		return Response.ok().build();
	}
}
