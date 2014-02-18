package com.zanclus.scanalyzer;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class to handle loading, parsing, and aggregating the various startup configurations.
 * 
 * @author <a href="https://github.com/InfoSec812">Deven Phillips</a>
 * 
 */
public class Init {

	private Map<String, String> config;

	private boolean shouldExit = false ;

	String[] args = null;

	private static final Logger LOG = LoggerFactory.getLogger(Init.class) ;

	public Init(String[] args) {
		super();
		this.args = args.clone();
	}

	private void printUsage() {
		System.out
				.println("scanalyzer [-c <config file>] [-b <bind address>] [-p <bind port>] [-t <thread count>]");
		System.out
				.println("	--config=<config file> || -c <config file>         The path to a configuration file (Default: Checks for /etc/scanalyzer/scanalyzer.conf).");
		System.out
				.println("	--bind=<bind address> || -b <bind address>         The address on which to listen on for web clients (defaults to 127.0.0.1).");
		System.out
				.println("	--port=<bind port> || -p <bind port>               The port on which to listen on for web clients (defaults to 8080).");
		System.out
				.println("	--threads=<thread count> || -t <thread count>      The number of concurrent scanning threads which can run (defaults to 5).");
		System.out
				.println("	--mailuser=<username> || -u <username>             A username with which to authenticate for sending e-mail.");
		System.out
				.println("	--mailpass=<password> || -w <password>             A password with which to authenticate for sending e-mail.");
		System.out
				.println("	--help || -h                                       Shows this help text.");
	}

	/**
	 * Use Gnu GetOpt to parse command-line arguments.
	 * @param args
	 *            The command line arguments passed when the application was
	 *            started.
	 */
	private void parseArgs(String[] args) {
		LongOpt[] longopts = new LongOpt[7];
		longopts[0] = new LongOpt("port", LongOpt.REQUIRED_ARGUMENT, null, 'p');
		longopts[1] = new LongOpt("bind", LongOpt.REQUIRED_ARGUMENT, null, 'b');
		longopts[2] = new LongOpt("threads", LongOpt.REQUIRED_ARGUMENT, null, 't');
		longopts[3] = new LongOpt("mailuser", LongOpt.REQUIRED_ARGUMENT, null, 'u');
		longopts[4] = new LongOpt("mailpass", LongOpt.REQUIRED_ARGUMENT, null, 'w');
		longopts[5] = new LongOpt("config", LongOpt.REQUIRED_ARGUMENT, null, 'c');
		longopts[6] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h');
		Getopt g = new Getopt("scanalyzer", args, "b:p:t:", longopts);
		int c = 0;
		while ((c = g.getopt()) != -1) {
			switch (c) {
				case 'b':
					config.put("scanalyzer.bind", g.getOptarg());
					break;
				case 'p':
					config.put("scanalyzer.port", g.getOptarg());
					break;
				case 't':
					config.put("scanalyzer.threads", g.getOptarg());
					break;
				case 'c':
					config.put("scanalyzer.config", g.getOptarg());
					break;
				case 'u':
					config.put("mail.username", g.getOptarg());
					break;
				case 'w':
					config.put("mail.password", g.getOptarg());
					break;
				case 'h':
					printUsage();
					shouldExit = true ;
					break;
				default:
					System.out.println("Invalid argument '" + g.getOptopt() + "'");
					printUsage();
					shouldExit = true ;
					break;
			}
		}
	}

	/**
	 * Load a configuration file if it exists.
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private void loadConfigFile() throws IOException, FileNotFoundException {
		// Load the configuration from a properties file (if available)
		Properties configFile = new Properties();
		File cFile;
		if (config.get("scanalyzer.config") != null) {
			LOG.debug("Attempting to open config file at '"+config.get("scanalyzer.config")+"'");
			cFile = new File(config.get("scanalyzer.config"));
		} else {
			cFile = new File("/etc/scanalyzer/scanalyzer.conf");
		}

		if (cFile.exists() && cFile.canRead()) {
			configFile.load(new FileInputStream(cFile));
			for (Object key : configFile.keySet()) {
				if (config.get(key) == null) {
					config.put((String)key, (String)configFile.get(key));
				}
			}
		}
	}

	/**
	 * Set default values for any configurations settings which were not
	 * previously configured.
	 */
	private void doDefaultSettings() {
		Properties defaults = new Properties();
		try {
			defaults.load(this.getClass().getClassLoader().getResourceAsStream("scanalyzer.properties"));
			for (Object key : defaults.keySet()) {
				// Set default values for properties which were not already set.
				if (config.get(key) == null) {
					config.put((String)key, (String)defaults.get(key));
				}
			}
		} catch (IOException e) {
			System.out.println("ERROR: Unable to load default properties.");
			e.printStackTrace();
		}

	}

	public boolean shouldExit() {
		return shouldExit ;
	}

	public Map<String, String> getConfig() {
		config = new HashMap<>();
		config.put("scanalyzer.port", "8080");
		config.put("scanalyzer.bind", "127.0.0.1");
		config.put("scanalyzer.threads", "5");
		parseArgs(args);

		try {
			this.loadConfigFile();
		} catch (Exception e) {
			LOG.error(e.getLocalizedMessage(), e) ;
		}

		// Set default values for any configurations settings which were not
		// previously configured.
		doDefaultSettings();

		for (String key: config.keySet()) {
			System.out.println(key+"="+config.get(key)) ;
		}

		return config ;
	}
}
