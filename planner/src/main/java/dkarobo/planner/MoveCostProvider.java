package dkarobo.planner;

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
