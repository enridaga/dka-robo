package dkarobot.bot;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import dkarobo.bot.BusyBotException;
import dkarobo.bot.DummyBot;
import dkarobo.bot.Position;

public class DummyBotTest {

	public DummyBotTest() {

	}

	@Test
	public void test() {
		DummyBot bot = new DummyBot(10, 20, 0);
		Assert.assertFalse(bot.isBusy());
		Assert.assertTrue(bot.getPlan() == null);
		Assert.assertFalse(bot.hasPlan());
		Assert.assertTrue(bot.whereAreYou().equals(Position.create(10, 20, 0)));

		String[] plan = new String[] { "do this", "do that", "then wait", "do this again", "do that again",
				"say thank you" };
		try {
			bot.sendPlan(plan);
		} catch (BusyBotException e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}

		Assert.assertTrue(bot.isBusy());
		Assert.assertTrue(bot.getPlan() != null);
		Assert.assertTrue(Arrays.asList(plan).contains(bot.whatHaveYouDone()));
		Assert.assertTrue(bot.hasPlan());
		
		bot.abort();
		Assert.assertFalse(bot.isBusy());
		Assert.assertTrue(bot.getPlan() == null);
		Assert.assertFalse(bot.hasPlan());

	}
}
