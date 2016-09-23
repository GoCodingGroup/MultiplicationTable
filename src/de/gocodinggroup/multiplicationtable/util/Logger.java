package de.gocodinggroup.multiplicationtable.util;

import java.time.*;

/**
 * Logger class. Use this for logging stuff instead of System.out.println() ->
 * Logs can be turned on/of just by changing the global logger to a subclass
 * with different behavoir
 * 
 * @author Dominik
 * @created 23.09.2016
 *
 */
public class Logger {
	public static int LOG_DEBUG = 0, LOG_CODEREVIEW = 1, LOG_RELEASE = 2;
	protected int logLevel;

	public Logger(int logLevel) {
		this.logLevel = logLevel;
	}

	public void setLogLevel(int logLevel) {
		this.logLevel = logLevel;
	}

	/**
	 * Outputs the message as an info log. This only occurs when the loglevel is
	 * debug
	 * 
	 * @param message
	 */
	public void info(String message) {
		if (this.logLevel == 0) System.out.println("INFO(" + LocalDateTime.now() + "): " + message);
	}

	/**
	 * Outputs the message as a warning log. This only occurs when the loglevel
	 * is debug or codereview
	 * 
	 * @param message
	 */
	public void warn(String message) {
		if (this.logLevel < 2) System.err.println("WARN(" + LocalDateTime.now() + "): " + message);
	}

	/**
	 * Outputs an error message and terminates the application. This occurs
	 * independant of log level and should be used for serious errors only
	 * 
	 * @param message
	 */
	public void error(String message) {
		System.err.println("ERROR(" + LocalDateTime.now() + "): " + message);
		System.exit(0);
	}
}
