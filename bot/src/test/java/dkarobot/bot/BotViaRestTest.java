package dkarobot.bot;

import java.net.MalformedURLException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import dkarobo.bot.BotViaRest;
import dkarobo.bot.BusyBotException;

public class BotViaRestTest {

	BotViaRest bot;

	@Before
	public void before() throws MalformedURLException {
		bot = new BotViaRest("http://localhost/~ed4565/dkarobot/");
	}

	@Ignore
	@Test
	public void whereAmI() {
		System.out.println(bot.whereAreYou());
	}

	@Test
	public void planAndPing() {
		Assert.assertTrue(!bot.isBusy());
		Exception e = null;
		try {
			bot.sendPlan(new String[] { "{\"name\":\"read_humidity\"}" });
		} catch (BusyBotException ex) {
			e = ex;
		}
		Assert.assertTrue(e == null);
		try {
			bot.sendPlan(new String[] { "" });
		} catch (BusyBotException ex) {
			e = ex;
		}
		Assert.assertTrue(e != null);

	}
}
