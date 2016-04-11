package dkarobo.planner.operator;

import dkarobo.planner.things.Symbols;
import harmony.core.api.thing.Thing;
import harmony.core.impl.operator.AbstractOperator;

public abstract class RoboOperator extends AbstractOperator {

	public RoboOperator() {
	}

	public RoboOperator(String name, @SuppressWarnings("unchecked") Class<? extends Thing>... things) {
		super(name, things);
	}

	protected boolean usesWildcard(Thing... T) {
		for (Thing t : T) {
			if (t.equals(Symbols._))
				return true;
		}
		return false;
	}
}
