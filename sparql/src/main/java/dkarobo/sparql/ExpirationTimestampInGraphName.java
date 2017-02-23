package dkarobo.sparql;

import org.apache.jena.graph.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExpirationTimestampInGraphName implements ValidityReader {
	Logger log = LoggerFactory.getLogger(ExpirationTimestampInGraphName.class);
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
				log.error(graphName);
			}
		}
		// Valid
		//return 1000000000;//
		return nowMilliseconds + 1000000;
	}

	@Override
	public int elapsingSeconds(String g, Triple t) {
		long millisec = milliseconds(g);
		log.trace("{} {} :: {} / {}",new Object[]{g,t, millisec, nowMilliseconds});
		return ((Long) ((millisec - nowMilliseconds) / 1000)).intValue();
	}
}
