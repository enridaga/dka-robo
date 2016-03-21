package uk.open.ac.kmi.robo.planner;

import static uk.open.ac.kmi.robo.planner.things.Symbols.Quad;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import harmony.core.api.fact.Fact;
import harmony.planner.bestfirst.GoalDistanceProvider;
import harmony.planner.bestfirst.Node;
import harmony.planner.bestfirst.ScoreCache;
import harmony.planner.bestfirst.heuristic.BestNodeHeuristic;
import uk.open.ac.kmi.robo.planner.things.Validity;

public class RoboBestNode implements BestNodeHeuristic {

	private Map<Node,Integer> minValidityCache = new HashMap<Node,Integer>();

	@Override
	public int compare(Node o1, Node o2) {
		int o1v = minValidity(o1);
		int o2v = minValidity(o2);
		if(o1v < o2v){
			return -1;
		}else if(o1v > o2v){
			return 1;
		}
//		if(o1.getDepth() < o2.getDepth()){
//			return -1;
//		}else if(o1.getDepth() > o2.getDepth()){
//			return 1;
//		}
		return 0;
	}

	@Override
	public int getGoalDistance(Node node) {
		return 0;
	}

	private int minValidity(Node n) {
		if(!minValidityCache.containsKey(n)){
			minValidityCache.put(n, computeMinValidity(n));
		}
		return minValidityCache.get(n);
	}

	private Integer computeMinValidity(Node n) {
		List<Fact> ff = n.getFactRegistry().getFacts(Quad);
		int min = 0;
		boolean first = true;
		for (Fact f : ff) {
			int v = ((Validity) f.getThing(0)).asInteger();
			if (first) {
				min = v;
				first = false;
			} else if (v < min) {
				min = v;
			}
		}
		return min;
	}
}
