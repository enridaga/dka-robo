package uk.open.ac.kmi.robo.dka.planner.property;

import harmony.core.impl.property.BasicProperty;
import uk.open.ac.kmi.robo.dka.planner.things.QuadResource;

public class At extends BasicProperty {
	@SuppressWarnings("unchecked")
	public At() {
		super("At", QuadResource.class);
	}
}
