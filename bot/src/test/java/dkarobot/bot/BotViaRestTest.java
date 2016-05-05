package dkarobot.bot;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import dkarobo.bot.BotViaRest;
import dkarobo.bot.BusyBotException;

public class BotViaRestTest {

	BotViaRest bot;

	@Before
	public void before() throws IOException {
		bot = new BotViaRest("http://localhost/~ed4565/dkarobot/");
		if(bot.isBusy()){
			bot.abort();
		}
	}

	@Ignore
	@Test
	public void whereAmI() {
		System.out.println(bot.whereAreYou());
	}

	
	@Test
	public void planAndPing() throws IOException {
		
		Assert.assertTrue(!bot.isBusy());
		Exception e = null;
		try {
			bot.sendPlan(new String[] { "{\"name\":\"goto\",\"x\":7.9,\"y\":5.0,\"t\":0 }", "{\"name\":\"read_humidity\"}" });
		} catch (BusyBotException ex) {
			e = ex;
		}
		Assert.assertTrue(e == null);
		Assert.assertTrue(bot.isBusy());
		try {
			bot.sendPlan(new String[] { "" });
		} catch (BusyBotException ex) {
			e = ex;
		}
		Assert.assertTrue(e != null);
		Assert.assertTrue(bot.isBusy());
		//bot.abort();
		//Assert.assertTrue(!bot.isBusy());
	}
}
