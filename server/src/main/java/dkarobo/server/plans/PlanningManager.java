package dkarobo.server.plans;

import java.util.Collections;
import java.util.List;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.ResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dkarobo.bot.Coordinates;
import dkarobo.planner.MoveCostProvider;
import dkarobo.planner.RoboPlanner;
import dkarobo.planner.RoboProblem;
import dkarobo.planner.ValidityProvider;
import dkarobo.planner.things.QuadResource;
import dkarobo.planner.things.QuadResourceImpl;
import dkarobo.planner.things.Validity;
import dkarobo.planner.things.ValidityImpl;
import dkarobo.sparql.ExpirationTimestampInGraphName;
import dkarobo.sparql.InvalidQuadCollector;
import dkarobo.sparql.MonitoredQueryExecutionFactory;
import dkarobo.sparql.RoboProblemBuilder;
import dkarobo.sparql.ValidityReader;
import dkarobo.sparql.Vocabulary;
import harmony.core.api.operator.GroundAction;
import harmony.core.api.plan.Plan;
import harmony.core.api.thing.Thing;
import harmony.planner.NoSolutionException;

public class PlanningManager {
	private static Logger log = LoggerFactory.getLogger(PlanningManager.class);

	private Dataset dataset;

	private MoveCostProvider moveCostProvider;
	private ValidityProvider validityProvider;

	public PlanningManager(Dataset dataset) {
		this.dataset = dataset;

		this.moveCostProvider = new MoveCostProvider() {

			@Override
			public int validityDecreaseFactor(String A, String B) {
				// TODO Always the same cost
				return 10;
			}
		};

		this.validityProvider = new ValidityProvider() {

			@Override
			public Validity getValidity(Thing S, Thing P, Thing O) {
				// TODO Estimate the validity of this triple, once it is
				// acquired.
				return new ValidityImpl(100);
			}
		};
	}

	public RoboProblem getProblem(String query) {
		ValidityReader provider = new ExpirationTimestampInGraphName(Vocabulary.NS_GRAPH, System.currentTimeMillis());
		InvalidQuadCollector collector = new InvalidQuadCollector(provider);
		QueryExecution qe = MonitoredQueryExecutionFactory.create(query, dataset, collector);
		ResultSet rs = qe.execSelect();
		while (rs.hasNext()) {
			rs.next();
		}
		log.debug("results number: {}", rs.getRowNumber());

		RoboProblem problem = new RoboProblemBuilder(collector).getProblem();
		return problem;
	}

	public QuadResource toLocation(Coordinates coordinates) {
		// XXX Calculate the location
		return new QuadResourceImpl("a location");
	}

	public Plan performPlanning(String query, Coordinates location) {
		RoboPlanner planner = new RoboPlanner(moveCostProvider, validityProvider);
		RoboProblem problem = getProblem(query);
		problem.onInitAt(toLocation(location));
		try {
			planner.search(problem);
		} catch (NoSolutionException e) {
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

		return planner.getLastSearchReport().getPlan();
	}
}
