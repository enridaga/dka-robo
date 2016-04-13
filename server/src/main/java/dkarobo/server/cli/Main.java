package dkarobo.server.cli;

import org.apache.jena.system.JenaSystem;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;

import dkarobo.server.webapp.Application;

public class Main {
	static{
		JenaSystem.DEBUG_INIT = true ;
	}
	public static void main(String[] args) {
		System.out.println("#1: dka-robo::server starting");
		Cli cli = new Cli(args);
		cli.parse();
		Server server = new Server();
		ServerConnector connector = new ServerConnector(server);

		connector.setIdleTimeout(1000 * 60 * 60);
		connector.setSoLingerTime(-1);
		connector.setPort(cli.getParamPort());
		server.setConnectors(new Connector[] { connector });
		System.out.println("#2: server is starting on port " + cli.getParamPort());
		WebAppContext root = new WebAppContext();
		root.setContextPath("/");

		String webxmlLocation = Main.class
				.getResource("/WEB-INF/web.xml").toString();
		root.setDescriptor(webxmlLocation);
		// Pass Cli arguments to Application
		root.setInitParameter(Application._ParamDATA, cli.getParamData());
		root.setInitParameter(Application._ParamLOAD, cli.getParamLoad());
		
		String resLocation = Main.class
				.getResource("/static").toString();
		root.setResourceBase(resLocation);
		root.setParentLoaderPriority(true);
		server.setHandler(root);
		System.out.println("#3: resources setup");

		try {
			server.start();
			System.out.println("#4: enjoy");
			server.join();
			System.out.println("#5: stopping server");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(100);
		}
		System.out.println("#6: thank you");
	}

}
