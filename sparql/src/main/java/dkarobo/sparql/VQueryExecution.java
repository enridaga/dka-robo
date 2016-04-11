package dkarobo.sparql;

import com.hp.hpl.jena.query.QueryExecution;

public interface VQueryExecution extends QueryExecution {


	public abstract VRoboProblemBuilder getProblemBuilder();
}
