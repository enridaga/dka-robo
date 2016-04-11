package dkarobo.sparql;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.engine.QueryEngineFactory;
import com.hp.hpl.jena.sparql.engine.QueryExecutionBase;
import com.hp.hpl.jena.sparql.util.Context;

public abstract class VQueryExecutionBase extends QueryExecutionBase implements VQueryExecution {

	public VQueryExecutionBase(Query query, Dataset dataset, Context context, QueryEngineFactory qeFactory) {
		super(query, dataset, context, qeFactory);
	}

	public abstract VRoboProblemBuilder getProblemBuilder();
}
