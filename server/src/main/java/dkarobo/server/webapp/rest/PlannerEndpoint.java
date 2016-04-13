package dkarobo.server.webapp.rest;

import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.ResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dkarobo.planner.RoboProblem;
import dkarobo.server.webapp.Application;
import dkarobo.sparql.VInvalidQuadCollector;
import dkarobo.sparql.VQuadValidityComputer;
import dkarobo.sparql.VQuadValidityProvider;
import dkarobo.sparql.VQueryExecutionFactory;
import dkarobo.sparql.VRoboProblemBuilder;
import dkarobo.sparql.Vocabulary;
import harmony.core.api.fact.Fact;
import harmony.core.impl.renderer.RendererImpl;

@Path("/planner")
public class PlannerEndpoint {
	private static Logger log = LoggerFactory.getLogger(PlannerEndpoint.class);

	@Context
	protected HttpHeaders requestHeaders;

	@PathParam("endpoint")
	private String endpoint;

	@Context
	private ServletContext context;

	@Context
	protected UriInfo requestUri;

	@GET
	@Path("/problem")
	public Response get(@QueryParam("query") String query) {
		log.trace("Calling GET /problem");
		Dataset dataset = (Dataset) context.getAttribute(Application._ObjectDataset);
		VQuadValidityProvider provider = new VQuadValidityComputer(Vocabulary.NS_GRAPH, System.currentTimeMillis());
		VInvalidQuadCollector collector = new VInvalidQuadCollector(provider);
		QueryExecution qe = VQueryExecutionFactory.create(query, dataset,
				collector);
		ResultSet rs = qe.execSelect();
		while (rs.hasNext()) {
			rs.next();
		}
		log.debug("results number: {}", rs.getRowNumber());

		RoboProblem problem = new VRoboProblemBuilder(collector).getProblem();
		List<Fact> facts = problem.getInitialState().getFacts();
		log.debug("initial state: {} facts", facts.size());
		RendererImpl r = new RendererImpl();
		for (Fact fa : facts) {
			r.append(fa).append("\n");
		}
		qe.close();

		return Response.ok(r.toString()).build();
	}
}
