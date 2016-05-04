package dkarobo.server.plans;

import java.util.HashMap;
import java.util.Map;

import harmony.core.api.plan.Plan;

public class PlansCache {

	private Map<String, Plan> cache = new HashMap<String, Plan>();

	public boolean isCached(String query) {
		return cache.containsKey(query);
	}

	public void removeCached(String query) {
		cache.remove(query);
	}

	public void emptyCache() {
		cache = new HashMap<String, Plan>();
	}

	public void put(String string, Plan plan) {
		cache.put(string, plan);
	}

	public Plan get(String query) {
		return cache.get(query);
	}
}
