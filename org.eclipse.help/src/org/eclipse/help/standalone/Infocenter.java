/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */
package org.eclipse.help.standalone;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * This program is used to start or stop Eclipse
 * Infocenter application.
 * It should be launched from command line.
 */
public class Infocenter {
	// ID of the application to run
	private static final String INFOCENTER_APPLICATION_ID =
		"org.eclipse.help.infocenterApplication";
	// application to launch
	String applicationID = INFOCENTER_APPLICATION_ID;
	// debugging
	boolean debug = false;
	// timout for .hostport file to apper since starting eclipse [ms]
	// 0 if no waiting for file should occur
	int startupTimeout = 0;
	// number of retries to connectect to webapp
	int connectionRetries = 0;
	// time between retries to connectect to webapp [ms]
	int connectionRetryInterval = 5 * 1000;
	// control servlet path
	private static final String CONTROL_SERVLET_PATH = "/helpControl/control.html";
	// arguments to pass to Eclipse
	private List eclipseArgs;
	// Eclipse installation directory
	private File eclipseHome;
	// workspace directory to be used by Eclipse
	private File workspace;
	// Eclipse .lock file
	private File lockFile;
	// .hostport file to obtain help server host and port from Eclipse help application
	private File hostPortFile;
	// help server host
	private String host = null;
	// help server port
	private String port = null;
	/**
	 * Constructs help system
	 * @param options array of String options and their values
	 * 	Option <code>-eclipseHome dir</code> specifies Eclipse
	 *  installation directory.
	 *  It must be provided, when current directory is not the same
	 *  as Eclipse installation directory.
	 *  Additionally, most options accepted by Eclipse execuable are supported.
	 * @param applicationID ID of Eclipse help application
	 */
	Infocenter(List options, String applicationID) {
		this.applicationID = applicationID;
		processOptions(options);
	}
	/**
	 * Controls start up and shut down of infocenter from command line.
	 * @param args array of String containng options
	 *  Options are:
	 * 	<code>-command start | shutdown [-eclipsehome eclipseInstallPath] [platform options] [-vmargs [Java VM arguments]]</code>
	 *  where
	 *  <ul>
	 * 	<li><code>dir</code> specifies Eclipse installation directory;
	 * 	  it must be provided, when current directory is not the same
	 *    as Eclipse installation directory,</li>
	 *   <li><code>platform options</code> are other options that are supported by Eclipse Executable.</li>
	 *  <ul>
	 */
	public static void main(String[] args) {
		// convert array of arguments to a list
		List argsList = new ArrayList();
		for (int i = 0; i < args.length; i++) {
			argsList.add(args[i]);
		}
		// consume -command option
		List helpCommands = removeEclipseOption("-command", argsList);
		// Construct infocenter
		Infocenter infocenter =
			new Infocenter(argsList, INFOCENTER_APPLICATION_ID);
		// Execute help command
		if (infocenter.executeHelpCommand(helpCommands)) {
			return;
		}
		printMainUsage();
	}
	/**
	 * Shuts-down the infocenter application.
	 */
	public void shutdown() {
		sendHelpCommand("shutdown", new String[0]);
		host = null;
		port = null;
	}

