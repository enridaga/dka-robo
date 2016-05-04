package dkarobo.planner.utils;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import dkarobo.planner.RoboBestNode;
import dkarobo.planner.RoboPlanner;
import harmony.core.api.plan.Plan;
import harmony.core.impl.renderer.RendererImpl;
import harmony.planner.bestfirst.BacktracePlan;
import harmony.planner.bestfirst.BestFirstSearchReport;
import harmony.planner.bestfirst.Node;

public class ReportPrinter {

	public static void print(PrintStream ps, RoboPlanner planner) {
		BestFirstSearchReport report = (BestFirstSearchReport) planner.getLastSearchReport();
		print(ps, report);
	}

	public static void print(PrintStream ps, Plan plan) {

	}

	public static String toString(Plan p) {
		StringBuilder sb = new StringBuilder();
		sb.append(" ");
		for (Node a : ((BacktracePlan) p).getPath()) {
			if (!a.isRoot()) {
				sb.append(new RendererImpl().append(a.getAction().getAction()).toString());
			}
			sb.append("[").append(RoboBestNode.computeMinValidity(a) + RoboBestNode.computeAvgValidity(a))
					.append("] / ");
		}
		return sb.toString();
	}

	public static void print(PrintStream ps, BestFirstSearchReport report) {
		List<Plan> plans = new ArrayList<Plan>();
		if (report.goalFound()) {
			plans.add(report.getPlan());
		} else {
			Set<Node> nodes = report.closedNodes();
			for (Node n : nodes) {
				plans.add(new BacktracePlan(n));
			}
		}
		List<String> reportList = new ArrayList<String>();
		// int c = 0;
		for (Plan p : plans) {
			// c++;
			reportList.add(toString(p));
		}
		Collections.sort(reportList);
		int c = 0;
		for (String s : reportList) {
			c++;
			StringBuilder sb = new StringBuilder();
			sb.append(c).append("/").append(report.closedNodes().size());
			ps.println(sb.append(s).toString());
		}
	}
}
