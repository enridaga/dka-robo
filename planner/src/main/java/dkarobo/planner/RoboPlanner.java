package dkarobo.planner;

import harmony.core.api.plan.Plan;
import harmony.planner.NoSolutionException;
import harmony.planner.PlannerInputBuilder;
import harmony.planner.bestfirst.BestFirstPlanner;
import harmony.planner.bestfirst.BestFirstSearchReport;

public class RoboPlanner {

	private BestFirstPlanner P = null;
	private RoboDomain domain;
	private int max;

	public RoboPlanner(MoveCostProvider moveCost, ValidityProvider validity) {
		domain = new RoboDomain(moveCost, validity);
	}

	public Plan search(RoboProblem problem) throws NoSolutionException {
		PlannerInputBuilder ib = new PlannerInputBuilder(domain, problem);
		P = new BestFirstPlanner(ib.build(), new RoboBestNode());
		if(max>0)
			P.setMaxClosedNodes(max);
		return P.search();
	}

	public BestFirstSearchReport getLastSearchReport() {
		return P.getLastSearchReport();
	}
	
	public void setMaxClsoedNodes(int max){
		this.max = max;
	}
}
