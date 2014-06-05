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

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.Thread.State;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.Queue;

import neurogame.library.Library;

/**
 * A class for handling local event logging for NeuroGame.
 * 
 * @author Ramon A. Lovato
 * @team Danny Gomez
 * @team Marcos Lemus
 */
public class Logger {
	public static final int MAX_QUEUE_SIZE = 10;
	// Prefixed to all log files under the default naming scheme.
	public static final String LOG_PREFIX = "NGLog_";
	public static final String LOG_EXTENSION = ".txt";
	public static final String DEFAULT_PATH = "logs";
	
	private String logName;
	private String logPath;
	private Path path;
	private File log;
	private BufferedWriter writer;
	private Queue<String> entries;
	private LogWorker worker;
	
	/**
	 * Instantiate a new Logger with the default file path.
	 * 
	 * @param initTime String containing the time stamp of initialization
	 *           for NeuroGame, as acquired by Library.timeStamp().
	 */
	public Logger() {
		logPath = DEFAULT_PATH;
		path = null;
		log = null;
		writer = null;
		entries = null;
		worker = null;
	}
	
	/**
	 * Create a new log file with the default naming scheme:
	 *     "NGLog_<time stamp>.txt."
	 * 
	 * Only one log file may be open at a time. Opening a new log file while
	 * one is already open closes the previous one.
	 * 
	 * @return True if log creation successful, else false.
	 */
	public boolean newLog() {
		return newLog(DEFAULT_PATH, LOG_PREFIX
				+ Library.fileTimeStamp() + LOG_EXTENSION);
	}
	
