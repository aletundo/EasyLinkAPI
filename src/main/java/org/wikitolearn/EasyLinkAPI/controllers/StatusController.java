package org.wikitolearn.EasyLinkAPI.controllers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

public class StatusController {
	
	@GET
	@Path("/status/{id}")
	public void getStatus(@PathParam("id") String id){
		
		
		
	}
}
