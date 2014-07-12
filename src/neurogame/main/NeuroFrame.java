/**
 * NeuroGame.
 * CS 351, Project 3
 * 
 * Team members:
 * Ramon A. Lovato
 * Danny Gomez
 * Marcos Lemus
 */

package neurogame.main;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JFrame;

import neurogame.gameplay.GameObject;
import neurogame.gameplay.PowerUp;
import neurogame.level.World;
import neurogame.library.Library;
import neurogame.library.SpriteMap;
import neurogame.library.User;
import neurogame.main.GameController.GameState;

/**
 * The main game frame for NeuroGame.
 * 
 * @author Marcos Lemus
 * @author Ramon A. Lovato
 * @team Danny Gomez
 */
@SuppressWarnings("serial")
public class NeuroFrame extends JFrame
{
  private static final int MIN_WINDOW_WIDTH = 200;
  private static final int MIN_WINDOW_HEIGHT = 200;

  // private BufferStrategy strategy;
  // private DrawingEngine engine;

  private MainDrawPanel drawPanel;
  private TitleScreen title;
  private GameOverScreen gameOver;
  private World world;
  private int windowPixelWidth, windowPixelHeight;
  private GameState mode = GameState.INITIALIZING;
  private int score;
  private int coins;
  private int health;
  private PowerUp powerUp;

  private SpriteMap sprites;

  private Container contentPane;

  private User CurrentUser;
  
  private boolean debug;

  /**
   * Instantiates a new NeuroFrame.
   */
  public NeuroFrame(final NeuroGame game)
  {
    System.out.println("NeuroFrame(): Enter");


    this.setResizable(false);
    this.setTitle(Library.GAME_TITLE);

    Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    int screenWidth = screenSize.width;
    int screenHeight = screenSize.height;

    this.setBounds(0, 0, screenWidth, screenHeight);
    this.setVisible(true);
    contentPane = getContentPane();
    contentPane.setLayout(null);

    drawPanel = new MainDrawPanel(game, this);
    contentPane.add(drawPanel);
    drawPanel.setVisible(false);
    //contentPane.setBackground(Color.black);

    score = 0;
    coins = 0;
    health = 0;

    debug = false;

    resizeHelper();

    sprites = Library.getSprites();



    drawPanel.setSprites(sprites);

    this.addComponentListener(new ComponentAdapter()
    {
      public void componentResized(ComponentEvent e)
      {
        resizeEvent();
      }
    });

    // Listener to check for the frame closing and cleanly exit.
    addWindowListener(new WindowAdapter()
    {

      public void windowClosing(WindowEvent e)
      {
        game.quit();
      }
    });

  }

  /**
   * Show the game using the provided DrawingEngine.
   * 
   * @param world
   *          World for the current game.
   * @return DrawingEngine being used to draw the world.
   */
  public void startGame(World world)
  {
    this.world = world;
    // engine = new DrawingEngine(this, world);
    //setContentPane(drawPanel);
    contentPane.add(drawPanel);
    drawPanel.setVisible(true);
    drawPanel.setWorld(world);
    mode = GameState.PLAYING;
    // title = null;
  }

  public void setGameMode(GameState mode)
  {
    this.mode = mode;
  }

  public GameState getGameMode()
  {
    return mode;
  }
  
  public int getHealth()
  {
    return health;
  }

  public int getCoins()
  {
    return coins;
  }

  public int getScore()
  {
    return score;
  }

  public PowerUp getPowerUp()
  {
    return powerUp;
  }
  
  /**
 * Sets the user for the session
 */
public void setUser(User newUser){
	  this.CurrentUser = newUser;
  }
public User getUser(){
	return this.CurrentUser;
}
  /**
   * Changes the display for when the game is paused.
   */
  public void pause()
  {
    mode = GameState.PAUSED;
  }

  /**
   * Changes the display back from being paused.
   */
  public void unpause()
  {
    mode = GameState.PLAYING;
  }

  /**
   * Create and show the title screen.
   * 
   * @return Newly created title screen.
   */
  public TitleScreen showTitle()
  {
    System.out.println("NeuroFrame.showTitle() Enter");
    drawPanel.setVisible(false);
    title = new TitleScreen(this);
    drawPanel.setTitle(title);
    mode = GameState.TITLE;
    return title;
  }
  
  public GameOverScreen showGameOver(){
	  System.out.println("NeuroFrame.showTitle() Enter");
	  
	  drawPanel.setVisible(false);
	  gameOver = new GameOverScreen(this,this.CurrentUser);
	  drawPanel.setGameOver(gameOver);
	  
	  mode = GameState.GAMEOVER;
	  return gameOver;
  }

