package org.wikitolearn.EasyLinkAPI.controllers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.wikitolearn.EasyLinkAPI.utils.MongoConnection;

@Path("/annotations")
public class AnnotationsController {

	@GET
	public String getAnnotations(){
		MongoConnection.getInstance().connect();
		MongoConnection.getInstance().disconnect();

		return "Ok";
	}
}
