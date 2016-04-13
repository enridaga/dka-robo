package dkarobo.planner.property;

import static dkarobo.planner.things.Symbols._anything;

import java.util.List;

import dkarobo.planner.things.QuadProperty;
import dkarobo.planner.things.QuadResource;
import dkarobo.planner.things.Symbols;
import dkarobo.planner.things.Validity;
import harmony.core.api.fact.Fact;
import harmony.core.api.fact.FactRegistry;
import harmony.core.api.property.DerivableProperty;
import harmony.core.api.state.State;
import harmony.core.api.thing.Thing;
import harmony.core.impl.property.BasicProperty;
import harmony.core.impl.property.DerivedPropertyException;

/**
 * 
 * Supposed to be: Quad(G,S,P,O) where G is a validity duration or "always"
 *
 */
public class Quad extends BasicProperty implements DerivableProperty {
	@SuppressWarnings("unchecked")
	public Quad() {
		super("Quad", Validity.class, QuadResource.class, QuadProperty.class, QuadResource.class);
	}

	@Override
	public boolean isDerivable(State state, Thing... T) throws DerivedPropertyException {
		boolean canBeDerivable = false;
		// If any of the elements of the quad is the wildcard only
		for (Thing t : T) {
			if (t.equals(Symbols._anything)) {
				canBeDerivable = true;
				break;
			}
		}
		if (!canBeDerivable)
			return false;

		FactRegistry FR = state.getFactRegistry();
		List<Fact> facts = FR.getFacts(Symbols.Quad);
		for (Fact f : facts) {
			if (T[0].equals(_anything) || f.getThing(0).equals(Symbols.Forever) || T[0].equals(f.getThing(0))) {
				// OK
			} else {
				continue;
			}
			for (int i = 1; i < 4; i++) {
				if (T[i].equals(_anything) || T[i].equals(f.getThing(i))) {
					if (i == 3) {
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
