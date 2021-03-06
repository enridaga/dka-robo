package dkarobo.server.webapp;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.jena.query.Dataset;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.tdb.base.file.Location;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dkarobo.server.plans.DKAManager;
import dkarobo.server.plans.PlansCache;

public class Application extends ResourceConfig implements ServletContextListener {
	private static final Logger log = LoggerFactory.getLogger(Application.class);
	// Objects in context
	public final static String _ObjectMANAGER = "dka-manager";
	public final static String _ObjectPLANSCACHE = "dka-plans-cache";
	public final static String _ObjectBOT = "dka-bot";
	// Parameters
	public final static String _ParamDATA = "dka-data";
	public final static String _ParamLOAD = "dka-load";
	public final static String _ParamINIT = "dka-init";

	public final static String _ObjectDataset = "_dataset";

	public Application() {
		packages("dkarobo.server.webapp.rest");
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		log.info("Initializing context.");

		log.debug("Setting up database");
		Dataset dataset;
		String dataString = sce.getServletContext().getInitParameter(_ParamDATA);
		log.debug("Datastring? {}", dataString);
		if (dataString == null) {
			throw new RuntimeException("Cannot setup database. Missing init parameter: " + _ParamDATA);
		}
		File data = (File) new File(dataString);
		if (!data.exists()) {
			log.debug("Creating directory " + data);
			data.mkdirs();
		}
		if (data.canRead() && data.canWrite() && data.canExecute()) {
			dataset = TDBFactory.createDataset(Location.create(data.getAbsolutePath()));
			sce.getServletContext().setAttribute(_ObjectDataset, dataset);
		} else {
			throw new RuntimeException("Cannot setup database. Not enough permissions on folder " + data);
		}
		String paramLOAD = sce.getServletContext().getInitParameter(_ParamLOAD);
		log.debug("Load? param={}", paramLOAD);
		if (paramLOAD != null && !paramLOAD.isEmpty()) {
			File load = new File(paramLOAD);
			log.debug("Loading data from {}", load);
			try {
				RDFDataMgr.read(dataset, load.getAbsolutePath(), Lang.NQUADS);
			} catch (Exception e) {
				throw new RuntimeException("Cannot load data.");
			}
		}

		DKAManager manager = new DKAManager(dataset);
		PlansCache cache = new PlansCache();
		sce.getServletContext().setAttribute(_ObjectPLANSCACHE, cache);
		sce.getServletContext().setAttribute(_ObjectMANAGER, manager);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {

	}

}
