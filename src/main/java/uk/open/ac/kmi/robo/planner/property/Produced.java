package uk.open.ac.kmi.robo.planner.property;

import static uk.open.ac.kmi.robo.planner.things.Symbols._;

import java.util.List;

import harmony.core.api.fact.Fact;
import harmony.core.api.fact.FactRegistry;
import harmony.core.api.property.DerivableProperty;
import harmony.core.api.state.State;
import harmony.core.api.thing.Thing;
import harmony.core.impl.property.BasicProperty;
import harmony.core.impl.property.DerivedPropertyException;
import uk.open.ac.kmi.robo.planner.things.QuadProperty;
import uk.open.ac.kmi.robo.planner.things.QuadResource;
import uk.open.ac.kmi.robo.planner.things.Symbols;

public class Produced extends BasicProperty implements DerivableProperty {
	
	@SuppressWarnings("unchecked")
	public Produced() {
		super("Produced", QuadResource.class, QuadProperty.class, QuadResource.class);
	}
	
	@Override
	public boolean isDerivable(State state, Thing... T) throws DerivedPropertyException {
		boolean canBeDerivable = false;
		// If any of the elements of the quad is the wildcard only
		for (Thing t : T) {
			if (t.equals(Symbols._)) {
				canBeDerivable = true;
				break;
			}
		}
		if (!canBeDerivable)
			return false;

		FactRegistry FR = state.getFactRegistry();
		List<Fact> facts = FR.getFacts(Symbols.Produced);
		for (Fact f : facts) {
			for (int i = 0; i < 3; i++) {
				if (T[i].equals(_) || T[i].equals(f.getThing(i))) {
					if (i == 2) {
						// Found!
						return true;
					}
				} else {
					break;
				}
			}
		}
		return false;
	}
}
