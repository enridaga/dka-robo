package uk.open.ac.kmi.robo.planner.property;

import harmony.core.impl.property.BasicProperty;
import uk.open.ac.kmi.robo.planner.things.QuadResource;

public class At extends BasicProperty {
	@SuppressWarnings("unchecked")
	public At() {
		super("At", QuadResource.class);
	}
}
