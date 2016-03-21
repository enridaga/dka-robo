package uk.open.ac.kmi.robo.planner.operator;

import static uk.open.ac.kmi.robo.planner.things.Symbols.Forever;
import static uk.open.ac.kmi.robo.planner.things.Symbols.Quad;

import java.util.ArrayList;
import java.util.List;

import harmony.core.api.effect.Effect;
import harmony.core.api.effect.GroundEffect;
import harmony.core.api.fact.Fact;
import harmony.core.api.state.State;
import harmony.core.api.thing.Thing;
import harmony.core.impl.fact.BasicFact;
import uk.open.ac.kmi.robo.planner.things.QuadProperty;
import uk.open.ac.kmi.robo.planner.things.QuadResource;
import uk.open.ac.kmi.robo.planner.things.Validity;
import uk.open.ac.kmi.robo.planner.things.ValidityImpl;

public class DecreaseValidityEffect implements Effect {

	private int factor;

	public DecreaseValidityEffect(int factor) {
		this.factor = factor;
	}

	protected boolean ignore(QuadResource r, QuadProperty p, QuadResource o) {
		return false;
	}

	@Override
	public GroundEffect asGroundEffect(State state) {
		// System.out.println(state.getFactRegistry().size());
		final List<Fact> remove = new ArrayList<Fact>();
		final List<Fact> add = new ArrayList<Fact>();
		for (Fact f : state.getFactRegistry().getFacts(Quad)) {
			if (f.getThing(0).equals(Forever) || ignore((QuadResource) f.getThing(1), (QuadProperty) f.getThing(2),
					(QuadResource) f.getThing(3))) {
				continue;
			}
			Validity oldv = ((Validity) f.getThing(0));
			Validity v = new ValidityImpl(oldv.asInteger() - factor);
			remove.add(f);
			add.add(new BasicFact(Quad, v, f.getThing(1), f.getThing(2), f.getThing(3)));
		}
		return new GroundEffect() {

			@Override
			public Fact[] remove() {
				return remove.toArray(new Fact[remove.size()]);
			}

			@Override
			public Thing[] destroy() {
				return new Thing[0];
			}

			@Override
			public Thing[] create() {
				return new Thing[0];
			}

			@Override
			public Fact[] add() {
				return add.toArray(new Fact[add.size()]);
			}
		};
	}

}
