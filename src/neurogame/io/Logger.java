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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
public class Logger
{
  public static final int MAX_QUEUE_SIZE = 10;
  // Prefixed to all log files under the default naming scheme.
  public static final String LOG_PREFIX = "NGLog_";
  public static final String LOG_EXTENSION = ".txt";
  public static final String DEFAULT_PATH = "logs";
  public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
  public static final String FILE_DATE_FORMAT = "yyyy-MM-dd HH-mm-ss.SSS";
  
  private static DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
  private static DateFormat fileDateFormat = new SimpleDateFormat(FILE_DATE_FORMAT);

  private String logName;
  private String logPath;
  private Path path;
  private File log;
  private BufferedWriter writer;

  /**
   * Instantiate a new Logger with the default file path.
   * 
   * @param initTime
   *          String containing the time stamp of initialization for NeuroGame,
   *          as acquired by Library.timeStamp().
   */
  public Logger()
  {
    logPath = DEFAULT_PATH;
    path = null;
    log = null;
    writer = null;
  
    logPath = DEFAULT_PATH;
    logName = LOG_PREFIX + fileTimeStamp() + LOG_EXTENSION;

    if (isLogOpen())
    {
      closeLog();
    }
    
    log = new File(logPath, logName);
    path = log.toPath();
    log.getParentFile().mkdir();
    System.out.println("Logger("+ logPath + ", "+ logName); 
    try
    {
      log.createNewFile();
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
    }
    try
    {
      writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
      writer.write(time() + ": Stream opened." + Library.NEWLINE);
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
      System.exit(0);
    }
  }

  public static String fileTimeStamp()
  {
    return fileDateFormat.format(Calendar.getInstance().getTime());
  }
  public static String time()
  {
    return dateFormat.format(Calendar.getInstance().getTime());
  }


  /**
   * Write a passed String to the file without getting the time stamp, followed
   * by a newline.
   * 
   * @param s
   *          String to print to the log file (without time stamp).
   * @throws LogClosedException
   *           if trying to write to closed log file.
   */
  public void writeLogRecord()
  {
    
    
    /**
     * Convenience getter for a String containing the current system date and
     * time, formatted as "yyyy-MM-dd HH-mm-ss.SSSS for file output"
     */

    try
    {
      writer.write(time() + Library.NEWLINE);
    }
    catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }






  /**
   * Check if a log file is currently open.
   * 
   * @return True if a log file is currently open, else false.
   */
  public boolean isLogOpen()
  {
    return writer != null && path != null && log != null;
  }




  /**
   * Write a final message and close the writer file stream. If writer is
   * closed, does nothing.
   */
  public void closeLog()
  {
    if (isLogOpen())
    {
      try
      {
        // Write a final goodbye message and flush the writer.
        writer.write(time() + ": Stream closed.");
        writer.flush();
        writer.close();
      }
      catch (IOException ex)
      {
        ex.printStackTrace();
      }
    }
  }
}
