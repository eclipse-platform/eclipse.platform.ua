/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */
package org.eclipse.help.internal.standalone;

import java.util.*;

/**
 * This is a standalone help system. It takes care of 
 * launching the eclipse with its help system implementation,
 * and controling it.
 * This class can be used instantiated and used in a Java program,
 * or can be launched from command line to execute single help action.
 * 
 * Usage as a Java component: 
 * <ul>
 * <li> create an instantance of this class and then hold onto 
 * this instance for the duration of your application</li>
 * <li> call start() </li>
 * <li> call displayHelp(...) or displayContext(..) any number of times </li>
 * <li> at the end, call shutdown(). </li>
 * </ul>
 */
public class StandaloneHelp extends StandaloneInfocenter {
	// timout for .hostport file to apper since starting eclipse [ms]
	private static final int STARTUP_TIMEOUT = 30 * 1000;
	// number of retries to connectect to webapp
	private static final int CONNECTION_RETRIES = 2;
	// time between retries to connectect to webapp [ms]
	private static final int CONNECTION_RETRY_INTERVAL = 5 * 1000;
	// ID of the application to run
	private static final String HELP_APPLICATION_ID =
		"org.eclipse.help.helpApplication";
	/**
	 * @see org.eclipse.help.standalone.Help#Help(java.util.List)
	 */
	public StandaloneHelp(List options) {
		super(options, HELP_APPLICATION_ID);
		startupTimeout = STARTUP_TIMEOUT;
		connectionRetries = CONNECTION_RETRIES;
		connectionRetryInterval = CONNECTION_RETRY_INTERVAL;
	}
	/**
	 * @see org.eclipse.help.standalone.Help#displayContext(java.lang.String,int,int)
	 */
	public void displayContext(String contextId, int x, int y) {
	}

	/**
	 * @see org.org.eclipse.help.standalone.Help#displayContextInfopop(java.lang.String,int,int)
	 */
	public void displayContextInfopop(String contextId, int x, int y) {
	}
	/**
	 * @see org.eclipse.help.standalone.Help#displayHelp()
	 */
	public void displayHelp() {
		sendHelpCommand("displayHelp", new String[0]);
	}

	/**
	 * @see org.eclipse.help.standalone.Help#displayHelp(java.lang.String)
	 */
	public void displayHelp(String href) {
		sendHelpCommand("displayHelp", new String[] { "href=" + href });
	}

	/**
	 * @see org.eclipse.help.standalone.Help#main(java.lang.String[])
	 */
	public static void main(String[] args) {
		// convert array of arguments to a list
		List argsList = new ArrayList();
		for (int i = 0; i < args.length; i++) {
			argsList.add(args[i]);
		}
		// consume -command option
		List helpCommands = removeEclipseOption("-command", argsList);
		// Construct help
		StandaloneHelp help = new StandaloneHelp(argsList);
		// Execute help command
		if (help.executeHelpCommand(helpCommands)) {
			return;
		}
		printMainUsage();
	}
	/**
	 * @return true if commands contained a known command
	 *  and it was executed
	 */
	boolean executeHelpCommand(List helpCommands) {
		if (super.executeHelpCommand(helpCommands)) {
			return true;
		}
		if (helpCommands.size() <= 0) {
			return false;
		}
		String command = (String) helpCommands.get(0);
		if ("displayHelp".equalsIgnoreCase(command)) {
			if (helpCommands.size() >= 2) {
				displayHelp((String) helpCommands.get(1));

			} else {
				displayHelp();
			}
			return true;
		} else if ("displayContext".equalsIgnoreCase(command)) {
			if (helpCommands.size() >= 4) {
				displayContext(
					(String) helpCommands.get(1),
					Integer.parseInt((String) helpCommands.get(2)),
					Integer.parseInt((String) helpCommands.get(3)));

				return true;
			}
		} else if ("displayContextInfopop".equalsIgnoreCase(command)) {
			if (helpCommands.size() >= 4) {
				displayContextInfopop(
					(String) helpCommands.get(1),
					Integer.parseInt((String) helpCommands.get(2)),
					Integer.parseInt((String) helpCommands.get(3)));
				return true;
			}
		}

		return false;
	}
	/**
	 * Prints usage of this class as a program.
	 */
	private static void printMainUsage() {
		System.out.println("Parameters syntax:");
		System.out.println();
		System.out.println(
			"-command start | shutdown | (displayHelp [href]) [-eclipsehome eclipseInstallPath] [platform options] [-vmargs [Java VM arguments]]");
		System.out.println();
		System.out.println("where:");
		System.out.println(" href is the URL of the help resource to display,");
		System.out.println(
			" dir specifies Eclipse installation directory; it must be provided, when current directory is not the same as Eclipse installation directory,");
		System.out.println(
			" platform options are other options that are supported by Eclipse Executable.");
	}
}
