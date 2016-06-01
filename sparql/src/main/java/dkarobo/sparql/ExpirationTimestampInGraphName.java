package dkarobo.sparql;

import org.apache.jena.graph.Triple;

public class ExpirationTimestampInGraphName implements ValidityReader {

	private String timeGraphNs;
	private long nowMilliseconds;

	public ExpirationTimestampInGraphName(String graphNs, long nowMilliseconds) {
		this.timeGraphNs = graphNs;
		this.nowMilliseconds = nowMilliseconds;
	}

	private long milliseconds(String graphName) {
		if (graphName.startsWith(timeGraphNs)) {
			try {
				return Long.parseLong(graphName.substring(timeGraphNs.length()));
			} catch (NumberFormatException e) {
				// Not a time graph
			}
		}
		// Valid
		return 1000000000;// nowMilliseconds + 1000000;
	}

	@Override
	public int elapsingSeconds(String g, Triple t) {
		long millisec = milliseconds(g);
		return ((Long) ((millisec - nowMilliseconds) / 1000)).intValue();
	}
}
