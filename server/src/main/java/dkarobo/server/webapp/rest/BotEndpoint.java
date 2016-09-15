package dkarobo.server.webapp.rest;

import harmony.core.api.plan.Plan;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletContext;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dkarobo.bot.Bot;
import dkarobo.bot.BotViaRest;
import dkarobo.bot.BusyBotException;
import dkarobo.bot.Coordinates;
import dkarobo.bot.DummyBot;
import dkarobo.bot.Position;
import dkarobo.server.plans.DKAManager;
import dkarobo.server.plans.PlansCache;
import dkarobo.server.webapp.Application;

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
		log.trace("Calling GET /wru");
		try {
			Coordinates c = getBot().whereAreYou();
			JsonObject jow = new JsonObject();
			JsonObject jo = new JsonObject();
			jo.addProperty("x", c.getX());
			jo.addProperty("y", c.getY());
			jo.addProperty("theta", c.getZ());
			jow.add("current_position", jo);
			return Response.ok(jow.toString()).build();
		} catch (Exception e) {
			throw new WebApplicationException(e);
		}
	}

	@GET
	@Path("/setbot")
	public Response setbot(@QueryParam("address") String address) {
		log.trace("Calling GET /setbot: {}", address);
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
		context.setAttribute(Application._ObjectBOT, new DummyBot( 2,-31, 0)); // Markbucks 100, 100, 0));
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
		try {
			return Response.ok(getBot().whatHaveYouDone()).build();
		} catch (IOException e) {
			throw new WebApplicationException(e);
		}
	}

	@GET
	@Path("/isbusy")
	public Response isbusy() {
		try {
			return Response.ok(getBot().isBusy()).build();
		} catch (IOException e) {
			throw new WebApplicationException(e);
		}
	}

	@DELETE
	@Path("/abort")
	public Response abort() {
		try {
			getBot().abort();
		} catch (IOException e) {
			throw new WebApplicationException(e);
		}
		return Response.noContent().build();
	}

	@GET
	@Path("/send")
	public Response send(@QueryParam("query") String query) {

		// If it is busy, say picche
		try {
			if (getBot().isBusy()) {
				return Response.status(Status.CONFLICT).build();
			}
		} catch (IOException e1) {
			throw new WebApplicationException(e1);
		}

		DKAManager manager = (DKAManager) context.getAttribute(Application._ObjectMANAGER);
		PlansCache cache = (PlansCache) context.getAttribute(Application._ObjectPLANSCACHE);
		Plan plan;
		if (!cache.isCached(query)) {
			try {
				plan = manager.performPlanning(query, getBot().whereAreYou());
			} catch (IOException e) {
				throw new WebApplicationException(e);
			}
		} else {
			plan = cache.get(query);
		}
		try {
			String[] theplan = manager.toBotJsonPlan(plan);
			getBot().sendPlan(theplan);
		} catch (BusyBotException | IOException e) {
			log.error("This should not happen", e);
			return Response.status(Status.EXPECTATION_FAILED).build();
		}
		return Response.created(requestUri.resolve(URI.create("doing"))).build();
	}

	@POST
	@Path("/write")
	public Response write(@FormParam("d") String data) {
		log.debug("POST /bot/write {}", data);
		JsonObject object = (JsonObject) new JsonParser().parse(data);
		Coordinates coord =  Position.create( object.get("x").getAsFloat(), object.get("y").getAsFloat(), object.get("theta").getAsFloat());
		DKAManager manager = (DKAManager) context.getAttribute(Application._ObjectMANAGER);
//		manager.reloadLocations();
		log.debug("{} {} {} {}", data+" "+ coord+" "+ object.get("field").getAsString()+" "+object.get("value").getAsString());
		boolean ok = manager.roboWrites(coord, object.get("field").getAsString(), object.get("value").getAsString());
		if(ok){
			log.debug("OK");
			
			return Response.ok().build();
		}else{
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@GET
	@Path("/currentplan")
	public Response currentPlan() {
		log.debug("GET /bot/currentplan");
		try {
			String jsonResponse = getBot().currentPlan();
			log.debug("CURRENT PLAN RECEIVED: {}", jsonResponse);
			JsonArray object = new JsonParser().parse(jsonResponse).getAsJsonArray();
			JsonArray ret = new JsonArray();
			
			for(int i = 0; i < object.size(); ++i) {
				JsonObject curActionJson = object.get(i).getAsJsonObject();
				// parsing response into json object
				// getting the coordinates
				String actionName = curActionJson.get("name").getAsString();
				if(actionName.equals("goto")) {
					curActionJson.addProperty("name", "Move");
					Coordinates coordinates = Position.create(curActionJson.get("x").getAsFloat(), curActionJson.get("y").getAsFloat(), curActionJson.get("t").getAsFloat());
					DKAManager manager = (DKAManager) context.getAttribute(Application._ObjectMANAGER);
					String location = manager.toLocation(coordinates);
					try {
						URI locationURI = new URI(location);
						String[] segments = locationURI.getPath().split("/");
						location = segments[segments.length-1];
					} catch (URISyntaxException e) {
						location = location.substring(location.lastIndexOf('/') + 1);
					}
					curActionJson.addProperty("to", location);
				}
				else if(actionName.equals("sniff_wifi")) {
					curActionJson.addProperty("name", "CheckWiFi");
				}
				else if(actionName.equals("read_temperature")) {
					curActionJson.addProperty("name", "ReadTemperature");
				}
				else if(actionName.equals("read_humidity")) {
					curActionJson.addProperty("name", "ReadHumidity");
				}
				else if(actionName.equals("count_people")) {
					curActionJson.addProperty("name", "CountPeople");
				}
				ret.add(curActionJson);
			}
			//adding location to the action
			return Response.ok(ret.toString()).build();
		} catch (IOException e) {
			throw new WebApplicationException(e);
		}
	}
}
