package dkarobo.bot;

import java.net.URL;

public class BotViaRest implements Bot {

	private URL webAddress;

	public BotViaRest(URL webAddress) {

	}

	public URL getWebAddress() {
		return webAddress;
	}

	@Override
	public Coordinates whereAreYou() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isBusy() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String[] getPlan() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendPlan(String[] thePlan) throws BusyBotException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasPlan() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void abort() {
		// TODO Auto-generated method stub

	}

	@Override
	public String whatHaveYouDone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		return new StringBuilder().append(getClass().getSimpleName()).append("[").append(webAddress.toString())
				.append("]").toString();
	}
}
