package dkarobo.server.webapp.rest;

import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dkarobo.server.plans.DKAManager;
import dkarobo.server.webapp.Application;

@Path("/data")
public class DataEndpoint {
	
	private static Logger log = LoggerFactory.getLogger(QueryEndpoint.class);

	@Context
	protected HttpHeaders requestHeaders;

	@PathParam("endpoint")
	private String endpoint;

	@Context
	private ServletContext context;

	@Context
	protected UriInfo requestUri;

	@PUT
	@Consumes("application/n-quads")
	public Response put(InputStream data) {
		log.trace("Calling PUT /data");
		Dataset dataset = (Dataset) context.getAttribute(Application._ObjectDataset);
		synchronized(dataset){
			dataset.begin(ReadWrite.WRITE);
			try {
				RDFDataMgr.read(dataset, data, Lang.NQUADS);
				dataset.commit();
			} catch (Exception e) {
				dataset.abort();
				throw new RuntimeException("Cannot load data.");
			}finally{
				dataset.end();
			}
		}
		//
		DKAManager manager = (DKAManager) context.getAttribute(Application._ObjectMANAGER);
		manager.reloadLocations(); // refresh locations if data has changed
		return Response.ok().build();
	}
}
