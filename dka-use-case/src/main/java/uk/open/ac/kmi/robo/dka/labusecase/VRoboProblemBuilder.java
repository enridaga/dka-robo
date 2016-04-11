package uk.open.ac.kmi.robo.dka.labusecase;

import uk.open.ac.kmi.robo.dka.planner.RoboProblem;
import uk.open.ac.kmi.robo.dka.planner.things.Symbols;

public class VRoboProblemBuilder implements VQuadListener {

	private long nowSeconds;
	private String timeGraphNs;
	private RoboProblem problem;

	public VRoboProblemBuilder(String timeGraphNs) {
		this(timeGraphNs, System.currentTimeMillis());

	}

	public VRoboProblemBuilder(String timeGraphNs, long mstimestamp) {
		this.nowSeconds = mstimestamp / 1000;
		this.timeGraphNs = timeGraphNs;
		this.problem = new RoboProblem();
	}

	@Override
	public void quad(String G, String S, String P, String O) {
		if (!G.startsWith(timeGraphNs)) {
			// ignore
			return;
		}
		long validUntil = seconds(timeGraphNs);
		long validity = validUntil - nowSeconds;
		if (validity < 0) {
			// Invalid Quad
			problem.onInitQuad(((Long) validity).intValue(), Symbols.aQuadResource(S), Symbols.aQuadProperty(P),
					Symbols.aQuadResource(O));
			problem.onGoalValidQuad(Symbols.aQuadResource(S), Symbols.aQuadProperty(P), Symbols._);
		}
	}

	// private String timeGraph(long millisec) {
	// return timeGraphNs + millisec;
	// }

	private long seconds(String graphName) {
		if (graphName.startsWith(timeGraphNs)) {
			try {
				return Long.parseLong(graphName.substring(timeGraphNs.length()));
			} catch (NumberFormatException e) {
				// Not a time graph
			}
		}
		return 0;
	}

	public RoboProblem getProblem(){
		return problem;
	}
}
