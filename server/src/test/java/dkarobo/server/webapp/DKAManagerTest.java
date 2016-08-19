package dkarobo.server.webapp;

import java.io.IOException;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.tdb.TDBFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dkarobo.bot.BotViaRest;
import dkarobo.bot.BusyBotException;
import dkarobo.bot.Position;
import dkarobo.planner.utils.ReportPrinter;
import dkarobo.server.plans.DKAManager;
import harmony.core.api.plan.Plan;

public class DKAManagerTest {

	private Dataset dataset;
	private Logger l = LoggerFactory.getLogger(DKAManagerTest.class);
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Before
	public void before() throws IOException {
		dataset = TDBFactory.createDataset(folder.newFolder().getAbsolutePath());
		//dataset = DatasetFactory.create();
		RDFDataMgr.read(dataset, getClass().getClassLoader()
				.getResourceAsStream("KB_partial.nq"), Lang.NQUADS);
	}

//	@Ignore
	@Test
	public void test() {
		DKAManager manager = new DKAManager(dataset);
		Plan plan = manager
				.performPlanning(
						"select * where {graph ?g { <http://data.open.ac.uk/kmi/location/Podium> <http://data.open.ac.uk/kmi/robo/hasTemperature> ?t}}",
						Position.create(5, 8, 0));
		ReportPrinter.print(System.out, plan);
		Assert.assertTrue(plan.size() > 0);
	}
	
	@Test
	public void testSendUpdate(){
		String a = "select * where {graph ?g { <http://data.open.ac.uk/kmi/location/Podium> <http://data.open.ac.uk/kmi/robo/hasTemperature> ?t}}";
		
		dataset.begin(ReadWrite.READ);
		QueryExecution qe = QueryExecutionFactory.create(a,dataset);
		ResultSet rs = qe.execSelect();
		Assert.assertTrue(rs.hasNext() == true);
		rs.next();
		Assert.assertTrue(rs.hasNext() == false);
		dataset.end();
		
		DKAManager manager = new DKAManager(dataset);
		manager.roboWrites(manager.toCoordinates("http://data.open.ac.uk/kmi/location/Podium"), "temperature", "25");
		manager.roboWrites(manager.toCoordinates("http://data.open.ac.uk/kmi/location/Podium"), "temperature", "26");
		
		dataset.begin(ReadWrite.READ);
		qe = QueryExecutionFactory.create(a,dataset);
		rs = qe.execSelect();
		Assert.assertTrue(rs.hasNext() == true);
		rs.next();
		Assert.assertTrue(rs.hasNext() == false);
		dataset.end();

		manager.roboWrites(manager.toCoordinates("http://data.open.ac.uk/kmi/location/Podium"), "temperature", "21");
		manager.roboWrites(manager.toCoordinates("http://data.open.ac.uk/kmi/location/Podium"), "temperature", "28");
		dataset.begin(ReadWrite.READ);
		qe = QueryExecutionFactory.create(a,dataset);
		rs = qe.execSelect();
		Assert.assertTrue(rs.hasNext() == true);
		rs.next();
		Assert.assertTrue(rs.hasNext() == false);
		dataset.end();
				
		dataset.begin(ReadWrite.READ);
		 qe = QueryExecutionFactory.create(a,dataset);
		 rs = qe.execSelect();
		while(rs.hasNext()){
			QuerySolution s = rs.next();
			l.debug("{}", s);
			
		}
		Assert.assertTrue(rs.hasNext() == false);
		dataset.end();
		
	}

	
//	@Ignore
	@Test
	public void testLocations() {
		DKAManager manager = new DKAManager(dataset);
		manager.toLocation(Position.create((float) 1, -10, 0));
	}

	@Ignore
	@Test
	public void testSendPlan() throws BusyBotException {
		DKAManager manager = new DKAManager(dataset);
		Plan plan = manager
				.performPlanning(
						"select * where {graph ?g { <http://data.open.ac.uk/kmi/location/MarkBucks> <http://data.open.ac.uk/kmi/robo/hasTemperature> ?t.}} ",
						// + "FILTER contains(str(?activity), 'Activity') . }}",
						Position.create(5, 8, 0));
		ReportPrinter.print(System.out, plan);
		BotViaRest bvr = new BotViaRest("http://137.108.112.211:5000");
		bvr.sendPlan(manager.toBotJsonPlan(plan));

	}

}
