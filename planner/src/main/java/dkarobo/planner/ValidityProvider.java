package dkarobo.planner;

import dkarobo.planner.things.Validity;
import harmony.core.api.thing.Thing;

public interface ValidityProvider {

	/**
	 * Seconds of validity of this information, once it is acquired.
	 * 
	 * @param S
	 * @param P
	 * @param O
	 * @return
	 */
	public Validity getValidity(Thing S, Thing P, Thing O);
}
