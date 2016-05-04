package dkarobo.server.webapp.rest;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dkarobo.bot.Bot;
import dkarobo.bot.BotViaRest;
import dkarobo.bot.BusyBotException;
import dkarobo.bot.DummyBot;
import dkarobo.server.plans.PlanningManager;
import dkarobo.server.plans.PlansCache;
import dkarobo.server.webapp.Application;
import harmony.core.api.operator.GroundAction;
import harmony.core.api.plan.Plan;

@Path("/bot")
public class BotEndpoint {
	private static Logger log = LoggerFactory.getLogger(BotEndpoint.class);

	@Context
	protected HttpHeaders requestHeaders;

	@PathParam("endpoint")
	private String endpoint;

	@Context
	private ServletContext context;

	@Context
	protected UriInfo requestUri;

	private Bot getBot() {
		return (Bot) context.getAttribute(Application._ObjectBOT);
	}

	@Path("/whereAreYou")
	public Response whereAreYou() {
		log.trace("Calling GET /whereAreYou");
		return Response.ok(getBot().whereAreYou().toString()).build();
	}

	@Path("/setbot")
	public Response setbot(@QueryParam("address") String address) {
		log.trace("Calling GET /setbot");
		try {
			context.setAttribute(Application._ObjectBOT, new BotViaRest(URI.create(address).toURL()));
			return Response.ok().build();
		} catch (MalformedURLException e) {
			return Response.serverError().entity(e).build();
		}
	}

	/**
	 * This is for test only
	 * 
	 * @return
	 */
	@Path("/setdummybot")
	public Response setdummybot() {
		log.trace("Calling GET /setdummybot");
		context.setAttribute(Application._ObjectBOT, new DummyBot(100, 100, 0));
		return Response.ok().build();
	}

	@Path("/getbot")
	public Response getbot() {
		log.trace("Calling GET /getbot");
		Object bot = getBot();
		if (bot instanceof Bot) {
			Bot b = (Bot) bot;
			return Response.ok(b.toString()).build();
		}
		return Response.status(404).build();
	}

	@GET
	@Path("/doing")
	public Response doing() {
		return Response.ok(getBot().whatHaveYouDone()).build();
	}

	@DELETE
	@Path("/delete")
	public Response abort() {
		getBot().abort();
		return Response.noContent().build();
	}

	@GET
	@Path("/send")
	public Response send(@QueryParam("query") String query) {

		// If it is busy, say picche
		if (getBot().isBusy()) {
			return Response.status(Status.CONFLICT).build();
		}

		PlansCache cache = (PlansCache) context.getAttribute(Application._ObjectPLANSCACHE);
		Plan plan;
		if (!cache.isCached(query)) {
			PlanningManager manager = (PlanningManager) context.getAttribute(Application._ObjectMANAGER);
			plan = manager.performPlanning(query, getBot().whereAreYou());
		} else {
			plan = cache.get(query);
		}
		try {
			getBot().sendPlan(toExecutablePlan(plan));
		} catch (BusyBotException e) {
			log.error("This should not happen", e);
			return Response.status(Status.EXPECTATION_FAILED).build();
		}
		return Response.created(requestUri.resolve(URI.create("doing"))).build();
	}

	private String[] toExecutablePlan(Plan plan) {
		List<String> actions = new ArrayList<String>();
		for (GroundAction ga : plan.getActions()) {
			actions.add(ga.toString());
		}
		return actions.toArray(new String[actions.size()]);
	}
}
