package uk.open.ac.kmi.robo.planner.things;

import harmony.core.api.thing.Thing;

public class Wildcard implements Thing, Validity, QuadResource, QuadProperty {

	@Override
	public String getSignature() {
		return "_";
	}

	@Override
	public int asInteger() {
		return -1000000000; // ?
	}
}
