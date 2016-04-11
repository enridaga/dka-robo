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
	public static final QuadProperty hasLocation = aQuadProperty("hasLocation");
	public static final QuadProperty hasHumidity =  aQuadProperty("hasHumidity");
	public static final QuadProperty hasPeopleCount =  aQuadProperty("hasPeopleCount");
	public static final QuadProperty hasWiFiSignal =  aQuadProperty("hasWiFiSignal");
	public static final QuadProperty type =  aQuadProperty("type");
	public static final QuadProperty hasTemperature = aQuadProperty("hasTemperature");
	public static final Wildcard _ = new Wildcard();

	public static final QuadResource aQuadResource(String id) {
		return new QuadResourceImpl(id);
	}
	public static final QuadProperty aQuadProperty(String id) {
		return new QuadPropertyImpl(id);
	}
}
