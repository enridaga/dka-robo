package dkarobo.server.webapp.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
public class RootResource {
	private static Logger log = LoggerFactory.getLogger(RootResource.class);
	
	@GET
	public Response get(){
		log.trace("Calling GET");
		return Response.ok("DKA Server here.").build();
	}
}
