package dkarobo.planner;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




/**
 * @author ilariatiddi
 * class for Stage (roomba simulator)
 */
public class StageMoveCostProvider implements MoveCostProvider {
	private static final Logger log = LoggerFactory.getLogger(StageMoveCostProvider.class);
	private double speed = 1; // defaul is null
	private double meter = 1 ;
	
	private Map<String, Double[]> locations = new HashMap<String, Double[]>();

	
	public void setMeterIs(double length) {
		meter = length;
	}

	public void setSpeed(double metersInSeconds) {
		this.speed = metersInSeconds;
	}

	public void addCoordinates(String location, double X, double Y) {
		locations.put(location, new Double[] { X, Y });
	}

	@Override
	public int validityDecreaseFactor(String A, String B) {
		if(A.equals(B)) return 0;
	//  A and B are locations with coordinates
		if (locations.get(A) == null || locations.get(B) == null) {
			// log.trace("{} or {} was null", A, B);
			return 0;
		}
			
		double x1 = locations.get(A)[0];
		double y1 = locations.get(A)[1];
		double x2 = locations.get(B)[0];
		double y2 = locations.get(B)[1]; 
		
		
//		double distance = Math.hypot(x1 - x2, y1 - y2) / meter;
//		double time = distance / speed;
//		return ((Double) time).intValue();
		
		
		//double distance = Math.sqrt( (Math.pow(  y1 - x1 ,2 )  +   Math.pow(  y2 - x2 ,2 )) )  ;
		double distance = Math.sqrt( (Math.pow(  y1 - y2 ,2 )  +   Math.pow(  x1 - x2 ,2 )) )  ;
		double distInMeters = distance / meter;
		double time = distInMeters / speed;
		log.trace("{} to {} in {} seconds",new Object[]{A,B,((Double) time).intValue()});
		return ((Double) time).intValue();
	}
}