	/**
	 * Starts the infocenter application.
	 */
	public void start() {
		host = null;
		port = null;
		startEclipse();
	}
	/**
	 * @return true if commands contained a known command
	 *  and it was executed
	 */
	boolean executeHelpCommand(List helpCommands) {
		if (helpCommands.size() <= 0) {
			return false;
		}
		String command = (String) helpCommands.get(0);
		if ("start".equalsIgnoreCase(command)) {
			start();
			return true;
		} else if ("shutdown".equalsIgnoreCase(command)) {
			shutdown();
			return true;
		}
		return false;
	}
	/**
	 * Removes specified option and its list of values
	 * from a list of options
	 * @param optionName name of the option e.g. -data
	 * @param options List of Eclipse options
	 * @return List of String values of the specified option
	 */
	static List removeEclipseOption(String optionName, List options) {
		List values = new ArrayList(1);
		for (int i = 0; i < options.size();) {
			if (optionName.equalsIgnoreCase((String) options.get(i))) {
				// found the option, remove option
				options.remove(i);
				while (i < options.size()) {
					if (((String) options.get(i)).startsWith("-")) {
						// start of next option
						break;
					}
					// note, and remove option value
					values.add(options.get(i));
					options.remove(i);
				}
			} else {
				i++;
			}
		}
		return values;
	}
	/**
	 * Ensures the application is running, and sends command
	 * to the control servlet.
	 * If connection fails, retries several times,
	 * in case webapp is starting up.
	 */
	void sendHelpCommand(String command, String[] parameters) {
		if (!"shutdown".equalsIgnoreCase(command)) {
			startEclipse();
		}
		if (host == null || port == null) {
			obtainHostPort();
		}
		if (host == null || port == null) {
			return;
		}
		URL url = createCommandURL(command, parameters);
		if (url == null) {
			return;
		}
		long time1 = System.currentTimeMillis();
		for (int i = 0; i <= connectionRetries; i++) {
			try {
				HttpURLConnection connection =
					(HttpURLConnection) url.openConnection();
				if (debug) {
					System.out.println(
						"Connection  to control servlet created.");
				}
				connection.connect();
				if (debug) {
					System.out.println(
						"Connection  to control servlet connected.");
				}
				int code = connection.getResponseCode();
				if (debug) {
					System.out.println(
						"Response code from control servlet=" + code);
				}
				connection.disconnect();
				return;
			} catch (IOException ioe) {
				if (debug) {
					ioe.printStackTrace();
				}
			}
			try {
				Thread.currentThread().sleep(connectionRetryInterval);
			} catch (InterruptedException ie) {
				return;
			}
		}
	}
	private void processOptions(List options) {
		// read -debug option
		if (getEclipseOption("-debug", options) != null) {
			debug = true;
			System.out.println("Debugging is on.");
		}
		// consume -eclipsehome (accept eclipse_home too) opion
		List homes = removeEclipseOption("-eclipseHome", options);
		if (homes.isEmpty()) {
			homes = removeEclipseOption("-eclipse_Home", options);
		}
		if (!homes.isEmpty()) {
			eclipseHome = new File((String) homes.get(0));
		} else {
			eclipseHome = new File(System.getProperty("user.dir"));
		}

		// read -data option
		List workspaces = getEclipseOption("-data", options);
		if (workspaces != null && !workspaces.isEmpty()) {
			workspace = new File((String) workspaces.get(0));
		} else {
			workspace = new File(eclipseHome, "workspace");
		}
		lockFile = new File(workspace, "/.metadata/.lock");
		hostPortFile = new File(workspace, "/.metadata/.hostport");

		// add -vm option if not present
		List vms = getEclipseOption("-vm", options);
		if (vms == null || vms.isEmpty()) {
			String vm = System.getProperty("java.vm.name");
			String executable = "J9".equals(vm) ? "j9" : "java";
			if (System.getProperty("os.name").startsWith("Win")) {
				executable += "w.exe";
			}
			String javaExe =
				System.getProperty("java.home")
					+ File.separator
					+ "bin"
					+ File.separator
					+ executable;
			options.add(0, "-vm");
			options.add(1, javaExe);

		}

		// add -application option
		removeEclipseOption("-application", options);
		options.add(0, "-application");
		options.add(1, applicationID);

		// add -nosplash option (prevent splash)
		removeEclipseOption("-showsplash", options);
		removeEclipseOption("-endsplash", options);
		removeEclipseOption("-nosplash", options);
		options.add(0, "-nosplash");

		eclipseArgs = options;
	}
	/**
	 * Prints usage of this class as a program.
	 */
	private static void printMainUsage() {
		System.out.println("Parameters syntax:");
		System.out.println();
		System.out.println(
			"-command start | shutdown [-eclipsehome eclipseInstallPath] [platform options] [-vmargs [Java VM arguments]]");
		System.out.println();
		System.out.println("where:");
		System.out.println(
			" dir specifies Eclipse installation directory; it must be provided, when current directory is not the same as Eclipse installation directory,");
		System.out.println(
			" platform options are other options that are supported by Eclipse Executable.");
	}

