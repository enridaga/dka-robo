package dkarobo.planner;

import java.util.HashMap;
import java.util.Map;

public class SinglePointsMapCostProvider implements MoveCostProvider {

	private int speed;
	private int meter;
	private Map<String, Double[]> coordinates = new HashMap<String, Double[]>();

	public void setMeterIs(int length) {
		meter = length;
	}

	public void setSpeed(int metersInSeconds) {
		this.speed = metersInSeconds;
	}

	public void addCoordinates(String location, double X, double Y) {
		coordinates.put(location, new Double[] { X, Y });
	}

	@Override
	public int validityDecreaseFactor(String from, String to) {
		double x1;
		double y1;
		double x2;
		double y2;
		try {
			x1 = coordinates.get(from)[0];
			y1 = coordinates.get(from)[1];
			x2 = coordinates.get(to)[0];
			y2 = coordinates.get(to)[1];
		} catch (NullPointerException e) {
			throw new RuntimeException("Missing location coordinates", e);
		}
		double distance = Math.hypot(x1 - x2, y1 - y2) / meter;
		double time = distance / speed;
		return ((Double) time).intValue();
	}
}
