package dkarobo.bot;

public interface Bot {

	public Coordinates whereAreYou();

	public boolean isBusy();

	public String[] getPlan();

	public void sendPlan(String[] thePlan) throws BusyBotException;

	public boolean hasPlan();

	public void abort();

	public String whatHaveYouDone();
}
