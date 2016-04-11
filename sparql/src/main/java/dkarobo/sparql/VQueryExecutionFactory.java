package dkarobo.sparql;

import com.hp.hpl.jena.query.ARQ;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.core.DatasetImpl;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.QueryEngineFactory;
import com.hp.hpl.jena.sparql.engine.QueryEngineRegistry;
import com.hp.hpl.jena.sparql.engine.main.OpExecutor;
import com.hp.hpl.jena.sparql.engine.main.OpExecutorFactory;
import com.hp.hpl.jena.sparql.engine.main.QC;
import com.hp.hpl.jena.sparql.util.Context;
import com.hp.hpl.jena.sparql.util.Symbol;

public class VQueryExecutionFactory {

	public static VQueryExecution create(String query, Dataset dataset) {
		Query q = QueryFactory.create(query);
		return create(q, dataset);
	}
	
	public static VQueryExecution create(String query, Model model) {
		Query q = QueryFactory.create(query);
		return create(q, new DatasetImpl(model));
	}
	
	public static VQueryExecution create(Query query, Dataset dataset) {
		final VRoboProblemBuilder problemBuilder = new VRoboProblemBuilder(Vocabulary.NS_GRAPH);
		OpExecutorFactory customExecutorFactory = new OpExecutorFactory() {
			@Override
			public OpExecutor create(ExecutionContext execCxt) {
				return new VOpExecutor(execCxt, problemBuilder);
			}
		};
		// XXX This could be done better, as it would create a new context for each query, increasing the size of the QC map.
		Context context = new Context();
		context.put(Symbol.create("problemBuilder"), problemBuilder);
		QC.setFactory(context, customExecutorFactory);
		Query q = QueryFactory.create(query);
		VQueryExecution ex;// = QueryExecutionFactory.create(q, kb);
		QueryEngineFactory f = QueryEngineRegistry.get().find(q, dataset.asDatasetGraph());
		ex = new VQueryExecutionBase(query, dataset, context, f) {
			@Override
			public VRoboProblemBuilder getProblemBuilder() {
				return problemBuilder;
			}
		};
		return ex;
	}
}
