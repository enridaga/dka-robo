package dkarobo.server.webapp.rest;

import java.net.URI;

import javax.servlet.ServletContext;
import javax.ws.rs.DELETE;
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
import dkarobo.bot.BotViaRest;
import dkarobo.bot.BusyBotException;
import dkarobo.bot.DummyBot;
import dkarobo.server.plans.DKAManager;
import dkarobo.server.plans.PlansCache;
import dkarobo.server.webapp.Application;
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
		try {
			return (Bot) context.getAttribute(Application._ObjectBOT);
		} catch (Exception e) {
			throw new WebApplicationException(e);
		}
	}

	@GET
	@Path("/wru")
	public Response whereAreYou() {
		log.trace("Calling GET /whereAreYou");
		try {
			return Response.ok(getBot().whereAreYou().toString()).build();
		} catch (Exception e) {
			throw new WebApplicationException(e);
		}
	}

	@GET
	@Path("/setbot")
	public Response setbot(@QueryParam("address") String address) {
		log.trace("Calling GET /setbot");
		try {
			if ("dummy".equals(address)) {
				return setdummybot();
			}
			context.setAttribute(Application._ObjectBOT, new BotViaRest(address));
			return Response.ok().build();
		} catch (Exception e) {
			throw new WebApplicationException(e);
		}
	}

	/**
	 * This is for test only
	 * 
	 * @return
	 */
	public Response setdummybot() {
		context.setAttribute(Application._ObjectBOT, new DummyBot(100, 100, 0));
		return Response.ok().build();
	}

	@GET
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
	

	@GET
	@Path("/isbusy")
	public Response isbusy() {
		return Response.ok(getBot().isBusy()).build();
	}

	@DELETE
	@Path("/abort")
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

		DKAManager manager = (DKAManager) context.getAttribute(Application._ObjectMANAGER);
		PlansCache cache = (PlansCache) context.getAttribute(Application._ObjectPLANSCACHE);
		Plan plan;
		if (!cache.isCached(query)) {
			plan = manager.performPlanning(query, getBot().whereAreYou());
		} else {
			plan = cache.get(query);
		}
		try {
			String[] theplan = manager.toBotJsonPlan(plan);
			getBot().sendPlan(theplan);
		} catch (BusyBotException e) {
			log.error("This should not happen", e);
			return Response.status(Status.EXPECTATION_FAILED).build();
		}
		return Response.created(requestUri.resolve(URI.create("doing"))).build();
	}

}
