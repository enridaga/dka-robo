package dkarobo.sparql;

import static dkarobo.sparql.Vocabulary.Area;
import static dkarobo.sparql.Vocabulary.GRAPH_FOREVER;
import static dkarobo.sparql.Vocabulary.Location;
import static dkarobo.sparql.Vocabulary.NS_AREA;
import static dkarobo.sparql.Vocabulary.NS_GRAPH;
import static dkarobo.sparql.Vocabulary.NS_LOCATION;
import static dkarobo.sparql.Vocabulary.hasHumidity;
import static dkarobo.sparql.Vocabulary.hasLocation;
import static dkarobo.sparql.Vocabulary.hasPeopleCount;
import static dkarobo.sparql.Vocabulary.hasTemperature;
import static dkarobo.sparql.Vocabulary.hasWiFiSignal;
import static dkarobo.sparql.Vocabulary.subAreaOf;
import static dkarobo.sparql.Vocabulary.xCoord;
import static dkarobo.sparql.Vocabulary.yCoord;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.core.DatasetImpl;
import org.apache.jena.vocabulary.RDF;

public class KnowledgeBaseBuilder {
	private String file;

	public KnowledgeBaseBuilder(String coordinatesFile) throws FileNotFoundException, IOException {
		file = coordinatesFile;
	}

	public Dataset build() throws FileNotFoundException, IOException {
		@SuppressWarnings("unchecked")
		List<String> lines = IOUtils.readLines(new FileInputStream(file));
		Model forever = ModelFactory.createDefaultModel();
		Model def = ModelFactory.createDefaultModel();
		for (String line : lines) {
			String[] l = line.split(",");
			String area = l[0];
			String subarea = l[1];
			String x = l[2];
			String y = l[3];
			forever.add(buildForeverTriples(area, subarea, x, y));
			def.add(buildObservedDefaults(subarea));
		}
		Dataset ds = new DatasetImpl(ModelFactory.createDefaultModel());
		ds.addNamedModel(GRAPH_FOREVER, forever);
		ds.addNamedModel(NS_GRAPH + "0", def);
		return ds;
	}

	private Model buildObservedDefaults(String location) {
		Property[] o = new Property[] { hasTemperature, hasHumidity, hasWiFiSignal, hasPeopleCount };
		Model model = ModelFactory.createDefaultModel();
		Resource area_r = model.createResource(NS_LOCATION + location);
		for (Property p : o) {
			area_r.addLiteral(p, -1);
		}
		return model;
	}

	private Model buildForeverTriples(String area, String subarea, String x, String y) {
		Model model = ModelFactory.createDefaultModel();
		Resource area_r = model.createResource(NS_AREA + area);
		area_r.addProperty(RDF.type, Area);
		Resource subarea_r = model.createResource(NS_AREA + subarea);
		subarea_r.addProperty(RDF.type, Area);
		subarea_r.addProperty(subAreaOf, area_r);
		Resource location_r = model.createResource(NS_LOCATION + subarea);
		location_r.addProperty(RDF.type, Location);
		subarea_r.addProperty(hasLocation, location_r);
		location_r.addProperty(xCoord, x);
		location_r.addProperty(yCoord, y);
		return model;
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		Dataset m = new KnowledgeBaseBuilder(args[0]).build();
		File o = new File(args[1]);
		o.delete();
		RDFDataMgr.write(new FileOutputStream(o), m, Lang.NQUADS);
	}
}
