/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */
package org.eclipse.help.standalone;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * This is the standalone help system. It takes care of 
 * launching the eclipse with its help system implementation,
 * and controling it.
 * This class can be used instantiated and used in a Java program,
 * or can be launched from command line to execute single help action.
 * 
 * Usage: 
 * <ul>
 * <li> create an instantance of this class and then hold onto 
 * this instance for the duration of your application</li>
 * <li> call start() </li>
 * <li> call displayHelp(...) or displayContext(..) any number of times </li>
 * <li> at the end, call shutdown(). </li>
 * </ul>
 */
public class Help {
	// timout for .hostport file to apper since starting eclipse
	private static final int STARTUP_TIMEOUT = 10 * 1000;
	// number of retries to connectect to webapp
	private static final int CONNECTION_RERIES = 2;
	// time between retries to connectect to webapp
	private static final int CONNECTION_RETRY_INTERVAL = 5 * 1000;

	private static boolean debug = false;
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
	 * Constructcts help system.  Assumes Eclipse installation
	 * directory is the same as current directory.
	 */
	public Help() {
		this(new ArrayList());
	}
	/**
	 * This contstructs the stand alone help.
	 * @param pluginsDir directory containing Eclipse plugins
	 * @deprecated use Help(List) and provide eclipseHome option
	 */
	public Help(String pluginsDir) {
		File plugins = new File(pluginsDir);
		String install = plugins.getParent();
		if (install != null) {
			ArrayList options = new ArrayList(2);
			options.add("-eclipseHome");
			options.add(install);
			processOptions(options);
		}
		processOptions(new ArrayList());
	}
	/**
	 * Constructs help system
	 * @param options array of String options and their values
	 * 	Option <code>-eclipseHome dir</code> specifies Eclipse
	 *  installation directory.
	 *  It must be provided, when current directory is not the same
	 *  as Eclipse installation directory.
	 *  Additionally, most options accepted by Eclipse execuable are supported.
	 */
	public Help(List options) {
		processOptions(options);
	}
	private void processOptions(List options) {
		// consume -eclipsehome opion
		List homes = removeEclipseOption("-eclipseHome", options);
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
		options.add(1, "org.eclipse.help.helpApplication");

		// add -nosplash option (prevent splash)
		removeEclipseOption("-showsplash", options);
		removeEclipseOption("-endsplash", options);
		removeEclipseOption("-nosplash", options);
		options.add(0, "-nosplash");

		eclipseArgs = options;
	}
	/**
	 * Displays context sensitive help.
	 * @param contextId context id
	 * @param x x coordinate
	 * @param y y coordinate
	 */
	public void displayContext(String contextId, int x, int y) {
	}

	/**
	 * Displays context sensitive help in infopop.
	 * @param contextId context id
	 * @param x x coordinate
	 * @param y y coordinate
	 */
	public void displayContextInfopop(String contextId, int x, int y) {
	}
	/**
	 * Displays help.
	 */
	public void displayHelp() {
		sendHelpCommand("displayHelp", new String[0]);
	}

	/**
	 * Displays specified help resource.
	 * @param href the href of the table of contents
	 */
	public void displayHelp(String href) {
		sendHelpCommand("displayHelp", new String[] { "href=" + href });
	}

	/**
	 * Controls standalone help system from command line.
	 * @param args array of String containng options
	 *  Options are:
	 * 	<code>-help start | shutdown | (displayHelp [href]) [-eclipsehome eclipseInstallPath] [platform options] [-vmargs [Java VM arguments]]</code>
	 *  where
	 *  <ul>
	 *  <li><code>href</code> is the URL of the help resource to display,</li>
	 * 	<li><code>dir</code> specifies Eclipse installation directory;
	 * 	  it must be provided, when current directory is not the same
	 *    as Eclipse installation directory,</li>
	 *   <li><code>platform options</code> are other options that are supported by Eclipse Executable.</li>
	 *  <ul>
	 */
	public static void main(String[] args) {
		// convert array of arguments to a list
		List argsList = new ArrayList(args.length + 3);
		for (int i = 0; i < args.length; i++) {
			argsList.add(args[i]);
		}
		// read -debug option
		if (getEclipseOption("-debug", argsList) != null) {
			debug = true;
			System.out.println("Debugging is on");
		}

		// consume -help option
		List helpCommands = removeEclipseOption("-help", argsList);
		// Construct help
		Help help = new Help(argsList);
		// Execute help command
		if (helpCommands.size() >= 1) {
			String command = (String) helpCommands.get(0);
			if ("start".equalsIgnoreCase(command)) {
				help.start();
			} else if ("shutdown".equalsIgnoreCase(command)) {
				help.shutdown();
			} else if ("displayHelp".equalsIgnoreCase(command)) {
				if (helpCommands.size() >= 2) {
					help.displayHelp((String) helpCommands.get(1));
				} else {
					help.displayHelp();
				}
			} else {
				System.out.println("Help command missing.");
			}
		}
		if (debug) {
			System.out.println("Exitting main.");
		}
	}
	/**
	 * Shuts-down the help system . To be called once, at the end. 
	 * Do not call other methods after shutdown is called.
	 */
	public void shutdown() {
		sendHelpCommand("shutdown", new String[0]);
		host = null;
		port = null;
	}

	/**
	 * Starts the help system. To be called once only.
	 */
	public void start() {
		host = null;
		port = null;
		startEclipse();
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
		urlStr.append("/help/control.html?command=");
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
		// wait for .hostport file to appear
		long time1 = System.currentTimeMillis();
		while (!hostPortFile.exists()) {
			if (debug) {
				System.out.println(
					"File " + hostPortFile + " does not exist, at the moment.");
			}
			try {
				Thread.currentThread().sleep(2000);
				// timeout
				if (System.currentTimeMillis() - time1 > STARTUP_TIMEOUT) {
					if (debug) {
						System.out.println(
							"Timeout waiting for file " + hostPortFile);
					}
					return;
				}
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
	 * Removes specified option and its list of values
	 * from a list of options
	 * @param optionName name of the option e.g. -data
	 * @param options List of Eclipse options
	 * @return List of String values of the specified option
	 */
	private static List removeEclipseOption(String optionName, List options) {
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
	 * Ensures help is running, and sends command to the control servlet.
	 * If connection fails, retries several times,
	 * in case webapp is starting up.
	 */
	private void sendHelpCommand(String command, String[] parameters) {
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
		for (int i = 0; i <= CONNECTION_RERIES; i++) {
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
				Thread.currentThread().sleep(CONNECTION_RETRY_INTERVAL);
			} catch (InterruptedException ie) {
				return;
			}
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
		}
		Eclipse eclipse = new Eclipse(eclipseHome, eclipseArgs);
		eclipse.start();
		if (debug) {
			System.out.println("Eclipse launched");
		}
	}

}
