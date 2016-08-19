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
//		sb.append(" ");
		for (Node a : ((BacktracePlan) p).getPath()) {
			if (!a.isRoot()) {
//				sb.append("Action: ");
				sb.append("Action: ").append(actionPrettyPrint(new RendererImpl().append(a.getAction().getAction()).toString()));
//				sb.append(new RendererImpl().append(a.getAction().getAction()).toString());
			}
//			sb.append("[").append(RoboBestNode.computeMinValidity(a) + RoboBestNode.computeAvgValidity(a))
//			.append("] \n ");
			sb.append("\n");
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

	private static String actionPrettyPrint(String action) {
		StringBuffer sb = new StringBuffer();
		String[] tokens = action.split("\\[");
		String actionName = tokens[0].substring(1);
		ArrayList<String> arguments = new ArrayList<String>();
		String[] argumentTokens = tokens[1].split(",");
		
		
		for(int i = 0; i < argumentTokens.length; ++i) {
			arguments.add(deUrlify(argumentTokens[i].trim().replaceAll("\\]", "").replaceAll("\\)", "")));
		}
		
		if(actionName.equalsIgnoreCase("move")) {
			sb.append(actionName + " from: " + arguments.get(0) + " to: " + arguments.get(1));
		}
		else if (actionName.equalsIgnoreCase("Temperature")){
			sb.append("Read " + actionName + " at: " + arguments.get(0));
		}
		else if (actionName.equalsIgnoreCase("CheckWifi")){
			sb.append("Sniff Wifi at: " + arguments.get(0));
		}
		else if (actionName.equalsIgnoreCase("CountPeople")){
			sb.append("Count People at: " + arguments.get(0));
		}
		else if (actionName.equalsIgnoreCase("Humidity")){
			sb.append("Read " + actionName + " at: " + arguments.get(0));
		}
		return sb.toString();
	}
	
	private static String deUrlify(String url) {
		return url.substring(url.lastIndexOf("/")+1);
	}
}
