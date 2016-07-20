package dkarobo.server.plans;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualLinkedHashBidiMap;
import org.apache.commons.io.IOUtils;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QueryParseException;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.update.UpdateAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




import dkarobo.bot.Coordinates;
import dkarobo.bot.Position;
import dkarobo.planner.MoveCostProvider;
import dkarobo.planner.RoboPlanner;
import dkarobo.planner.RoboProblem;
import dkarobo.planner.StageMoveCostProvider;
import dkarobo.planner.ValidityProvider;
import dkarobo.planner.operator.Move;
import dkarobo.planner.operator.Observe;
import dkarobo.planner.operator.Observe.CheckWiFi;
import dkarobo.planner.operator.Observe.CountPeople;
import dkarobo.planner.operator.Observe.Humidity;
import dkarobo.planner.operator.Observe.Temperature;
import dkarobo.planner.things.QuadPropertyImpl;
import dkarobo.planner.things.QuadResource;
import dkarobo.planner.things.QuadResourceImpl;
import dkarobo.planner.things.Symbols;
import dkarobo.planner.things.Validity;
import dkarobo.planner.things.ValidityImpl;
import dkarobo.planner.utils.ReportPrinter;
import dkarobo.sparql.ExpirationTimestampInGraphName;
import dkarobo.sparql.InvalidQuadCollector;
import dkarobo.sparql.MonitoredQueryExecutionFactory;
import dkarobo.sparql.RoboProblemBuilder;
import dkarobo.sparql.ValidityReader;
import dkarobo.sparql.Vocabulary;
import harmony.core.api.operator.GroundAction;
import harmony.core.api.operator.Operator;
import harmony.core.api.plan.Plan;
import harmony.core.api.thing.Thing;
import harmony.planner.NoSolutionException;
import net.minidev.json.JSONObject;

public class DKAManager {
	private static Logger log = LoggerFactory.getLogger(DKAManager.class);

	private Dataset dataset;
	private BidiMap<String, Coordinates> locations;
	private MoveCostProvider moveCostProvider;
	private ValidityProvider validityProvider;

	private Map<String, Integer> rules;
	
	public DKAManager(Dataset dataset) {
		
		// rules
		this.loadRules();
		
		this.dataset = dataset;
		this.moveCostProvider = new StageMoveCostProvider();
		((StageMoveCostProvider) this.moveCostProvider).setSpeed(0.3);
		
		this.loadLocations();
		

		this.validityProvider = new ValidityProvider() {

			
			// TODO put here implementing class
			@Override
			public Validity getValidity(Thing S, Thing P, Thing O) {
//				// Estimate the validity of this triple, once it is
//				// acquired.
				String bestMatchingRule = "";
				int validity = 1000000;
				
				for ( String rule : rules.keySet()){
					
					// prendo la parte statica dal database
					try{
					dataset.begin(ReadWrite.READ);
					Model model = dataset.getNamedModel(Vocabulary.GRAPH_FOREVER);
					Model triple = ModelFactory.createDefaultModel();
					Model toQuery = ModelFactory.createUnion(triple, model);
					
					Resource sbj = triple.createResource(S.getSignature());
					Property prop =  triple.createProperty(P.getSignature());
					Literal obj = triple.createTypedLiteral(O.getSignature());
					triple.add(model.createLiteralStatement(sbj,prop,obj));
					
					// create query
					String q = "ASK {" + rule + "} ";
					Query query = QueryFactory.create(q);	
					QueryExecution qe = QueryExecutionFactory.create(query, toQuery);
					
					
					boolean rs = qe.execAsk();
					
					if (rs){	
						if (rules.get(rule) < validity ){
							bestMatchingRule = rule;
							validity = rules.get(rule) ;
						} // get the most specific
						// what if they are the same?
					} // else? 
					
					qe.close();
					
					}catch(Exception e){
						log.error("",e);
						dataset.abort();
					}finally{
						dataset.end();
					}
				}
				

				if (bestMatchingRule.equals("") ){ // XXX what if?
					log.warn("no matching rule for <{}> <{}> \"{}\"?", new Object[]{  S.getSignature(), P.getSignature(), O.getSignature()} );
					// not sure this could happen
					return new ValidityImpl(500);
				}
				
				int tripleValidityInSeconds =  rules.get(bestMatchingRule); //System.currentTimeMillis() + ;
//				log.debug(" {} matches <{}> <{}> \"{}\" {}", new Object[]{ bestMatchingRule, S.getSignature(), P.getSignature(), O.getSignature(), rules.get(bestMatchingRule)});
//							 
//				int tripleValidityInSeconds = 100;
				// return this
				return new ValidityImpl(tripleValidityInSeconds);
			}
		};
	}

