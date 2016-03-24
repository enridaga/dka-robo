package uk.open.ac.kmi.robo.dka.planner;

public interface MoveCostProvider {
	/**
	 * Seconds required for moving from A to B
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public int validityDecreaseFactor(String A, String B);
}
