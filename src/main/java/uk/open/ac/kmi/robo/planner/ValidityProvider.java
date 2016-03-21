package uk.open.ac.kmi.robo.planner;

import harmony.core.api.thing.Thing;
import uk.open.ac.kmi.robo.planner.things.Validity;

public interface ValidityProvider {

	public Validity getValidity(Thing S, Thing P, Thing O);
}
