package dkarobo.server.webapp.rest;

import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dkarobo.bot.Bot;
import dkarobo.planner.RoboProblem;
import dkarobo.planner.utils.ReportPrinter;
import dkarobo.server.plans.DKAManager;
import dkarobo.server.plans.PlansCache;
import dkarobo.server.webapp.Application;
import harmony.core.api.fact.Fact;
import harmony.core.api.operator.GroundAction;
import harmony.core.api.plan.Plan;
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
	public Response problem(@QueryParam("query") String query) {
		log.trace("Calling GET /problem");
		try {
			DKAManager manager = (DKAManager) context.getAttribute(Application._ObjectMANAGER);
			Bot bot = (Bot) context.getAttribute(Application._ObjectBOT);
			if(bot == null){
				return Response.status(Status.EXPECTATION_FAILED).entity("Bot is not available\n").build();
			}
			RoboProblem problem = manager.getProblem(query, bot.whereAreYou());
			List<Fact> facts = problem.getInitialState().getFacts();
			log.debug("initial state: {} facts", facts.size());
			RendererImpl r = new RendererImpl();
			for (Fact fa : facts) {
				r.append(fa).append("\n");
			}

			return Response.ok(r.toString()).build();
		} catch (Exception e) {
			throw new WebApplicationException(e);
		}
	}

	@GET
	@Path("/plan")
	public Response plan(@QueryParam("query") String query) {
		log.trace("Calling GET /plan");
		try {
			DKAManager manager = (DKAManager) context.getAttribute(Application._ObjectMANAGER);
			Bot bot = (Bot) context.getAttribute(Application._ObjectBOT);
			if(bot == null){
				return Response.status(Status.EXPECTATION_FAILED).entity("Bot is not available\n").build();
			}
			Plan plan = manager.performPlanning(query, bot.whereAreYou());
			PlansCache cache = (PlansCache) context.getAttribute(Application._ObjectPLANSCACHE);
			// caching the plan
			cache.put(query, plan);
			log.debug("Plan: {} actions", plan.size());
			if (plan.size() == -1) {
				return Response.noContent().build();
			}
			
			return Response.ok(ReportPrinter.toString(plan)).build();
		} catch (Exception e) {
			log.error("",e);
			throw new WebApplicationException(e);
		}
	}

	@GET
	@Path("/plan-cached")
	public Response planCached(@QueryParam("query") String query) {
		log.trace("Calling GET /plan-cached");
		try {
			PlansCache cache = (PlansCache) context.getAttribute(Application._ObjectPLANSCACHE);
			Plan plan = cache.get(query);

			log.debug("Plan: {} ", plan);

			if (plan == null) {
				return Response.status(404).build();
			}
			
			if (plan.size() == -1) {
				return Response.noContent().build();
			}
			
			RendererImpl r = new RendererImpl();
			for (GroundAction a : plan.getActions()) {
				r.append(a.toString()).append("\n");
			}

			return Response.ok(r.toString()).build();
		} catch (Exception e) {
			throw new WebApplicationException(e);
		}
	}
}
