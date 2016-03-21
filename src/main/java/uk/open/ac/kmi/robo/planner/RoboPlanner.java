package uk.open.ac.kmi.robo.planner;

import harmony.core.api.plan.Plan;
import harmony.planner.NoSolutionException;
import harmony.planner.PlannerInputBuilder;
import harmony.planner.SearchReport;
import harmony.planner.bestfirst.BestFirstPlanner;

public class RoboPlanner {

	private BestFirstPlanner P = null;
	private RoboDomain domain;

	public RoboPlanner(MoveCostProvider moveCost, ValidityProvider validity) {
		domain = new RoboDomain(moveCost, validity);
	}

	public Plan search(RoboProblem problem) throws NoSolutionException {
		PlannerInputBuilder ib = new PlannerInputBuilder(domain, problem);
		P = new BestFirstPlanner(ib.build(), new RoboBestNode());
		return P.search();
	}

	public SearchReport getLastSearchReport() {
		return P.getLastSearchReport();
	}
}