  private void resizeEvent()
  {
    if (mode == GameState.INITIALIZING) return;
    resizeHelper();
  }

  /**
   * A helper method that gets called when the window is resized, which releases
   * the old buffered image and generates a new one with the new window
   * dimensions.
   */
  private void resizeHelper()
  {

    boolean sizeIsOkay = true;
    int outsideFrameWidth = this.getWidth();
    int outsideFrameHeight = this.getHeight();

    if (outsideFrameWidth < MIN_WINDOW_WIDTH)
    {
      outsideFrameWidth = MIN_WINDOW_WIDTH;
      sizeIsOkay = false;
    }

    if (outsideFrameHeight < MIN_WINDOW_HEIGHT)
    {
      outsideFrameHeight = MIN_WINDOW_HEIGHT;
      sizeIsOkay = false;
    }

    if (outsideFrameHeight > outsideFrameWidth)
    {
      outsideFrameHeight = outsideFrameWidth;
      sizeIsOkay = false;
    }
    if (outsideFrameWidth > outsideFrameHeight * 2)
    {
      outsideFrameWidth = outsideFrameHeight * 2;
      sizeIsOkay = false;
    }

    Insets inset = this.getInsets();
    windowPixelWidth = outsideFrameWidth - inset.left - inset.right;
    windowPixelHeight = outsideFrameHeight - inset.top - inset.bottom;

    System.out.println("NeuroFrame.resizeHelper(): Enter (" + windowPixelWidth
        + ", " + windowPixelHeight + ")");

    if (sizeIsOkay)
    {
      Library.setWindowPixelWidth(windowPixelWidth);
      Library.setWindowPixelHeight(windowPixelHeight);
      Library.U_VALUE = windowPixelHeight;

      drawPanel.resizeHelper(windowPixelWidth, windowPixelHeight);
    }
    else
    {
      this.setSize(outsideFrameWidth, outsideFrameHeight);
    }

  }

  /**
   * Setter for the HUD statistics. Called by the GameController when the HUD
   * data needs to be updated.
   * 
   * @param score
   *          Number of points (long).
   * @param coins
   *          Number of coins (int).
   * @param health
   *          Player health (int).
   * @param powerUp
   *          PowerUp whose icon should be displayed on the hud.
   */
  public void setStats(int score, int coins, int health, PowerUp powerUp)
  {
    this.score = score;
    this.coins = coins;
    this.health = health;
    this.powerUp = powerUp;
  }

  public void render(ArrayList<GameObject> gameObjList)
  {
    drawPanel.render(gameObjList);
  }

  // public void renderBackground()
  // { drawPanel.renderBackground();
  // }

  /**
   * Enable or disable the debugging overlay.
   */
  public void setDebug(boolean debug)
  {
    this.debug = debug;
  }

  /**
   * // * Draw the debugging overlay. Draws vertical lines with labels every 0.5
   * u // * within the world. //
   */
  // public void debug()
  // {
  // Player player = world.getPlayer();
  // // U-coordinates.
  // graphics.setColor(Color.CYAN);
  // graphics.setFont(new Font("Serif", Font.PLAIN, 14));
  // double deltaX = world.getDeltaX();
  //
  // // xMin and yMin use integer rounding to guarantee that the demarcated
  // // lines are aligned to u values of 0.5.
  // double xMin = (int) deltaX - 2;
  // double xMax = deltaX + Library.HORIZONTAL_MAX;
  // double yMin = -2;
  // double yMax = Library.VERTICAL_MAX;
  // // X-axes.
  // for (double x = xMin; x < xMax; x += 0.5)
  // {
  // int w = (int) ((x - deltaX) * getHeight());
  // String xString = String.format("x = %.1f u", x);
  // graphics.drawLine(w, 0, w, windowHeight - 14);
  // graphics.drawString(xString, w - 14, windowHeight - 5);
  // }
  // // Y-axes.
  // graphics.setColor(Color.PINK);
  // for (double y = yMin; y < yMax; y += 0.5)
  // {
  // int w = (int) ((y) * windowHeight);
  // String yString = String.format("y = %.1f u", y);
  // graphics.drawLine(yString.length() * 7, w, windowWidth, w);
  // graphics.drawString(yString, 5, w + 5);
  // }
  // graphics.setColor(Color.WHITE);
  // graphics.drawString(String.format("deltaX: %.16f u", deltaX), 5, 50);
  // graphics
  // .drawString(String.format("playerX: %.16f u", player.getX()), 5, 90);
  // graphics.drawString(String.format("playerY: %.16f u", player.getY()), 5,
  // 110);
  // }

}
