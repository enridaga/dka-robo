package dkarobo.server.webapp;

import java.io.IOException;

import harmony.core.api.plan.Plan;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.tdb.TDBFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import dkarobo.bot.BotViaRest;
import dkarobo.bot.BusyBotException;
import dkarobo.bot.Position;
import dkarobo.planner.utils.ReportPrinter;
import dkarobo.server.plans.DKAManager;

public class DKAManagerTest {

	private Dataset dataset;
	
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
