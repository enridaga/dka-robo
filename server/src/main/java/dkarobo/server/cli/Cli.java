package dkarobo.server.cli;

import java.io.File;
import java.io.PrintStream;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Cli {
	private int port = 8080;
	private String data = "./dka-server-data";
	private String load = null;

	private String[] args;

	private static PrintStream O = System.out;
	private static PrintStream E = System.err;

	public int getParamPort() {
		return port;
	}

	private Options options = new Options();

	public Cli(String[] args) {
		this.args = args;
		options.addOption("h", "help", false, "Show this help.");
		options.addOption("p", "port", true, "Set the port the server will listen to (defaults to 8080).");
		options.addOption("l", "load", true, "Load this file (must be in N-QUAD format).");
		options.addOption("d", "data", true, "Database directory (default is ./dka-data).");
	}

	private void help() {
		String syntax = "java [java-opts] -jar [jarfile] ";
		new HelpFormatter().printHelp(syntax, options);
		System.exit(0);
	}

	/**
	 * Parses command line arguments and acts upon them.
	 */
	public void parse() {
		CommandLineParser parser = new BasicParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
			if (cmd.hasOption('h'))
				help();
			if (cmd.hasOption('p')) {
				port = Integer.parseInt(cmd.getOptionValue('p'));
				if (port < 0 && port > 65535) {
					O.println("Invalid port number " + port + ". Must be in the range [0,65535].");
					System.exit(100);
				}
			}

			if (cmd.hasOption('d')) {
				String dataDir = cmd.getOptionValue('d');
				File dataFile = new File(dataDir);
				if (dataFile.exists() && !dataFile.canRead()) {
					throw new RuntimeException(
							"Cannot read database in existing directory! Please provide another directory.");
				}
				data = dataFile.getAbsolutePath();
			}
			if (cmd.hasOption('l')) {
				File loadFile = new File(cmd.getOptionValue('l'));
				if (!loadFile.exists() || !loadFile.canRead()) {
					throw new RuntimeException("Cannot load " + load + ". File does not exists or is not readable.");
				}
				load = loadFile.getAbsolutePath();
			}

		} catch (ParseException e) {
			E.println("Failed to parse comand line properties");
			e.printStackTrace();
			help();
		}
	}

	public String getParamData() {
		return data;
	}

	public String getParamLoad() {
		return load;
	}
}