	private void loadRules() {
		
//		String json = IOUtils.toString(connection.getInputStream());
//		log.trace("{}", json);
//		Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);
//		String x = JsonPath.read(document, "$.current_position.x");
//		String y = JsonPath.read(document, "$.current_position.y");
//		String z = JsonPath.read(document, "$.current_position.theta");
//		JsonObject object = (JsonObject) new JsonParser().parse(data);
		
		String  filename = "rules.csv"; //XXX change this
		this.rules = new HashMap<String, Integer>();
		try {
			
			List<String> lines = IOUtils.readLines(new FileInputStream(new File (filename)));
			
			for (String line : lines  ){
				this.rules.put(line.split(";")[0],Integer.parseInt(line.split(";")[1].replace("\"", "")));
			}
		} catch ( IOException e) {
			log.error("File {} not found",new File(filename).getAbsolutePath());	
		}
		log.debug("Rules? {} ({})", !this.rules.isEmpty(), this.rules.size());
		
	}

	public void reloadLocations() {
		loadLocations();
	}

	public RoboProblem getProblem(String query, Coordinates location) {
		ValidityReader provider = new ExpirationTimestampInGraphName(Vocabulary.NS_GRAPH, System.currentTimeMillis());
		InvalidQuadCollector collector = new InvalidQuadCollector(provider);
		QueryExecution qe = MonitoredQueryExecutionFactory.create(query, dataset, collector);
		if (dataset.supportsTransactions()) {
			dataset.begin(ReadWrite.READ);
		}
		ResultSet rs = qe.execSelect();
		while (rs.hasNext()) {
			rs.next();
		}
		log.debug("results number: {}", rs.getRowNumber());

		RoboProblem problem = new RoboProblemBuilder(collector).getProblem();
		if (dataset.supportsTransactions()) {
			dataset.end();
		}
		// Load locations
		for (Entry<String, Coordinates> en : locations.entrySet()) {
			problem.onInitQuad(Symbols.Forever, Symbols.aQuadResource(en.getKey()), Symbols.type, Symbols.Location);
		}
		// Set start location
		problem.onInitAt(new QuadResourceImpl(toLocation(location)));
		return problem;
	}

	private void loadLocations() {
		this.locations = new DualLinkedHashBidiMap<String, Coordinates>();
		//
		String q = "SELECT ?L ?X ?Y { graph <http://data.open.ac.uk/kmi/graph/static> { ?L a <http://data.open.ac.uk/kmi/robo/Location> ; <http://data.open.ac.uk/kmi/robo/xCoord> ?X ; <http://data.open.ac.uk/kmi/robo/yCoord> ?Y . }}";
		QueryExecution qe = QueryExecutionFactory.create(q, dataset);
		if (dataset.supportsTransactions()) {
			dataset.begin(ReadWrite.READ);
		}
		ResultSet rs = qe.execSelect();
		log.debug("Locations? {}", rs.hasNext());
		while (rs.hasNext()) {
			QuerySolution qs = rs.next();
			String location = qs.getResource("L").getURI().toString();
			String X = qs.getLiteral("X").getLexicalForm();
			String Y = qs.getLiteral("Y").getLexicalForm();
			log.debug("Loading {} {} {}", new Object[] { location, X, Y });
			Coordinates coord = Position.create(Float.parseFloat(X), Float.parseFloat(Y), 0);
			locations.put(location, coord);
			((StageMoveCostProvider) moveCostProvider).addCoordinates(location,  Double.parseDouble(X), Double.parseDouble(Y) );
		}
		if (dataset.supportsTransactions()) {
			dataset.end();
		}
	}
	
	public Coordinates toCoordinates(String location){
		return locations.get(location);
	}

	/**
	 * Logic: find the nearest known place.
	 * 
	 * @param coordinates
	 * @return
	 */
	public String toLocation(Coordinates coordinates) {
		String location = null; // will be the closest
		float x = coordinates.getX();
		float y = coordinates.getY();
		float distance = 10000; // a large number

		for (Entry<String, Coordinates> loc : locations.entrySet()) {
			// compute distance between A (current coord) and any other location (B)
			float z = loc.getValue().getX(); // B coordX
			float q = loc.getValue().getY(); // B coordY
			float d = (float) Math.sqrt(Math.pow(x - z, 2) + Math.pow(q - y, 2));
			
			
			log.trace("Distance btw ({},{}) and <{}> ({},{}) = {}", new Object[] { x, y, loc.getKey(), z, q, d });
			if (distance > d) {
				distance = d;
				location = loc.getKey();
			}
		}
		log.debug("Closest? {} : {} ({})", new Object[] { coordinates, locations.get(location), location });
		return location;
	}

