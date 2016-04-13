package dkarobo.planner.property;

import static dkarobo.planner.things.Symbols._anything;

import java.util.List;

import dkarobo.planner.things.QuadProperty;
import dkarobo.planner.things.QuadResource;
import dkarobo.planner.things.Symbols;
import dkarobo.planner.things.Validity;
import harmony.core.api.fact.Fact;
import harmony.core.api.property.DerivableProperty;
import harmony.core.api.state.State;
import harmony.core.api.thing.Thing;
import harmony.core.impl.property.BasicProperty;
import harmony.core.impl.property.DerivedPropertyException;

public class ValidQuad extends BasicProperty implements DerivableProperty {
	@SuppressWarnings("unchecked")
	public ValidQuad() {
		super("ValidQuad", QuadResource.class, QuadProperty.class, QuadResource.class);
	}

	@Override
	public boolean isDerivable(State state, Thing... T) throws DerivedPropertyException {

		// Lookup State
		List<Fact> facts = state.getFactRegistry().getFacts(Symbols.Quad);
		boolean found = false;
		// Find matching quads and check that all of them are valid.
		for (Fact f : facts) {
			for (int i = 0; i < 3; i++) {
				if (T[i].equals(_anything) || T[i].equals(f.getThing(i + 1))) {
					if (i == 2) {
						//System.out.println(new RendererImpl().append(f).toString());
						// Quad match, but is it time valid?
						Validity v = (Validity) f.getThing(0);
						if (v.asInteger() >= 0) {
							// OK, found and valid
							found = true;
						} else {
							// Found but not valid :(
							return false;
						}
					}
				} else {
					// Quad does not match
					break;
				}
			}
		}
		return found; // all found are also valid
	}
}
