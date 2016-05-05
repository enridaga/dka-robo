package dkarobo.bot;

import java.io.IOException;

public interface Bot {

	public Coordinates whereAreYou() throws IOException;

	public boolean isBusy() throws IOException;

	public String[] getPlan();

	public void sendPlan(String[] thePlan) throws BusyBotException, IOException;

	public boolean hasPlan() throws IOException;

	public void abort() throws IOException;

	public String whatHaveYouDone() throws IOException;
}
