package dkarobo.sparql;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.core.DatasetImpl;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryEngineFactory;
import org.apache.jena.sparql.engine.QueryEngineRegistry;
import org.apache.jena.sparql.engine.main.OpExecutor;
import org.apache.jena.sparql.engine.main.OpExecutorFactory;
import org.apache.jena.sparql.engine.main.QC;
import org.apache.jena.sparql.util.Context;
import org.apache.jena.sparql.util.Symbol;

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
