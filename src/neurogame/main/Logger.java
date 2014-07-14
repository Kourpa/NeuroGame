package neurogame.main;

import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import neurogame.library.Library;
public class Logger
{
 
  private static final String LOG_PREFIX = "NGLog_";
  private static final String LOG_EXTENSION = ".txt";
  private static final String PATH = "logs/";
  
  private static final SimpleDateFormat FILE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd.HH-mm-ss.SSS");



  private File logFile;
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
    String fileName = generateFileName();
    
    logFile = new File(PATH, fileName);
    logFile.getParentFile().mkdir();
    
    

    
    try
    {
      writer = new BufferedWriter(new FileWriter(logFile));
      writer.write("Hello Joel");
      
    }
    catch (IOException ex)
    {  ex.printStackTrace();
       System.exit(0);
    }
  }


  private String generateFileName()
  {
    return LOG_PREFIX + FILE_DATE_FORMAT.format(new Date()) + LOG_EXTENSION;
  }
  
  public void closeLog()
  {
    try
    {
      writer.close();
    }
    catch (IOException e) { }
  }

}
