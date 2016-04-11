package dkarobo.planner;

import java.util.HashSet;
import java.util.Set;

import dkarobo.planner.things.Symbols;
import dkarobo.planner.things.Validity;
import dkarobo.planner.things.ValidityImpl;
import harmony.core.api.condition.Condition;
import harmony.core.api.fact.Fact;
import harmony.core.api.goal.Goal;
import harmony.core.api.problem.Problem;
import harmony.core.api.thing.Thing;
import harmony.core.impl.condition.And;
import harmony.core.impl.condition.AssertFact;
import harmony.core.impl.fact.BasicFact;
import harmony.core.impl.goal.GoalImpl;
import harmony.core.impl.state.InitialState;
import harmony.core.impl.state.StaticState;

public class RoboProblem implements Problem {
	Set<Fact> init = new HashSet<Fact>();
	Set<Condition> goal = new HashSet<Condition>();
	Set<Thing> objects = new HashSet<Thing>();

	public void onInitQuad(Validity V, Thing S, Thing P, Thing O) {
		objects.add(V);
		objects.add(S);
		objects.add(P);
		objects.add(O);
		init.add(new BasicFact(Symbols.Quad, V, S, P, O));
	}

	public void onInitQuad(int V, Thing S, Thing P, Thing O) {
		onInitQuad(new ValidityImpl(V), S, P, O);
	}

	public void onInitAt(Thing L) {
		objects.add(L);
		init.add(new BasicFact(Symbols.At, L));
	}

	public void onGoalValidQuad(Thing S, Thing P, Thing O) {
		goal.add(new AssertFact(Symbols.ValidQuad, S, P, O));
	}

	public void onGoalAt(Thing L) {
		goal.add(new AssertFact(Symbols.At, L));
	}
	
	public void onGoal(Condition c){
		goal.add(c);
	}

	@Override
	public InitialState getInitialState() {
		// Objects cannot include a wildcard in the init state!
		if (objects.contains(Symbols._)) {
			throw new RuntimeException("Wildcard _ not valid in init facts!");
		}
		StaticState state = new StaticState();
		for (Fact f : init) {
			state.add(f);
		}
		return new InitialState(state);
	}

	@Override
	public Goal getGoal() {
		And and = new And();
		for (Condition c : goal) {
			and.append(c);
		}
		return new GoalImpl(and);
	}

	@Override
	public Thing[] getObjects() {
		return objects.toArray(new Thing[objects.size()]);
	}
}