	public Plan performPlanning(String query, Coordinates location) {
		return performPlanning(getProblem(query, location));
	}

	public Plan performPlanning(RoboProblem problem) {
		RoboPlanner planner = new RoboPlanner(moveCostProvider, validityProvider);

		try {
			planner.search(problem);
		} catch (NoSolutionException e) {
			
			log.debug("No solution");
			return new Plan() {
				@Override
				public int size() {
					return -1;
				}

				@Override
				public List<GroundAction> getActions() {
					return Collections.emptyList();
				}
			};
		}

		if (log.isDebugEnabled()) {
			ReportPrinter.print(System.out, planner);
		}

		return planner.getLastSearchReport().getPlan();
	}

	/**
	 * Returns an ordered array of actions in the form of Json Objects.
	 * 
	 * @param plan
	 * @return
	 */
	public String[] toBotJsonPlan(Plan plan) {
		List<String> actions = new ArrayList<String>();
		for (GroundAction ga : plan.getActions()) {
			Operator operator = ga.getAction().operator();
			JSONObject o = new JSONObject();
			if (operator instanceof Move) {
				QuadResource qr = (QuadResource) ga.parameters()[1];
				String location = qr.getSignature();
				Coordinates coo = locations.get(location);
				if (coo == null)
					throw new RuntimeException("Unknown location: " + location);
				o.put("name", "goto");
				o.put("x", coo.getX());
				o.put("y", coo.getY());
				o.put("t", coo.getZ());
			} else if (operator instanceof Observe) {
				if (operator instanceof Humidity) {
					o.put("name", "read_humidity");
				} else if (operator instanceof CountPeople) {
					o.put("name", "count_people");
				}else if (operator instanceof Temperature) {
					o.put("name", "read_temperature");
				} else if (operator instanceof CheckWiFi) {
					o.put("name", "sniff_wifi");
					o.put("iface", "wlan0");
				} else {
					throw new RuntimeException("Unsupported Action Operator");
				}
			} else {
				throw new RuntimeException("Unsupported Action Operator");
			}
			actions.add(o.toJSONString());
		}
		return actions.toArray(new String[actions.size()]);
	}

	public String fieldToProperty(String roboField) {
		if (roboField.equals("humidity")) {
			return Vocabulary.hasHumidity.getURI();
		} else if (roboField.equals("wifi")) {
			return Vocabulary.hasWiFiSignal.getURI();
		} else if (roboField.equals("temperature")) {
			return Vocabulary.hasTemperature.getURI();
		} 
		else if (roboField.equals("count_people")){
			return Vocabulary.hasPeopleCount.getURI();// TODO check
		}
		else {
			throw new UnsupportedOperationException("unknown field " + roboField);
		}
	}

	public boolean roboWrites(Coordinates location, String field, String value) {
		log.debug("Coord ({},{})", location.getX(), location.getY());
		String place = toLocation(location);
		String property = fieldToProperty(field);
		Thing s = new QuadResourceImpl(place);
		Thing p = new QuadPropertyImpl(property);
		Thing v = new QuadResourceImpl(value);
		Validity validity = validityProvider.getValidity(s, p, v);
		int seconds = validity.asInteger();
		int milliseconds = seconds * 1000;
		long timestamp = System.currentTimeMillis();
		long validUntil = timestamp + (long) milliseconds;
		String graph = Vocabulary.NS_GRAPH + validUntil;
		String update = "DELETE { GRAPH ?ANY { <" + place + "> <" + property + "> ?t } } "
				+ " INSERT { GRAPH <" + graph + "> { <" + place + "> <" + property + "> \"" + value + "\" } } " 
				+ "WHERE  { GRAPH ?ANY { <" + place + "> <" + property + "> ?t } } ";
		
		log.info("Update g={}, place={}, prop={}, val={}", new Object[]{graph,place,property,value});
		try {
			dataset.begin(ReadWrite.WRITE);
			log.info("{}",update);
			UpdateAction.parseExecute(update, dataset);
			dataset.commit();

			log.debug("OK");
			return true;
		} catch (QueryParseException qpe) {
			log.error("FAILED", qpe);
			dataset.abort();
			return false;
		} catch (Exception e) {
			log.error("", e);
			dataset.abort();

			return false;
		} finally {
			dataset.end();
			
		}
	}
}
