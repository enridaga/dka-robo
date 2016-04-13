package dkarobo.sparql;

import java.util.List;

import org.apache.jena.query.ARQ;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryEngineFactory;
import org.apache.jena.sparql.engine.QueryEngineRegistry;
import org.apache.jena.sparql.engine.QueryExecutionBase;
import org.apache.jena.sparql.engine.main.OpExecutor;
import org.apache.jena.sparql.engine.main.OpExecutorFactory;
import org.apache.jena.sparql.engine.main.QC;
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
	@Test
	public void queryTest() {
		String q = "SELECT (MAX(?O) as ?M) WHERE { GRAPH ?g { ?S <" + Vocabulary.NS + "hasHumidity> ?O . ?S <" + Vocabulary.NS + "hasTemperature> ?X } } ";
		Dataset kb = DatasetFactory.createMem();//
		RDFDataMgr.read(kb, getClass().getClassLoader().getResourceAsStream("./KB.txt"), Lang.NQ);
		QuadValidityProvider provider = new QuadValidityComputer(Vocabulary.NS_GRAPH, System.currentTimeMillis());
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
	public void testFactory(){
		String query = "SELECT (MAX(?O) as ?M) WHERE { GRAPH ?g { ?S <" + Vocabulary.NS + "hasHumidity> ?O . ?S <" + Vocabulary.NS + "hasTemperature> ?X } } ";
		Dataset dataset = DatasetFactory.createMem();//
		// new DatasetImpl(ModelFactory.createDefaultModel());
		RDFDataMgr.read(dataset, getClass().getClassLoader().getResourceAsStream("./KB.txt"), Lang.NQ);
		
		QuadValidityProvider provider = new QuadValidityComputer(Vocabulary.NS_GRAPH, System.currentTimeMillis());
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
