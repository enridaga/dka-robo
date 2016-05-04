package dkarobo.sparql;

import org.apache.jena.graph.Triple;

public class ExpirationTimestampInGraphName implements ValidityReader {

	private String timeGraphNs;
	private long nowMilliseconds;

	public ExpirationTimestampInGraphName(String graphNs, long nowMilliseconds) {
		this.timeGraphNs = graphNs;
		this.nowMilliseconds = nowMilliseconds / 1000;
	}

	private long seconds(String graphName) {
		if (graphName.startsWith(timeGraphNs)) {
			try {
				return Long.parseLong(graphName.substring(timeGraphNs.length()));
			} catch (NumberFormatException e) {
				// Not a time graph
			}
		}
		// Valid
		return 100000;
	}

	@Override
	public int elapsingSeconds(String g, Triple t) {
		long millisec = seconds(timeGraphNs);
		return ((Long) ((millisec - nowMilliseconds) / 1000)).intValue();
	}
}
