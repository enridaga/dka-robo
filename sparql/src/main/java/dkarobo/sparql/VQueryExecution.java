package dkarobo.sparql;

import org.apache.jena.query.QueryExecution;

public interface VQueryExecution extends QueryExecution {


	public abstract VRoboProblemBuilder getProblemBuilder();
}
