package uk.open.ac.kmi.robo.dka.labusecase;

import static uk.open.ac.kmi.robo.dka.labusecase.Vocabulary.Area;
import static uk.open.ac.kmi.robo.dka.labusecase.Vocabulary.GRAPH_FOREVER;
import static uk.open.ac.kmi.robo.dka.labusecase.Vocabulary.Location;
import static uk.open.ac.kmi.robo.dka.labusecase.Vocabulary.NS_AREA;
import static uk.open.ac.kmi.robo.dka.labusecase.Vocabulary.NS_GRAPH;
import static uk.open.ac.kmi.robo.dka.labusecase.Vocabulary.NS_LOCATION;
import static uk.open.ac.kmi.robo.dka.labusecase.Vocabulary.hasHumidity;
import static uk.open.ac.kmi.robo.dka.labusecase.Vocabulary.hasLocation;
import static uk.open.ac.kmi.robo.dka.labusecase.Vocabulary.hasPeopleCount;
import static uk.open.ac.kmi.robo.dka.labusecase.Vocabulary.hasTemperature;
import static uk.open.ac.kmi.robo.dka.labusecase.Vocabulary.hasWiFiSignal;
import static uk.open.ac.kmi.robo.dka.labusecase.Vocabulary.subAreaOf;
import static uk.open.ac.kmi.robo.dka.labusecase.Vocabulary.xCoord;
import static uk.open.ac.kmi.robo.dka.labusecase.Vocabulary.yCoord;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.core.DatasetImpl;
import com.hp.hpl.jena.vocabulary.RDF;

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
