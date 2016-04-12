package dkarobo.server.webapp.rest;

import org.glassfish.jersey.uri.internal.UriTemplateParser;
import org.junit.Assert;
import org.junit.Test;

public class UriTest {

	@Test
	public void test() {
		UriTemplateParser parser = new UriTemplateParser("/{endpoint:(query|sparql)}");
		Assert.assertTrue(parser.getPattern().matcher("/query").matches());
		Assert.assertTrue(parser.getPattern().matcher("/sparql").matches());
	}
}
