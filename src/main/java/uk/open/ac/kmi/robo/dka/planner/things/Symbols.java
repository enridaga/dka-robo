package uk.open.ac.kmi.robo.dka.planner.things;

import harmony.core.api.property.Property;
import uk.open.ac.kmi.robo.dka.planner.property.At;
import uk.open.ac.kmi.robo.dka.planner.property.Produced;
import uk.open.ac.kmi.robo.dka.planner.property.Quad;
import uk.open.ac.kmi.robo.dka.planner.property.ValidQuad;
import uk.open.ac.kmi.robo.dka.planner.things.ValidityImpl.Forever;

public final class Symbols {

	public static final Property Quad = new Quad();
	public static final Property ValidQuad = new ValidQuad();
	public static final Property Produced = new Produced();
	public static final Property At = new At();
	public static final Validity Forever = new Forever();
	public static final QuadResource Location = aQuadResource("Location");
	public static final QuadProperty hasLocation = new QuadPropertyImpl("hasLocation");
	public static final QuadProperty hasHumidity = new QuadPropertyImpl("hasHumidity");
	public static final QuadProperty hasPeopleCount = new QuadPropertyImpl("hasPeopleCount");
	public static final QuadProperty hasWiFiSignal = new QuadPropertyImpl("hasWiFiSignal");
	public static final QuadProperty type = new QuadPropertyImpl("type");
	public static final QuadProperty hasTemperature = new QuadPropertyImpl("hasTemperature");
	public static final Wildcard _ = new Wildcard();

	public static final QuadResource aQuadResource(String id) {
		return new QuadResourceImpl(id);
	}
}
