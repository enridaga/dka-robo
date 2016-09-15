package dkarobo.bot;

import java.io.IOException;

public class DummyBot implements Bot {
	private Thread doing = null;
	private Coordinates coords;
	private String[] plan = null;

	public DummyBot(int x, int y, int z) {
		setCoordinates(x, y, z);
	}

	public DummyBot(Coordinates start) {
		coords = start;
	}

	public void setCoordinates(int x, int y, int z) {
		coords = Position.create(x, y, z);
	}

	public void setCoordinates(Coordinates c) {
		coords = c;
	}

	@Override
	public Coordinates whereAreYou() {
		return coords;
	}

	@Override
	public String whatHaveYouDone() {
		return plan != null ? plan[0] : "";
	}

	@Override
	public boolean isBusy() {
		return doing != null && doing.isAlive();
	}

	@Override
	public String[] getPlan() {
		return this.plan;
	}

	@Override
	public void sendPlan(String[] thePlan) throws BusyBotException {
		if (isBusy())
			throw new BusyBotException();

		this.plan = thePlan;
		this.doing = (new Thread(new FakePlanExecution(plan)));
		this.doing.start();
	}

	@Override
	public boolean hasPlan() {
		return this.plan != null;
	}

	public class FakePlanExecution implements Runnable {
		private String[] plan;

		public FakePlanExecution(String[] plan) {
			this.plan = plan;
		}

		public void run() {
			System.out.println("I am the great pretender");
			int duration = plan.length * 3000;
			// at least 10 seconds
			if (duration < 10000) {
				duration = 10000;
			}
			long stop = System.currentTimeMillis() + (duration);
			while (System.currentTimeMillis() < stop) {
				try {
					Thread.sleep(1000);
					System.out.println("(doing)");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("(finished)");
		}
	}

	@Override
	public void abort() {
		if (isBusy()) {
			this.doing.interrupt();
			this.doing = null;
			this.plan = null;
		}
	}
	
	@Override
	public String toString() {
		return "DummyBot";
	}

	@Override
	public String currentPlan() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
}