	/**
	 * Create a new log file with the specified path and name. Only one log
	 * file may be open at a time. Opening a new log file while one is already
	 * open closes the previous one.
	 * 
	 * @param logPath String containing the desired path of the log.
	 * @param logName String containing the desired name of the log.
	 * @return True if log creation successful, else false.
	 */
	public boolean newLog(String logPath, String logName) {
		if (isLogOpen()) {
			closeLog();
		}
		this.logPath = logPath;
		this.logName = logName;
		entries = new LinkedList<String>();
		log = new File(logPath, logName);
		path = log.toPath();
		log.getParentFile().mkdir();
		try {
			log.createNewFile();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		try {
			writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
			writeln(time() + ": Stream opened.");
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		} catch (LogClosedException ex) {
			// Does nothing since the stream was explicitly opened first.
		}
		worker = new LogWorker(entries);
		return true;
	}
	
	/**
	 * Append a time stamp to the passed String and add it to the queue. If the
	 * worker thread hasn't been started yet, starts it.
	 * 
	 * @param s String to print to the log file (without time stamp).
	 * @throws LogClosedException if trying to write to closed log file.
	 */
	public synchronized void queue(String s) throws LogClosedException {
		if (isLogOpen()) {
			synchronized (entries) {
				entries.add(time() + ": " + s);
			}
			
			if (worker.getState() == State.NEW) {
				worker.start();
			}
		} else {
			throw new LogClosedException();
		}
	}
	
	/**
	 * Write a passed String to the file without getting the time stamp.
	 * 
	 * @param s String to print to the log file (without time stamp).
	 * @throws LogClosedException if trying to write to closed log file.
	 */
	private synchronized void write(String s) throws LogClosedException {
		if (isLogOpen()) {
			try {
				writer.write(s);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} else {
			throw new LogClosedException();
		}
	}
	
	/**
	 * Write a passed String to the file without getting the time stamp,
	 * followed by a newline.
	 * 
	 * @param s String to print to the log file (without time stamp).
	 * @throws LogClosedException if trying to write to closed log file.
	 */
	private void writeln(String s) throws LogClosedException {
		write(s + Library.NEWLINE);
	}
	
	/**
	 * Pull a single String from the entries queue and write to disk.
	 */
	private void log() {
		String s;
		synchronized (entries) {
			s = entries.poll();
		}
		try {
			if (s != null) {
				writeln(s);
			}
		} catch (LogClosedException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Flush the BufferedWriter to the currently open log file.
	 * 
	 * @throws LogClosedException if trying to flush a closed log file.
	 */
	public void flush() throws LogClosedException {
		if (isLogOpen()) {
			try {
				writer.flush();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} else {
			throw new LogClosedException();
		}
	}
	
	/**
	 * Block and dump all Strings from the entries queue and to the log file.
	 */
	public void dump() throws LogClosedException {
		synchronized (entries) {
			while (!entries.isEmpty()) {
				String s = entries.poll();
				if (s != null) {
					writeln(s);
				}
			}
		}
		try {
			writer.flush();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Check if a log file is currently open.
	 * 
	 * @return True if a log file is currently open, else false.
	 */
	public boolean isLogOpen() {
		return writer != null && path != null && log != null;
	}
	
	/**
	 * Getter for the current log's File.
	 * 
	 * @return The current log's File or null if no current log.
	 * @throws LogClosedException if trying to access a closed log file.
	 */
	public File getLog() throws LogClosedException {
		if (isLogOpen()) {
			return log;
		} else {
			throw new LogClosedException();
		}
	}
	
	/**
	 * Getter for the current log's name.
	 * 
	 * @return The current log's filename.
	 * @throws LogClosedException if trying to access a closed log file.
	 */
	public String getLogName() throws LogClosedException {
		if (isLogOpen()) {
			return logName;
		} else {
			throw new LogClosedException();
		}
	}
	
	/**
	 * Getter for the current log's Path.
	 * 
	 * @return The current log's Path.
	 * @throws LogClosedException if trying to access a closed log file.
	 */
	public Path getPath() throws LogClosedException {
		if (isLogOpen()) {
			return path;
		} else {
			throw new LogClosedException();
		}
	}
	
	/**
	 * Getter for the current log's Path as a String.
	 * 
	 * @return The current log's Path as a String.
	 * @throws LogClosedException if trying to access a closed log file.
	 */
	public String getLogPath() throws LogClosedException {
		if (isLogOpen()) {
			return logPath;
		} else {
			throw new LogClosedException();
		}
	}
	
	/**
	 * A pass-through convenience method for accessing Library.timeStamp().
	 * 
	 * @return The current formatted time stamp String provided by
	 *             Library.timeStamp().
	 */
	public static String time() {
		return Library.timeStamp();
	}
	
	/**
	 * Write a final message and close the writer file stream. If writer is
	 * closed, does nothing.
	 */
	public void closeLog() {
		if (isLogOpen()) {
			// Interrupt the worker thread, which will cause it to dump any
			// remaining entries to the log file all at once, then die.
			worker.interrupt();
			
			try {
				// Write a final goodbye message and flush the writer.
				dump();
				writeln(time() + ": Stream closed.");
				writer.flush();
				writer.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			} catch (LogClosedException lcex) {
				// Does nothing because trying to close an already-closed log.
			} finally {
				writer = null;
				path = null;
				log = null;
			}
		}
	}
	
	/**
	 * A nested, lightweight exception for if trying to write to an unassigned
	 * log file. Provides a mechanism for allowing creation of a new log file
	 * if trying to write to one that doesn't exist.
	 */
	@SuppressWarnings("serial")
  public static class LogClosedException extends Exception {
		public LogClosedException() {
			super("Trying to write to closed log file.");
		}
	}
	
	/**
	 * A nested worker thread that pulls Strings from the entries queue and
	 * writes them to the log file.
	 */
	public class LogWorker extends Thread {
		private Queue<String> entries;
		
		/**
		 * Instantiate a new LogWorker with the passed entries queue.
		 * 
		 * @param entries Queue<String> reference to entries queue.
		 */
		public LogWorker(Queue<String> entries) {
			this.entries = entries;
		}
		
		/**
		 * Override of run.
		 */
		@Override
		public void run() {
			while (!isInterrupted()) {
				// Pull an entry String from the queue and write to log.
				if (!entries.isEmpty()) {
					log();
				} else if (entries.size() > MAX_QUEUE_SIZE) {
					// Queue too bloated - block and dump to file all at once.
					try {
						dump();
					} catch (LogClosedException ex) {
						ex.printStackTrace();
					}
				}
			}
		}
	}
	
}
