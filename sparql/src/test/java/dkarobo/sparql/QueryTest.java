package dkarobo.sparql;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.jena.query.ARQ;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryEngineFactory;
import org.apache.jena.sparql.engine.QueryEngineRegistry;
import org.apache.jena.sparql.engine.QueryExecutionBase;
import org.apache.jena.sparql.engine.main.OpExecutor;
import org.apache.jena.sparql.engine.main.OpExecutorFactory;
import org.apache.jena.sparql.engine.main.QC;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dkarobo.planner.RoboProblem;
import harmony.core.api.fact.Fact;
import harmony.core.impl.renderer.RendererImpl;

public class QueryTest {
	private final static Logger l = LoggerFactory.getLogger(QueryTest.class);

	/**
	 * 
	 */
	@Ignore
	@Test
	public void queryTest() {
		String q = "SELECT (MAX(?O) as ?M) WHERE { GRAPH ?g { ?S <" + Vocabulary.NS + "hasHumidity> ?O . ?S <" + Vocabulary.NS + "hasTemperature> ?X } } ";
		
		Dataset kb = DatasetFactory.createGeneral(); // createMem = deprecated
		RDFDataMgr.read(kb, getClass().getClassLoader().getResourceAsStream("./KB.txt"), Lang.NQ);
		ValidityReader provider = new ExpirationTimestampInGraphName(Vocabulary.NS_GRAPH, System.currentTimeMillis());
		final InvalidQuadCollector collector = new InvalidQuadCollector(provider);
		OpExecutorFactory customExecutorFactory = new OpExecutorFactory() {
			@Override
			public OpExecutor create(ExecutionContext execCxt) {
				return new MonitoredOpExecutor(execCxt, collector);
			}
		};

		QC.setFactory(ARQ.getContext(), customExecutorFactory);
		Query query = QueryFactory.create(q);
		QueryExecution ex;// = QueryExecutionFactory.create(q, kb);
		QueryEngineFactory f = QueryEngineRegistry.get().find(query, kb.asDatasetGraph(), ARQ.getContext());
		ex = new QueryExecutionBase(query, kb, ARQ.getContext(), f);
		// System.out.println(ARQ.getContext().equals(ex.getContext()));
		ResultSet rs = ex.execSelect();
		while (rs.hasNext()) {
			rs.next();
		}
		l.info("res: {}", rs.getRowNumber());
		ex.close();

		RoboProblemBuilder problemBuilder = new RoboProblemBuilder(collector);
		RoboProblem problem = problemBuilder.getProblem();
		List<Fact> facts = problem.getInitialState().getFacts();
		for (Fact fa : facts) {
			l.info("{}", new RendererImpl().append(fa).toString());
		}
	}
	
	@Test
	public void testValidityProvider(){
		// read rules
		Map<String, String> rules = readRules();
		// for each rule, create an inMemDS
		
		String[] roboTriple = ("http://data.open.ac.uk/kmi/location/Room22 " + Vocabulary.NS + "hasTemperature 23").split(" ");
//		System.out.println(Arrays.toString(roboTriple));
		
		
		String bestMatchingRule = "";
		int validityBestMatch = 1000000;
		Map<String,String> matchingRules = new HashMap<String,String>(); // Do we need them?
		
		for ( String rule : rules.keySet()){
			
			// create DS
			Dataset kb = DatasetFactory.createGeneral();
			RDFDataMgr.read(kb, getClass().getClassLoader().getResourceAsStream("./KB_partial.nq"), Lang.NQ);
			
			// new model from triple
			Model model =  ModelFactory.createDefaultModel();
			Resource sbj = model.createResource(roboTriple[0]);
			Property prop =  model.createProperty(roboTriple[1]);
			Literal obj = model.createTypedLiteral(Long.parseLong(roboTriple[2]));
			model.add(model.createLiteralStatement(sbj,prop,obj));
			
			// add model from triple
			kb.addNamedModel(Vocabulary.NS_GRAPH+"0", model); // TODO name of graph?
			
//			RDFDataMgr.write(System.out, kb, RDFFormat.NQ); 
			
			
			// create query
//			System.out.println(rule);
			String q = "ASK { GRAPH ?g  {"+rule +"} } ";
			System.out.println(q);
			Query query = QueryFactory.create(q);	
			QueryExecution qe = QueryExecutionFactory.create(query, kb);

			if (kb.supportsTransactions()) {
				kb.begin(ReadWrite.READ);
			}
			boolean rs = qe.execAsk();
			
			if (rs){
				matchingRules.put(rule, rules.get(rule));
				
				if (Integer.parseInt(rules.get(rule)) < validityBestMatch){
					bestMatchingRule = rule;
					validityBestMatch = Integer.parseInt(rules.get(rule));
				} // get the most specific
				// what if they are the same?
				
			}
			
			
			if (kb.supportsTransactions()) {
				kb.end();
			}
			
			
			// if currentTime - declaredValidity < its own validity
			
			
		}
		System.out.println(matchingRules);
		System.out.println(bestMatchingRule+" "+validityBestMatch);
		
		// return this
		System.out.println("Will expire at"+ validityBestMatch+System.currentTimeMillis());
		
	}

	
	private Map<String,String> readRules() {
		String  filename = "./src/test/resources/rules.csv";
		try {
			Map<String,String> rules = new HashMap<String,String>();
			
			@SuppressWarnings("unchecked")
			List<String> lines = IOUtils.readLines(new FileInputStream(new File (filename)));
			
			
			for (String line : lines  ){
				rules.put(line.split(",")[0],line.split(",")[1]);
			}
			return rules;
		} catch ( IOException e) {
			System.out.println("File "+new File(filename).getAbsolutePath()+" not found");
			return Collections.emptyMap();
		}
	}
	
	@Test
	@Ignore
	public void testFactory(){
		String query = "SELECT (MAX(?O) as ?M) WHERE { GRAPH ?g { ?S <" + Vocabulary.NS + "hasHumidity> ?O . ?S <" + Vocabulary.NS + "hasTemperature> ?X } } ";
		Dataset dataset = DatasetFactory.createMem();//
		// new DatasetImpl(ModelFactory.createDefaultModel());
		RDFDataMgr.read(dataset, getClass().getClassLoader().getResourceAsStream("./KB_partial.nq"), Lang.NQ);
		
		ValidityReader provider = new ExpirationTimestampInGraphName(Vocabulary.NS_GRAPH, System.currentTimeMillis());
		final InvalidQuadCollector collector = new InvalidQuadCollector(provider);
		QueryExecution qe = MonitoredQueryExecutionFactory.create(query, dataset, collector);
		ResultSet rs = qe.execSelect();
		while (rs.hasNext()) {
			l.info("res: {}", rs.next());
		}
		l.info("res: {}", rs.getRowNumber());
		qe.close();

		RoboProblem problem = new RoboProblemBuilder(collector).getProblem();
		List<Fact> facts = problem.getInitialState().getFacts();
		for (Fact fa : facts) {
			l.info("{}", new RendererImpl().append(fa).toString());
		}
		
		
	}
	

}
