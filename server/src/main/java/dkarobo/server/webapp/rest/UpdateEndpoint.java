package dkarobo.server.webapp.rest;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryParseException;
import org.apache.jena.update.UpdateAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dkarobo.server.webapp.Application;

@Path("/{endpoint: (update)}")
public class UpdateEndpoint {
	private static Logger log = LoggerFactory.getLogger(UpdateEndpoint.class);

	@Context
	protected HttpHeaders requestHeaders;

	@PathParam("endpoint")
	private String endpoint;

	@Context
	private ServletContext context;

	@GET
	public Response get(@QueryParam("update") String update) {
		log.trace("Calling GET /{} with query: {}", endpoint, update);
		return perform(update);
	}

	@POST
	@Consumes("application/x-www-form-urlencoded")
	public Response post(@FormParam("update") String update) {
		log.trace("Calling POST /{} with query: {}", endpoint, update);
		return perform(update);
	}

	public Response perform(String update) {
		try {
			Dataset dataset = (Dataset) context.getAttribute(Application._ObjectDataset);
			UpdateAction.parseExecute(update, dataset);
			return Response.ok().build();
		} catch(QueryParseException qpe){
			return Response.status(Status.BAD_REQUEST).entity(qpe.getLocalizedMessage()).build();
		} catch (Exception e) {
			log.error("", e);
			return Response.serverError().build();
		}
	}
}
