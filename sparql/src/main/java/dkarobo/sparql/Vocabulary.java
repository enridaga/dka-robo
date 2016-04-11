package dkarobo.sparql;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class Vocabulary {

	public static final String NS = "http://data.open.ac.uk/kmi/robo/";
	public static final String NS_AREA = "http://data.open.ac.uk/kmi/area/";
	public static final String NS_GRAPH = "http://data.open.ac.uk/kmi/graph/dynamic/";
	public static final String GRAPH_FOREVER = "http://data.open.ac.uk/kmi/graph/static";
	public static final String NS_LOCATION = "http://data.open.ac.uk/kmi/location/";
	public static final Resource Area = ResourceFactory.createResource(NS + "Area");
	public static final Resource Location = ResourceFactory.createResource(NS + "Location");
	public static final Property hasLocation = ResourceFactory.createProperty(NS + "hasLocation");
	public static final Property xCoord = ResourceFactory.createProperty(NS + "xCoord");
	public static final Property yCoord = ResourceFactory.createProperty(NS + "yCoord");
	public static final Property subAreaOf = ResourceFactory.createProperty(NS + "subAreaOf");
	public static final Property hasTemperature = ResourceFactory.createProperty(NS + "hasTemperature");
	public static final Property hasHumidity = ResourceFactory.createProperty(NS + "hasHumidity");
	public static final Property hasPeopleCount = ResourceFactory.createProperty(NS + "hasPeopleCount");
	public static final Property hasWiFiSignal = ResourceFactory.createProperty(NS + "hasWiFiSignal");

	
}
