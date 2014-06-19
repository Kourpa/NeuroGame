/**
 * NeuroGame.
 * CS 351, Project 3
 * 
 * Team members:
 * Ramon A. Lovato
 * Danny Gomez
 * Marcos Lemus
 */

package neurogame.library;

import java.awt.Container;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.imageio.ImageIO;

import neurogame.gameplay.GameObject;
import neurogame.io.IOExecutor;
import neurogame.main.NeuroFrame;

/**
 * A static library for NeuroGame.
 * 
 * @author Ramon A. Lovato
 * @team Danny Gomez
 * @team Marcos Lemus
 */
public final class Library
{
  public static final String NEWLINE = System.getProperty("line.separator");
  public static final String SEPARATOR = System.getProperty("file.separator");

  public static final String GAME_TITLE = "NeuroSideScroller Version 2014-06-12";

  public static final int MIN_FRAME_MILLISEC = 20;
  public static final String ARGS_REGEX = "\\-[hdDlLfFwsSgG]+";
  public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
  public static final String FILE_DATE_FORMAT = "yyyy-MM-dd HH-mm-ss.SSS";
  public static final String NUM2_FORMAT = "%.2f";

  public static final java.util.Random RANDOM = new java.util.Random();

  private static int windowPixelWidth, windowPixelHeight;

  // The initial u-value is set to the screen height. When the frame is
  // resized, the setter is called and the uValue updated to the new height.
  public static int U_VALUE;
  // Since 1u is defined as the height of the frame, the vertical movement
  // bounds for the screen are always between 0 and 1, with 0 being the top
  // and 1 being the bottom.
  public static final double VERTICAL_MIN = 0.0;
  public static final double VERTICAL_MAX = 1.0;
  public static final double HORIZONTAL_MIN = 0.0;

  public static final double SCORE_PER_SEC = 0.01;
  public static final int HEALTH_MAX = 100;
  public static final int COIN_POINTS = 100;
  public static final int POWERUP_POINTS = 200;
  public static final int ENEMY_POINTS = 150;

  public static final int HEALTH_PER_COIN = 1;

  public static final int DAMAGE_PER_WALL_HIT = 5;
  public static final int DAMAGE_PER_SEC_IN_ZAPPER = 1;

  private static DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
  private static DateFormat fileDateFormat = new SimpleDateFormat(
      FILE_DATE_FORMAT);

  private static boolean debug = false;
  public static double leftEdgeOfWorld = 0;

  private static IOExecutor executor;
  private static SpriteMap sprites;

  /**
   * Convenience getter for a String containing the current system date and
   * time, formatted as "yyyy/MM/dd HH:mm:ss.SSSS."
   */
  public static String timeStamp()
  {
    return dateFormat.format(Calendar.getInstance().getTime());
  }

  public static int getWindowPixelWidth()
  {
    return windowPixelWidth;
  }

  public static int getWindowPixelHeight()
  {
    return windowPixelHeight;
  }

  public static double getWindowAspect()
  {
    return (double) windowPixelWidth / (double) windowPixelHeight;
  }

  public static void setWindowPixelWidth(int width)
  {
    windowPixelWidth = width;
  }

  public static void setWindowPixelHeight(int height)
  {
    windowPixelHeight = height;
  }

  /**
   * Convenience getter for a String containing the current system date and
   * time, formatted as "yyyy-MM-dd HH-mm-ss.SSSS for file output"
   */
  public static String fileTimeStamp()
  {
    return fileDateFormat.format(Calendar.getInstance().getTime());
  }

  /**
   * Set the random number generator's seed.
   * 
   * @param seed
   *          Long to use as the RNG seed.
   */
  public static void seed(long seed)
  {
    RANDOM.setSeed(seed);
  }

  /**
   * Queue the passed String for logging if the IOExecutor has been set.
   * 
   * Note: this method is provided for convenience and makes no guarantees about
   * the log being open when called. If the log is closed, a stack trace will be
   * printed for the LogClosedException. If it's possible that the log might be
   * closed when calling log, the IOExecutor should first be acquired using
   * getExecutor and its methods used instead.
   * 
   * @param s
   *          String to queue for logging.
   */
  public static void log(String s)
  {
    if (executor != null)
    {
      executor.logEntry(s);
    }
  }

  /**
   * Queue the passed String for logging if the IOExecutor has been set and
   * appends the risk.
   * 
   * Note: this method is provided for convenience and makes no guarantees about
   * the log being open when called. If the log is closed, a stack trace will be
   * printed for the LogClosedException. If it's possible that the log might be
   * closed when calling log, the IOExecutor should first be acquired using
   * getExecutor and its methods used instead.
   * 
   * @param s
   *          String to queue for logging.
   * @param risk
   *          Double for the risk to append to the entry.
   */
  public static void log(String s, double risk)
  {
    if (executor != null)
    {
      executor.logEntry(s, risk);
    }
  }

  /**
   * Getter for debug.
   * 
   * @return True if debugging is enabled, false if not.
   */
  public static boolean debugging()
  {
    return debug;
  }

