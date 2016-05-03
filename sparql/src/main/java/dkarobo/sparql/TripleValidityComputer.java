package dkarobo.sparql;

import java.util.Date;

import org.apache.jena.graph.Triple;

public interface TripleValidityComputer {
	public long useByTimestamp(Triple triple, Date now);
	public long useByTimestamp(Triple triple);
	public int seconds(Triple triple);
}
