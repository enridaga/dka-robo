package uk.open.ac.kmi.robo.dka.planner.property;

import static uk.open.ac.kmi.robo.dka.planner.things.Symbols._;

import java.util.List;

import harmony.core.api.fact.Fact;
import harmony.core.api.fact.FactRegistry;
import harmony.core.api.property.DerivableProperty;
import harmony.core.api.state.State;
import harmony.core.api.thing.Thing;
import harmony.core.impl.property.BasicProperty;
import harmony.core.impl.property.DerivedPropertyException;
import uk.open.ac.kmi.robo.dka.planner.things.QuadProperty;
import uk.open.ac.kmi.robo.dka.planner.things.QuadResource;
import uk.open.ac.kmi.robo.dka.planner.things.Symbols;
import uk.open.ac.kmi.robo.dka.planner.things.Validity;

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
			if (t.equals(Symbols._)) {
				canBeDerivable = true;
				break;
			}
		}
		if (!canBeDerivable)
			return false;

		FactRegistry FR = state.getFactRegistry();
		List<Fact> facts = FR.getFacts(Symbols.Quad);
		for (Fact f : facts) {
			if (T[0].equals(_) || f.getThing(0).equals(Symbols.Forever) || T[0].equals(f.getThing(0))) {
				// OK
			} else {
				continue;
			}
			for (int i = 1; i < 4; i++) {
				if (T[i].equals(_) || T[i].equals(f.getThing(i))) {
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