  /**
   * Setter for debug.
   * 
   * @param flag
   *          Boolean enabling debugging if true or disabling if false.
   */
  public static void setDebug(boolean flag)
  {
    debug = flag;
  }

  /**
   * Converts pixel scale (int) coordinates to u-scale world coordinates by
   * dividing the passed value by the current u-value.
   * 
   * @param w
   *          Screen (int) coordinate to convert to u-scale world coordinate.
   * @return Result of converting the parameter to u-scale.
   */
  // public static double screenToWorld(int w)
  // {
  // return (double) w / U_VALUE;
  // }

  public static int worldUnitToScreen(double u)
  {
    return (int) (u * U_VALUE);
  }

  public static int worldPosXToScreen(double x)
  {
    return (int) ((x - leftEdgeOfWorld) * U_VALUE);
  }

  public static int worldPosYToScreen(double y)
  {
    return (int) (y * U_VALUE);
  }

  /**
   * Determine if a Point2D in the world would be on screen by checking it
   * against the current deltaX and deltaY values.
   * 
   * @param p
   *          Point2D to check if on screen.
   * @return True if p is on screen, else false.
   */
  public static boolean isOnScreen(double x, double y)
  {
    if (y < 0.0) return false;
    if (y > 1.0) return false;
    if (x < leftEdgeOfWorld) return false;

    if (worldPosXToScreen(x) >= getWindowPixelWidth()) return false;
    return true;
  }

  /**
   * Determine if a GameObject in the world would be on screen by checking it
   * against the current deltaX and deltaY values.
   * 
   * @param o
   *          GameObject to check if on screen.
   * @return True if p is on screen, else false.
   */
  public static boolean isOnScreen(GameObject o)
  {
    double x = o.getX();
    double y = o.getY();
    double width = o.getWidth();
    double height = o.getHeight();

    if (isOnScreen(x, y)) return true;
    if (isOnScreen(x + width, y)) return true;
    if (isOnScreen(x, y + height)) return true;
    if (isOnScreen(x + width, y + height)) return true;
    return false;
  }

  // /**
  // * Get the drawing position of a Point2D.
  // *
  // * @param p
  // * Point2D.Double in world coordinates for which to determine the
  // * screen position.
  // * @return Point containing p's location on screen or null if the location
  // is
  // * not on screen.
  // */
  // public static Point getScreenPosition(Point2D.Double p)
  // {
  // if (!isOnScreen(p))
  // {
  // return null;
  // }
  // else
  // {
  // return new Point(worldToScreen(p.x - deltaX),
  // worldToScreen(worldToScreen(p.y - deltaY)));
  // }
  // }

  /**
   * Getter for executor.
   * 
   * @return IOExecutor responsible for managing logging and communications for
   *         the current game.
   */
  public static IOExecutor getExecutor()
  {
    return executor;
  }

  /**
   * Setter for executor.
   * 
   * @param executorIn
   *          IOExecutor for which to store a reference.
   */
  public static void setExecutor(IOExecutor executorIn)
  {
    executor = executorIn;
  }

  /**
   * Getter for sprites.
   * 
   * @return SpriteMap containing all of the loaded sprites for this game.
   */
  public static SpriteMap getSprites()
  {
    return sprites;
  }

  /**
   * Setter for sprites.
   * 
   * @param SpriteMap
   *          for which to store a reference.
   */
  public static void initSprites(NeuroFrame frame)
  { // System.out.println("Library.initSprites(): Enter");
    sprites = new SpriteMap(frame);
  }

  /**
   * Loads a image file with the given path into a new bufferedImage. Blocks
   * until the image has finished loading. widit is the component on which the
   * images will eventually be drawn.
   * 
   * @return A buffered image containing the loaded image.
   */
  public static BufferedImage loadImage(String imagePath, Container widgit)
  {
    if (imagePath == null) return null;
    if (widgit == null)
    {
      widgit = new Container();
    }

    // Create a MediaTracker instance, to montior loading of images
    MediaTracker tracker = new MediaTracker(widgit);

    // Load the image
    // Toolkit tk = Toolkit.getDefaultToolkit();
    // BufferedImage loadedImage = //tk.getImage(imagePath);
    BufferedImage loadedImage = null;
    URL fileURL = null;
    try
    {
      fileURL = widgit.getClass().getResource(imagePath);
      loadedImage = ImageIO.read(fileURL);

      // Register it with media tracker
      tracker.addImage(loadedImage, 1);
      tracker.waitForAll();

      // System.out.println("Library.loadImage("+imagePath+")  size=(" +
      // loadedImage.getWidth() +", " + loadedImage.getHeight() + ")");
    }
    catch (Exception e)
    {
      System.out.println("Cannot Open image: " + imagePath);
      e.printStackTrace();
      System.exit(0);
    }

    return loadedImage;
  }

  public static String bounds2DString(Rectangle2D bounds)
  {

    return String.format("[x=" + NUM2_FORMAT + ", y=" + NUM2_FORMAT + ", w="
        + NUM2_FORMAT + ", h=" + NUM2_FORMAT + "]", bounds.getX(),
        bounds.getY(), bounds.getWidth(), bounds.getHeight());
  }

}
