package dkarobo.server.webapp.rest;

import java.util.Map;

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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QueryParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dkarobo.server.webapp.Application;
import uk.ac.open.kmi.basil.rendering.CannotRenderException;
import uk.ac.open.kmi.basil.rendering.Renderer;
import uk.ac.open.kmi.basil.rendering.RendererFactory;

@Path("/{endpoint:(query|sparql)}")
public class QueryEndpoint {
	private static Logger log = LoggerFactory.getLogger(QueryEndpoint.class);

	@Context
	protected HttpHeaders requestHeaders;

	@PathParam("endpoint")
	private String endpoint;

	@Context
	private ServletContext context;

	@Context
	protected UriInfo requestUri;

	@GET
	public Response get(@QueryParam("query") String query) {
		log.info("Calling GET /{} query: {}", endpoint, query);
		return perform(query);
	}

	@POST
	@Consumes("application/x-www-form-urlencoded")
	public Response post(@FormParam("query") String query) {
		log.trace("Calling POST /{} query: {}", endpoint, query);
		return perform(query);

	}

	public Response perform(String query) {
		try {
			Query q = QueryFactory.create(query);
			Map<String, String> prefixMap = q.getPrefixMapping().getNsPrefixMap();
			Renderer<?> renderer = RendererFactory.getRenderer(execute(q));
			MediaType type = AboutMediaTypes.getBestAcceptable(requestHeaders);
			ResponseBuilder rb = Response.ok()
					.entity(renderer.stream(type, requestUri.getRequestUri().toString(), prefixMap));
			rb.header("Content-Type", type.withCharset("UTF-8").toString());
			return rb.build();
		} catch (QueryParseException qpe) {
			return Response.status(Status.BAD_REQUEST).entity(qpe.getLocalizedMessage()).build();
		} catch (CannotRenderException cre) {
			log.error("", cre);
			return Response.serverError().build();
		} catch (Exception e) {
			log.error("", e);
			return Response.serverError().build();
		}
	}

	public Object execute(Query q) {
		Dataset dataset = (Dataset) context.getAttribute(Application._ObjectDataset);
		QueryExecution qe = QueryExecutionFactory.create(q, dataset);

		if (q.isSelectType()) {
			return qe.execSelect();
		} else if (q.isConstructType()) {
			return qe.execConstruct();
		} else if (q.isAskType()) {
			return qe.execAsk();
		} else if (q.isDescribeType()) {
			return qe.execDescribe();
		} else {
			throw new RuntimeException("Unsupported query type: " + q.getQueryType());
		}
	}
}
