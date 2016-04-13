package dkarobo.sparql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dkarobo.planner.RoboProblem;
import dkarobo.planner.things.Symbols;
import dkarobo.planner.things.Wildcard;

public class RoboProblemBuilder {

	private static final Logger log = LoggerFactory.getLogger(RoboProblemBuilder.class);
	private RoboProblem problem;

	public RoboProblemBuilder(QuadCollector collector) {
		this.problem = new RoboProblem();
		for (String[] t : collector.getTriples()) {
			log.debug("{}",t);
			// Invalid Quad
			String V = t[0];
			String S = t[1];
			String P = t[2];
			String O = t[3];
			// Set as invalid in the init state
			problem.onInitQuad(Integer.parseInt(V), Symbols.aQuadResource(S), Symbols.aQuadProperty(P), Symbols.aQuadResource(O));
			// Set as valid in goal (with any value)
			problem.onGoalValidQuad(Symbols.aQuadResource(S), Symbols.aQuadProperty(P), Wildcard.it());
		}
	}

	public RoboProblem getProblem() {
		return problem;
	}
}
