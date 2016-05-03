package dkarobo.sparql;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.jena.graph.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvalidQuadCollector implements QuadListener, QuadCollector {
	private static final Logger log = LoggerFactory.getLogger(InvalidQuadCollector.class);
	private ValidityReader provider;
	private Set<String[]> invalid;

	public InvalidQuadCollector(ValidityReader provider) {
		this.provider = provider;
		this.invalid = new HashSet<String[]>();
	}

	@Override
	public void quad(String G, Triple t) {
		log.trace("Scanning quad {} t", new Object[] { G, t });
		int validity = provider.elapsingSeconds(G, t);
		if (validity < 0) {
			log.trace(" - invalid quad");
			// Invalid Quad
			invalid.add(new String[] { Integer.toString(validity), t.getSubject().toString(true),
					t.getPredicate().toString(true), t.getObject().toString(true) });
		}
	}

	public Set<String[]> getTriples() {
		return Collections.unmodifiableSet(invalid);
	}
}
