package dkarobo.planner.things;

import dkarobo.planner.property.At;
import dkarobo.planner.property.Produced;
import dkarobo.planner.property.Quad;
import dkarobo.planner.property.ValidQuad;
import dkarobo.planner.things.ValidityImpl.Forever;
import harmony.core.api.property.Property;

public final class Symbols {

	public static final Property Quad = new Quad();
	public static final Property ValidQuad = new ValidQuad();
	public static final Property Produced = new Produced();
	public static final Property At = new At();
	public static final Validity Forever = new Forever();
	public static final QuadResource Location = aQuadResource("http://data.open.ac.uk/kmi/robo/Location");
	public static final QuadProperty hasLocation = aQuadProperty("http://data.open.ac.uk/kmi/robo/hasLocation");
	public static final QuadProperty hasHumidity =  aQuadProperty("http://data.open.ac.uk/kmi/robo/hasHumidity");
	public static final QuadProperty hasPeopleCount =  aQuadProperty("http://data.open.ac.uk/kmi/robo/hasPeopleCount");
	public static final QuadProperty hasWiFiSignal =  aQuadProperty("http://data.open.ac.uk/kmi/robo/hasWiFiSignal");
	public static final QuadProperty type =  aQuadProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
	public static final QuadProperty hasTemperature = aQuadProperty("http://data.open.ac.uk/kmi/robo/hasTemperature");
	public static final Wildcard _anything = Wildcard.it();

	public static final QuadResource aQuadResource(String id) {
		return new QuadResourceImpl(id);
	}
	public static final QuadProperty aQuadProperty(String id) {
		return new QuadPropertyImpl(id);
	}
}
