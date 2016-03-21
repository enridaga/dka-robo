package uk.open.ac.kmi.robo.planner;

import harmony.core.api.thing.Thing;

public interface MoveCostProvider {
	public int validityDecreaseFactor(Thing from, Thing to);
}
