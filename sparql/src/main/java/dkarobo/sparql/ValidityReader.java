package dkarobo.sparql;

import org.apache.jena.graph.Triple;

/**
 * To implement methods for reading the seconds of validity of a Quad.
 * 
 * @author enridaga
 *
 */
public interface ValidityReader {

	public int elapsingSeconds(String g, Triple t);
}
