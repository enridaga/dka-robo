package dkarobo.planner;

import static dkarobo.planner.things.Symbols.Forever;
import static dkarobo.planner.things.Symbols.Quad;
import static dkarobo.planner.things.Symbols._anything;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dkarobo.planner.things.Validity;
import harmony.core.api.fact.Fact;
import harmony.planner.bestfirst.Node;
import harmony.planner.bestfirst.heuristic.BestNodeHeuristic;

public class RoboBestNode implements BestNodeHeuristic {

	private Map<Node, Integer> minValidityCache = new HashMap<Node, Integer>();
	private Map<Node, Integer> avgValidityCache = new HashMap<Node, Integer>();

	@Override
	public int compare(Node o1, Node o2) {
		int o1v = minValidity(o1);
		int o2v = minValidity(o2);
		o1v += avgValidity(o1);
		o2v += avgValidity(o1);

		if (o1v < o2v) {
			return -1;
		} else if (o1v > o2v) {
			return 1;
		}
		// if(o1.getDepth() < o2.getDepth()){
		// return -1;
		// }else if(o1.getDepth() > o2.getDepth()){
		// return 1;
		// }
		return 0;
	}

	@Override
	public int getGoalDistance(Node node) {
		return computeAvgValidity(node);
	}

	private int minValidity(Node n) {
		if (!minValidityCache.containsKey(n)) {
			minValidityCache.put(n, computeMinValidity(n));
		}
		return minValidityCache.get(n);
	}

	private int avgValidity(Node n) {
		if (!avgValidityCache.containsKey(n)) {
			avgValidityCache.put(n, computeAvgValidity(n));
		}
		return avgValidityCache.get(n);
	}

	public static Integer computeAvgValidity(Node n) {
		List<Fact> ff = n.getFactRegistry().getFacts(Quad);
		int sum = 0;
		int num = 0;
		for (Fact f : ff) {
			if (f.getThing(0).equals(Forever) || f.getThing(0).equals(_anything))
				continue;
			int v = ((Validity) f.getThing(0)).asInteger();
			num++;
			sum += v;
		}
		if(num == 0) return 0;
		return sum / num;
	}

	public static Integer computeMinValidity(Node n) {
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
