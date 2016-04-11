package dkarobo.sparql;

import java.util.List;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.query.ARQ;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.QueryEngineFactory;
import com.hp.hpl.jena.sparql.engine.QueryEngineRegistry;
import com.hp.hpl.jena.sparql.engine.QueryExecutionBase;
import com.hp.hpl.jena.sparql.engine.main.OpExecutor;
import com.hp.hpl.jena.sparql.engine.main.OpExecutorFactory;
import com.hp.hpl.jena.sparql.engine.main.QC;

import dkarobo.sparql.VOpExecutor;
import dkarobo.sparql.VQueryExecution;
import dkarobo.sparql.VQueryExecutionFactory;
import dkarobo.sparql.VRoboProblemBuilder;
import dkarobo.sparql.Vocabulary;
import harmony.core.api.fact.Fact;
import harmony.core.impl.renderer.RendererImpl;
import uk.open.ac.kmi.robo.dka.planner.RoboProblem;

public class QueryTest {
	private final static Logger l = LoggerFactory.getLogger(QueryTest.class);

	/**
	 * 
	 */
	@Test
	public void queryTest() {
		String q = "SELECT (MAX(?O) as ?M) WHERE { GRAPH ?g { ?S <" + Vocabulary.NS + "hasHumidity> ?O . ?S <" + Vocabulary.NS + "hasTemperature> ?X } } ";
		Dataset kb = DatasetFactory.createMem();//
		// new DatasetImpl(ModelFactory.createDefaultModel());
		RDFDataMgr.read(kb, getClass().getClassLoader().getResourceAsStream("./KB.txt"), Lang.NQ);
		final VRoboProblemBuilder problemBuilder = new VRoboProblemBuilder(Vocabulary.NS_GRAPH);
		OpExecutorFactory customExecutorFactory = new OpExecutorFactory() {
			@Override
			public OpExecutor create(ExecutionContext execCxt) {
				return new VOpExecutor(execCxt, problemBuilder);
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
		
		VQueryExecution qe = VQueryExecutionFactory.create(query, dataset);
		ResultSet rs = qe.execSelect();
		while (rs.hasNext()) {
			l.info("res: {}", rs.next());
		}
		l.info("res: {}", rs.getRowNumber());
		qe.close();

		RoboProblem problem = qe.getProblemBuilder().getProblem();
		List<Fact> facts = problem.getInitialState().getFacts();
		for (Fact fa : facts) {
			l.info("{}", new RendererImpl().append(fa).toString());
		}
	}
}
