package dkarobo.planner;

import static dkarobo.planner.things.Symbols.Forever;
import static dkarobo.planner.things.Symbols.Location;
import static dkarobo.planner.things.Symbols.ValidQuad;
import static dkarobo.planner.things.Symbols._anything;
import static dkarobo.planner.things.Symbols.aQuadResource;
import static dkarobo.planner.things.Symbols.hasHumidity;
import static dkarobo.planner.things.Symbols.hasPeopleCount;
import static dkarobo.planner.things.Symbols.hasTemperature;
import static dkarobo.planner.things.Symbols.hasWiFiSignal;
import static dkarobo.planner.things.Symbols.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dkarobo.planner.MoveCostProvider;
import dkarobo.planner.RoboBestNode;
import dkarobo.planner.RoboPlanner;
import dkarobo.planner.RoboProblem;
import dkarobo.planner.ValidityProvider;
import dkarobo.planner.things.QuadResource;
import dkarobo.planner.things.QuadResourceImpl;
import dkarobo.planner.things.Validity;
import dkarobo.planner.things.ValidityImpl;
import harmony.core.api.fact.Fact;
import harmony.core.api.operator.Action;
import harmony.core.api.plan.Plan;
import harmony.core.api.thing.Thing;
import harmony.core.impl.condition.AssertFact;
import harmony.core.impl.renderer.RendererImpl;
import harmony.planner.NoSolutionException;
import harmony.planner.bestfirst.BacktracePlan;
import harmony.planner.bestfirst.BestFirstSearchReport;
import harmony.planner.bestfirst.Node;

public class PlannerTest {

	Logger log = LoggerFactory.getLogger(PlannerTest.class);

	@Rule
	public TestName name = new TestName();

	// Thing R1 = aThing("R1");
	final QuadResource L1 = aQuadResource("L1");
	final QuadResource L2 = aQuadResource("L2");
	final QuadResource L3 = aQuadResource("L3");
	final QuadResource L4 = aQuadResource("L4");
	final QuadResource L5 = aQuadResource("L5");
	final QuadResource L6 = aQuadResource("L6");
	final QuadResource L7 = aQuadResource("L7");
	final QuadResource L8 = aQuadResource("L8");
	final QuadResource L9 = aQuadResource("L9");
	final QuadResource L10 = aQuadResource("L10");

	private RoboProblem rp;
	private MoveCostProvider mcp;
	private ValidityProvider vp;

	private QuadResource aValue() {
		return new QuadResourceImpl("V" + System.currentTimeMillis());
	}

	@Before
	public void Before() {
		mcp = new MoveCostProvider() {

			@Override
			public int validityDecreaseFactor(String from, String to) {
				try {
					// This breaks if the two things are not L{number}
					// in this case we return a _Always. This does not have
					// effect in the plan
					// as the Move action has a guard in the precodnitions to
					// avoid to
					// move from/to things that are not locations
					int diff = Integer.parseInt(from.substring(1))
							- Integer.parseInt(to.substring(1));
					diff = Math.abs(diff);
					log.debug("from {} to {} in {} minutes", new Object[] { from, to, diff });
					return diff;
				} catch (Exception e) {
					return 0;
				}
			}
		};
		vp = new ValidityProvider() {

			@Override
			public Validity getValidity(Thing S, Thing P, Thing O) {
				int v = 20;
				Thing[] more = new Thing[] { L1, L2, L3, L4, L5 };
				Thing[] less = new Thing[] { L6, L7, L8, L9, L10 };
				// Temperature at location
				if (P.equals(hasTemperature)) {
					v = 30;
				} else if (P.equals(hasPeopleCount)) {
					v = 50;
				} else if (P.equals(hasHumidity)) {
					v = 30;
				} else if (P.equals(hasWiFiSignal)) {
					v = 100;
				}
				if (Arrays.asList(more).contains(S)) {
					v += 10;
				} else if (Arrays.asList(less).contains(S)) {
					v -= 10;
				}
				return new ValidityImpl(v);
			}
		};

		rp = new RoboProblem();
	}

	private Plan search() throws NoSolutionException {
		// PlannerInputBuilder ib = new PlannerInputBuilder(rd, rp);
		// BestFirstPlanner planner = new BestFirstPlanner(ib.build());
		RoboPlanner planner = new RoboPlanner(mcp, vp);
		Plan plan = planner.search(rp);
		Node last = planner.getLastSearchReport().getGoalNode();
		log.info("Plan:");
		for (Action a : plan.getActions()) {
			log.info("{}", new RendererImpl().append(a));
		}
		log.info("Goal State:");
		for (Fact f : last.getFacts()) {
			log.info(" {}", new RendererImpl().append(f));
		}
		return plan;
	}

