package dkarobo.sparql;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

public class VQuadValidityComputer implements VQuadValidityProvider {

	private String timeGraphNs;
	private long nowMilliseconds;

	public VQuadValidityComputer(String timeGraphNs, long nowMilliseconds) {
		this.timeGraphNs = timeGraphNs;
		this.nowMilliseconds = nowMilliseconds / 1000;
	}

	@Override
	public int elapsingSeconds(String G, String S, String P, String O) {
		return computeValidity(G, S, P, O);
	}

	private int computeValidity(String g, String s, String p, String o) {
		long millisec = seconds(timeGraphNs);
		// millisec is supposed to be the created time
		Date date = new Date(millisec);
		// TODO
		// XXX Everything is valid for 10 minutes
		DateUtils.addMinutes(date, 10);
		int validity = ((Long) ((date.getTime() - nowMilliseconds) / 1000)).intValue();
		return validity;
	}

	private long seconds(String graphName) {
		if (graphName.startsWith(timeGraphNs)) {
			try {
				return Long.parseLong(graphName.substring(timeGraphNs.length()));
			} catch (NumberFormatException e) {
				// Not a time graph
			}
		}
		return 0;
	}
}
