package dkarobo.server.webapp.rest;

import java.util.List;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.open.kmi.basil.rendering.MoreMediaType;

public class AboutMediaTypes {
	private static Logger log = LoggerFactory.getLogger(AboutMediaTypes.class);
	
	public static final MediaType getBestAcceptable(HttpHeaders requestHeaders) {
		// This list is sorted by the client preference
		List<MediaType> acceptHeaders = requestHeaders.getAcceptableMediaTypes();
		log.debug("Acceptable media types: {}", acceptHeaders);
		if (acceptHeaders == null || acceptHeaders.size() == 0) {
			// Default type is text/plain
			return MediaType.TEXT_PLAIN_TYPE;
		}

		for (MediaType mt : acceptHeaders) {
			String qValue = mt.getParameters().get("q");
			if (qValue != null && Double.valueOf(qValue).doubleValue() == 0.0) {
				break;
			}

			for (MediaType variant : MoreMediaType.MediaTypes) {
				if (variant.isCompatible(mt)) {
					return variant;
				}
			}

		}
		return null;
	}
}
