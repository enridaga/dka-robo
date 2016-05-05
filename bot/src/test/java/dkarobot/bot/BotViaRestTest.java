package dkarobot.bot;

import java.net.MalformedURLException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import dkarobo.bot.BotViaRest;

public class BotViaRestTest {

	BotViaRest bot;

	@Before
	public void before() throws MalformedURLException {
		bot = new BotViaRest("http://localhost:5000");
	}

	@Ignore
	@Test
	public void whereAmI() {
		System.out.println(bot.whereAreYou());
	}
}
