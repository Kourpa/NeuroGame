
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
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.MediaTracker;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import javax.imageio.ImageIO;

import neurogame.gameplay.GameObject;
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

  public static final String GAME_TITLE = "Escape from Asteroid Axion v2014-07-28";

  public static final int MIN_FRAME_MILLISEC = 10;
  public static final String ARGS_REGEX = "\\-[hdDlLfFwsSgG]+";

  public static final String NUM2_FORMAT = "%.2f";
  public static final double WORLD_SCROLL_SPEED = 0.20;

  public static final java.util.Random RANDOM = new java.util.Random();
  
  public static final boolean DEBUG_SHOW_HITBOXES = false;

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

  public static final int HEALTH_MAX = 100;
  public static final int ENEMY_POINTS = 100;

  public static final int HEALTH_PER_COIN = 1;
  public static final int SCORE_COIN = 10;
  public static final int SCORE_AMMOBOX = 40;

  public static final int DAMAGE_PER_WALL_HIT = 10;


  private static boolean debug = false;
  public static double leftEdgeOfWorld = 0;

  private static SpriteMap sprites;
  private static ArrayList<User> users = new ArrayList<User>();
  
  /**
	 * Creates a new user file
	 * 
	 * @param userName
	 */
	public static void addUser(String userName) {
		String path = System.getProperty("user.dir");
		path += "/Users/";
		
		try {
			User newUser = new User(userName);

			FileOutputStream saveFile = new FileOutputStream(path+userName + ".user");
			ObjectOutputStream save = new ObjectOutputStream(saveFile);
			save.writeObject(newUser);
			save.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("FileNotFound. Failed to save User: " + userName);
		} catch (IOException e) {
			System.out.println("IO. Failed to save User: " + userName);
		}
	}
	
	public static void saveUser(User newUser){
		String path = System.getProperty("user.dir");
		path += "/Users/";
		
		try {
			FileOutputStream saveFile = new FileOutputStream(path+newUser.getName() + ".user");
			ObjectOutputStream save = new ObjectOutputStream(saveFile);
			save.writeObject(newUser);
			save.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("FileNotFound. Failed to save User: " + newUser.getName());
		} catch (IOException e) {
			System.out.println("IO. Failed to save User: " + newUser.getName());
		}
	}

	/**
	 * Load all user files in the directory
	 */
	public static void loadUsers() {
		String path = System.getProperty("user.dir");
		path += "/Users/";
		File dir = new File(path);

    if(!dir.exists()){
      dir.mkdir();
      addUser("Guest");
    }

    boolean guestexists = false;
    for (File file : dir.listFiles()) {
			if (file.getName().endsWith((".user"))) {
				parseUser(file);
			}
      if(file.getName().startsWith("Guest")){
        guestexists = true;
      }
		}

    if(!guestexists){
      addUser("Guest");
    }
	}

	/**
	 * Parse a user file into a user object
	 */
	private static void parseUser(File file) {
		User newUser;
		try {
			FileInputStream saveFile = new FileInputStream(file);
			ObjectInputStream save = new ObjectInputStream(saveFile);
			newUser = (User) save.readObject();
			users.add(newUser);
			save.close();
		} catch (Exception e) {
			e.getStackTrace();
			System.out.println("Problem Parsing User: "+file.getName());
		}
	}

	/**
	 * @return a list of the names of the users
	 */
	public static String[] getUserNames() {
		System.out.println("-- Get Names: "+users.size());
		users.clear();
		loadUsers();
		if (users.size() > 0) {
			String[] names = new String[users.size()];
			for (int i = 0; i < users.size(); i++) {
				names[i] = users.get(i).getName();
			}
			return names;
		}
		
		String[] nullString = {"No Users"};
		return nullString;
	}
	
	public static User getUser(int i){
		try{
			return users.get(i);
		}
		catch(Exception e){
			return null;
		}
	}
	
	public static User getUser(String name){
		for (int i = 0; i < users.size(); i++) {
			if(name.compareTo(users.get(i).getName()) == 0){
				return users.get(i); 
			}
		}
		return null;
	}

	/**
	 * Load the local fonts
	 */
	public static void loadFont(){
		String path = System.getProperty("user.dir");
		path += "/resources/fonts/";

		try {
			GraphicsEnvironment ge = GraphicsEnvironment
					.getLocalGraphicsEnvironment();
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File(path
					+ "KarmaticArcade.ttf")));
			System.out.println("Registered Font");
		} catch (IOException | FontFormatException e) {
			System.out.println("Error Loading Font - MenuButtons.java");
		}
	}
	
	/**
	 * Get the best highscores from any user
	 */
	public static Long[] getBestHighScores(int amount){
		ArrayList<Long> scores = new ArrayList<Long>();
		Long[] userScores;
		
		if (users.size() > 0) {
			for (int i = 0; i < users.size(); i++) {
				userScores = users.get(i).getHighScores(amount);
				
				for (int k = 0; k < userScores.length; k++) {
					scores.add(userScores[k]);
				}
			}
		}
		
		Collections.sort(scores);
		Collections.reverse(scores);
		return scores.subList(0, amount).toArray(new Long[0]);
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
    { //System.out.println("imagePath="+imagePath);
      //fileURL = new URL(new URL("file:"), imagePath);
      
      imagePath = "resources" + imagePath;
      fileURL = new URL("file:" + imagePath);
      
      
      //fileURL = widgit.getClass().getResource(imagePath);
      
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