	/**
	 * Builds a URL that communicates the specified command
	 * to help control servlet.
	 * @param command standalone help system command e.g. "displayHelp"
	 * @param parameters array of parameters of the command e.g. {"http://www.eclipse.org"}
	 */
	private URL createCommandURL(String command, String[] parameters) {
		StringBuffer urlStr = new StringBuffer();
		urlStr.append("http://");
		urlStr.append(host);
		urlStr.append(":");
		urlStr.append(port);
		urlStr.append(CONTROL_SERVLET_PATH);
		urlStr.append("?command=");
		urlStr.append(command);
		for (int i = 0; i < parameters.length; i++) {
			urlStr.append("&");
			urlStr.append(parameters[i]);
		}
		if (debug) {
			System.out.println("Control servlet URL=" + urlStr.toString());
		}
		try {
			return new URL(urlStr.toString());
		} catch (MalformedURLException mue) {
			mue.printStackTrace();
			return null;
		}

	}
	/**
	 * Obtains specified option and its list of values
	 * from a list of options
	 * @param optionName name of the option e.g. -data
	 * @param options List of Eclipse options
	 * @return List of String values of the specified option,
	 *  or null if option is not present
	 */
	private static List getEclipseOption(String optionName, List options) {
		List values = null;
		for (int i = 0; i < options.size(); i++) {
			if (optionName.equalsIgnoreCase((String) options.get(i))) {
				if (values == null) {
					values = new ArrayList(1);
				}
				for (int j = i + 1; j < options.size(); j++) {
					if (((String) options.get(j)).startsWith("-")) {
						// start of next option
						i = j;
						break;
					}
					values.add(options.get(j));
				}
			}
		}
		return values;
	}
	/**
	 * Obtains host and port from the file.
	 * Retries several times if file does not exists,
	 * and help might be starting up.
	 */
	private void obtainHostPort() {
		long time1 = System.currentTimeMillis();
		while (!hostPortFile.exists()) {
			// wait for .hostport file to appear
			if (debug) {
				System.out.println(
					"File " + hostPortFile + " does not exist, at the moment.");
			}
			// timeout
			if (System.currentTimeMillis() - time1 >= startupTimeout) {
				if (debug) {
					System.out.println(
						"Timeout waiting for file " + hostPortFile);
				}
				return;
			}
			// wait more
			try {
				Thread.currentThread().sleep(2000);
			} catch (InterruptedException ie) {
				return;
			}
		}
		Properties p = new Properties();
		FileInputStream is = null;
		try {
			is = new FileInputStream(hostPortFile);
			p.load(is);
			is.close();
		} catch (IOException ioe) {
			// it ok, eclipse might have just exited
			ioe.printStackTrace();
			return;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ioe2) {
				}
			}
		}
		host = (String) p.get("host");
		port = (String) p.get("port");
		if (debug) {
			System.out.println("Help server host=" + host);
		}
		if (debug) {
			System.out.println("Help server port=" + port);
		}
	}
	/**
	 * Starts Eclipse if not yet running.
	 */
	private void startEclipse() {
		if (debug) {
			System.out.println(
				"Using workspace " + workspace.getAbsolutePath());
			System.out.println("Checking if file " + lockFile + " exists.");
		}
		if (lockFile.exists()) {
			if (!lockFile.delete()) {
				// already running
				if (debug) {
					System.out.println(
						"File " + lockFile + " exists and is locked.");
				}
				return;
			} else {
				if (debug) {
					System.out.println("Deleted file " + lockFile);
				}
				// left over files
				hostPortFile.delete();
				if (debug) {
					System.out.println("Deleted file " + hostPortFile);
				}
			}
		}
		if (debug) {
			System.out.println("Launching Eclipse.");
			for (Iterator it = eclipseArgs.iterator(); it.hasNext();) {
				System.out.println("  " + (String) it.next());
			}
		}
		Eclipse eclipse = new Eclipse(eclipseHome, eclipseArgs);
		eclipse.start();
		if (debug) {
			System.out.println("Eclipse launched");
		}
	}

}
