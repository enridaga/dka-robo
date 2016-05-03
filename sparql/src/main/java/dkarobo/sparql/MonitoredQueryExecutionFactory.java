package dkarobo.sparql;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.core.DatasetImpl;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryEngineFactory;
import org.apache.jena.sparql.engine.QueryEngineRegistry;
import org.apache.jena.sparql.engine.QueryExecutionBase;
import org.apache.jena.sparql.engine.main.OpExecutor;
import org.apache.jena.sparql.engine.main.OpExecutorFactory;
import org.apache.jena.sparql.engine.main.QC;
import org.apache.jena.sparql.util.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MonitoredQueryExecutionFactory {
	
	private static final Logger log = LoggerFactory.getLogger(MonitoredQueryExecutionFactory.class);

	public static QueryExecution create(String query, Dataset dataset, QuadListener listener) {
		Query q = QueryFactory.create(query);
		return create(q, dataset, listener);
	}

	public static QueryExecution create(String query, Model model, QuadListener listener) {
		Query q = QueryFactory.create(query);
		return create(q, new DatasetImpl(model), listener);
	}

	public static QueryExecution create(Query query, Dataset dataset, final QuadListener listener) {
		log.trace("Executing {}", query);
		OpExecutorFactory customExecutorFactory = new OpExecutorFactory() {
			@Override
			public OpExecutor create(ExecutionContext execCxt) {
				return new MonitoredOpExecutor(execCxt, listener);
			}
		};
		
		// XXX This could be done better, as it would create a new context for
		// each query, increasing the size of the QC map.
		Context context = dataset.asDatasetGraph().getContext();
		QC.setFactory(context, customExecutorFactory);
		Query q = QueryFactory.create(query);

		QueryEngineFactory f = QueryEngineRegistry.get().find(q, dataset.asDatasetGraph(), context);
		return new QueryExecutionBase(query, dataset, context, f);
	}
}
