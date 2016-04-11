package uk.open.ac.kmi.robo.dka.planner;

import harmony.core.api.thing.Thing;
import uk.open.ac.kmi.robo.dka.planner.things.Validity;

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