	private List<Plan> searchAndReport(int max) throws NoSolutionException {
		// PlannerInputBuilder ib = new PlannerInputBuilder(rd, rp);
		// BestFirstPlanner planner = new BestFirstPlanner(ib.build());
		RoboPlanner planner = new RoboPlanner(mcp, vp);
		planner.setMaxClsoedNodes(max);
		List<Plan> plans = new ArrayList<Plan>();
		BestFirstSearchReport report;
		try {
			Plan plan = planner.search(rp);
			report = planner.getLastSearchReport();
			Node last = report.getGoalNode();
			log.info("Plan:");
			for (Action a : plan.getActions()) {
				log.info("{}", new RendererImpl().append(a));
			}
			log.info("Goal State:");
			for (Fact f : last.getFacts()) {
				log.info(" {}", new RendererImpl().append(f));
			}
			plans.add(plan);
		} catch (NoSolutionException e) {
			report = planner.getLastSearchReport();
			Set<Node> nodes = report.closedNodes();
			for (Node n : nodes) {
				plans.add(new BacktracePlan(n));
			}
		}
		List<String> reportList = new ArrayList<String>();
//		int c = 0;
		for (Plan p : plans) {
//			c++;
			StringBuilder sb = new StringBuilder();
			sb.append(" ");
			for (Node a : ((BacktracePlan) p).getPath()) {
				if (!a.isRoot()) {
					sb.append(new RendererImpl().append(a.getAction().getAction()).toString());
				}
				sb.append("[").append(RoboBestNode.computeMinValidity(a)+RoboBestNode.computeAvgValidity(a)).append("] / ");
			}
			reportList.add(sb.toString());
		}
		Collections.sort(reportList);
		int c = 0;
		for(String s: reportList){
			c++;
			StringBuilder sb = new StringBuilder();
			sb.append(c).append("/").append(report.closedNodes().size());
			System.out.println(sb.append(s).toString());
		}
		return plans;
	}

	@Test
	public void temperature() throws NoSolutionException {

		log.info("{}", name.getMethodName());
		rp.onInitAt(L1);
		rp.onInitQuad(Forever, L1, type, Location);
		rp.onInitQuad(-100, L1, hasTemperature, aValue());
		rp.onInitQuad(Forever, L2, type, Location);

		rp.onGoalValidQuad(L1, hasTemperature, _anything);
		//
		search();
	}

	//
	@Test
	public void move() throws NoSolutionException {

		log.info("{}", name.getMethodName());
		rp.onInitAt(L1);
		rp.onInitQuad(Forever, L1, type, Location);
		rp.onInitQuad(-100, L1, hasTemperature, aValue());
		rp.onInitQuad(Forever, L2, type, Location);
		rp.onInitQuad(Forever, L3, type, Location);
		rp.onInitQuad(Forever, L4, type, Location);
		rp.onInitQuad(-100, L4, hasTemperature, aValue());
		rp.onInitQuad(Forever, L5, type, Location);
		rp.onInitQuad(-100, L5, hasTemperature, aValue());
		// Goal
		rp.onGoalAt(L5);
		//
		search();
	}

	@Test
	public void plan1() throws NoSolutionException {

		log.info("{}", name.getMethodName());
		rp.onInitAt(L1);
		rp.onInitQuad(Forever, L1, type, Location);
		rp.onInitQuad(Forever, L2, type, Location);
		rp.onInitQuad(Forever, L3, type, Location);
		rp.onInitQuad(Forever, L4, type, Location);
		rp.onInitQuad(-100, L4, hasTemperature, aValue());
		rp.onInitQuad(Forever, L5, type, Location);
		rp.onInitQuad(-100, L5, hasTemperature, aValue());
		// Goal
		rp.onGoal(new AssertFact(ValidQuad, L5, hasTemperature, _anything));
		rp.onGoal(new AssertFact(ValidQuad, L4, hasTemperature, _anything));
		//
		search();
	}

	@Test
	public void plan2() throws NoSolutionException {

		log.info("{}", name.getMethodName());
		rp.onInitAt(L1);
		rp.onInitQuad(Forever, L1, type, Location);
//		rp.onInitQuad(Forever, L2, type, Location);
		rp.onInitQuad(Forever, L3, type, Location);
		rp.onInitQuad(Forever, L4, type, Location);
		rp.onInitQuad(Forever, L5, type, Location);
		rp.onInitQuad(Forever, L6, type, Location);
		rp.onInitQuad(Forever, L7, type, Location);
//		rp.onInitQuad(Forever, L8, type, Location);
//		rp.onInitQuad(Forever, L9, type, Location);
		rp.onInitQuad(Forever, L10, type, Location);
		rp.onInitQuad(-100, L3, hasHumidity, aValue());
		rp.onInitQuad(-100, L3, hasWiFiSignal, aValue());
		rp.onInitQuad(-100, L3, hasPeopleCount, aValue());
		rp.onInitQuad(-100, L4, hasTemperature, aValue());
		rp.onInitQuad(-100, L4, hasHumidity, aValue());
		rp.onInitQuad(-100, L5, hasTemperature, aValue());
		rp.onInitQuad(-100, L6, hasWiFiSignal, aValue());
		rp.onInitQuad(-100, L6, hasTemperature, aValue());
		rp.onInitQuad(-100, L7, hasHumidity, aValue());
		rp.onInitQuad(-100, L7, hasTemperature, aValue());
		rp.onInitQuad(-100, L10, hasHumidity, aValue());
		// Goal
		rp.onGoal(new AssertFact(ValidQuad, L3, hasHumidity, _anything));
		rp.onGoal(new AssertFact(ValidQuad, L3, hasWiFiSignal, _anything));
		rp.onGoal(new AssertFact(ValidQuad, L3, hasPeopleCount, _anything));
		rp.onGoal(new AssertFact(ValidQuad, L4, hasTemperature, _anything));
		rp.onGoal(new AssertFact(ValidQuad, L4, hasHumidity, _anything));
		rp.onGoal(new AssertFact(ValidQuad, L5, hasTemperature, _anything));
		rp.onGoal(new AssertFact(ValidQuad, L6, hasWiFiSignal, _anything));
		rp.onGoal(new AssertFact(ValidQuad, L6, hasTemperature, _anything));
		rp.onGoal(new AssertFact(ValidQuad, L7, hasHumidity, _anything));
		rp.onGoal(new AssertFact(ValidQuad, L7, hasTemperature, _anything));
		rp.onGoal(new AssertFact(ValidQuad, L10, hasHumidity, _anything));
		//
		searchAndReport(2000);

	}
}
