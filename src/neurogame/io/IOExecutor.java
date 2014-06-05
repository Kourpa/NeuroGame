/**
 * NeuroGame.
 * CS 351, Project 3
 * 
 * Team members:
 * Ramon A. Lovato
 * Danny Gomez
 * Marcos Lemus
 */

package neurogame.io;

import neurogame.io.Logger.LogClosedException;

/**
 * A class for handling concurrent local event logging for NeuroGame.
 * 
 * @author Ramon A. Lovato
 * @team Danny Gomez
 * @team Marcos Lemus
 */
public class IOExecutor {
	public static final int SAMPLE_FREQUENCY = 500;
	public static final int SAMPLE_DELAY = 1000/SAMPLE_FREQUENCY;
	
	private Logger logger;
	
	private boolean loggingEnabled;
	
	
	/**
	 * Instantiate a new IOExecutor.
	 */
	public IOExecutor() {
		logger = new Logger();
		loggingEnabled = false;
	}
	
	/**
	 * Queue an entry in the Logger.
	 * 
	 * @param s String to queue for logging.
	 */
	public synchronized void logEntry(String s) {
		if (loggingEnabled) {		
			try {
				logger.queue(s);
			} catch (LogClosedException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * Queue an entry in the Logger and append the risk to it.
	 * 
	 * @param s String to queue for logging.
	 * @param risk Double of the risk to append to the String.
	 */
	public synchronized void logEntry(String s, double risk) {
		if (loggingEnabled) {		
			try {
				logger.queue(s + " (" + risk + ")");
			} catch (LogClosedException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * Configure the Logger with default settings.
	 */
	public boolean setupLogger() {
		loggingEnabled = logger.newLog();
		return loggingEnabled;
	}
	
	/**
	 * Configure the Logger with the passed settings.
	 * 
	 * @param logPath String containing the path to the log directory.
	 * @param logName String containing the log filename.
	 */
	public boolean setupLogger(String logPath, String logName) {
		loggingEnabled = logger.newLog(logPath, logName);
		return loggingEnabled;
	}
	
	/**
	 * Kill the current Logger managed by the executor.
	 */
	public void killLogger() {
		if (logger != null && logger.isLogOpen()) {
			logger.closeLog();
		}
	}
	
	/**
	 * Kill all current IO channels managed by the executor. Called when
	 * exiting the program. Does nothing if Logger and Communicator are
	 * already closed.
	 */
	public void killAll() {
		killLogger();
	}
	
}
