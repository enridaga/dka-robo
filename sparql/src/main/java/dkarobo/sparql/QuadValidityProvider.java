package dkarobo.sparql;

/**
 * To implement methods for calculating the seconds of validity of a Quad.
 * 
 * @author enridaga
 *
 */
public interface QuadValidityProvider {

	public int elapsingSeconds(String G, String S, String P, String O);
}