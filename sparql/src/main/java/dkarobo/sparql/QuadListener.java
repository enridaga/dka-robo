package dkarobo.sparql;

import org.apache.jena.graph.Triple;

public interface QuadListener {
	public void quad(String graph, Triple triple);
}
