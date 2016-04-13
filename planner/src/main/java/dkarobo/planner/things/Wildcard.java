package dkarobo.planner.things;

import harmony.core.api.thing.Thing;

public class Wildcard implements Thing, Validity, QuadResource, QuadProperty {

	private Wildcard() {
	}

	@Override
	public String getSignature() {
		return "_";
	}

	@Override
	public int asInteger() {
		return -1000000000; // ?
	}

	private static Wildcard it = null;

	public static final Wildcard it() {
		if (it == null)
			it = new Wildcard();
		return it;
	}
}
