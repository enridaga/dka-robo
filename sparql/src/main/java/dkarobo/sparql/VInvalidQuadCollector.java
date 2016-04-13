package dkarobo.sparql;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VInvalidQuadCollector implements VQuadListener, QuadCollector {
	private static final Logger log = LoggerFactory.getLogger(VInvalidQuadCollector.class);
	private VQuadValidityProvider provider;
	private Set<String[]> invalid;

	public VInvalidQuadCollector(VQuadValidityProvider provider) {
		this.provider = provider;
		this.invalid = new HashSet<String[]>();
	}

	@Override
	public void quad(String G, String S, String P, String O) {
		log.trace("Scanning quad {} {} {} {}", new Object[] { G, S, P, O });
		int validity = provider.elapsingSeconds(G, S, P, O);
		if (validity < 0) {
			log.trace(" - invalid quad");
			// Invalid Quad
			invalid.add(new String[] { Integer.toString(validity), S, P, O });
		}
	}

	public Set<String[]> getTriples() {
		return Collections.unmodifiableSet(invalid);
	}
}
