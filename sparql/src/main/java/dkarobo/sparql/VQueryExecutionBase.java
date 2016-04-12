package dkarobo.sparql;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.sparql.engine.QueryEngineFactory;
import org.apache.jena.sparql.engine.QueryExecutionBase;
import org.apache.jena.sparql.util.Context;

public abstract class VQueryExecutionBase extends QueryExecutionBase implements VQueryExecution {

	public VQueryExecutionBase(Query query, Dataset dataset, Context context, QueryEngineFactory qeFactory) {
		super(query, dataset, context, qeFactory);
	}

	public abstract VRoboProblemBuilder getProblemBuilder();
}
