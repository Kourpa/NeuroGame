/**
 * NeuroGame.
 * CS 351, Project 3
 * 
 * Team members:
 * Ramon A. Lovato
 * Danny Gomez
 * Marcos Lemus
 */

//GIT Repository
//git clone https://github.com/Kourpa/NeuroGame  //initial set up
//git config core.autocrlf false

//git status
//git add -A //when you create new fines that need to be added
//git commit -am 'comment' //commit -a (all) -m (message)
//git pull origin master
//git push origin master //use --force when fracking git is being a git.

//git stash //kill all local changes

package neurogame.main;

import java.awt.Container;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.UnsupportedLookAndFeelException;
import neurogame.gameplay.Enemy;
import neurogame.gameplay.GameObject;
import neurogame.gameplay.GameObjectType;
import neurogame.gameplay.Missile;
import neurogame.gameplay.Player;
import neurogame.gameplay.Ammo;
import neurogame.gameplay.Star;
import neurogame.level.World;
import neurogame.library.Library;
import neurogame.io.Logger;
import neurogame.io.Logger.LogType;
import neurogame.io.User;
import neurogame.io.InputController;

/**
 * NeuroGame's main class.
 * 
 * @author Ramon A. Lovato
 * @team Danny Gomez
 * @team Marcos Lemus
 */
@SuppressWarnings("serial")
public class NeuroGame extends JFrame implements ActionListener
{
  private InputController controller;

  public enum GameState
  {
    INITIALIZING, // Skip all rendering
    TITLE, // Title / option screen displayed
    PLAYING, // Normal game play
    PAUSED, // Normal game play paused
    GAMEOVER, // show the high scores
    ODDBALL, // Oddball visual test
    BANDIT; // Highscore screen after winning
  }

  private static final int MIN_WINDOW_WIDTH = 200;
  private static final int MIN_WINDOW_HEIGHT = 200;

  public static final double NANO_TO_SEC = 1.0e-9;

  private Timer timer;
  private double timeSec_start, timeSec_LastTick, timeCurrent, timeGameSec;

  private GameState gameState = GameState.INITIALIZING;
  private TitleScreen titlePanel;

  private World world;
  private Player player;

  private Oddball oddball;
  private Bandit bandit;
  public Logger log;

  private MainDrawPanel drawPanel;
  private int windowPixelWidth, windowPixelHeight;

  private Container contentPane;

  private User currentUser;

  /**
   * Initializes a new NeuroGame session.
   */
  public NeuroGame()
  {
    System.out.println("NeuroFrame(): Enter");

    this.setResizable(true);
    this.setTitle(Library.GAME_TITLE);

    //Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    //int screenWidth = screenSize.width;
    //int screenHeight = screenSize.height;

    //this.setBounds(0, 0, screenWidth, screenHeight);
    this.setExtendedState(JFrame.MAXIMIZED_BOTH);
    this.setVisible(true);
    contentPane = getContentPane();
    contentPane.setLayout(null);

    calculateWindowSize();

    Library.initSprites(this);
    Library.loadFont();

    // Load user profiles
    User.loadUsers();

    controller = new InputController();

    drawPanel = new MainDrawPanel(this, controller);
    contentPane.add(drawPanel);
    drawPanel.setVisible(false);

    titlePanel = new TitleScreen(this, controller);
    contentPane.add(titlePanel);

    resizeHelper();

    showTitle();

    this.addComponentListener(new ComponentAdapter()
    {
      public void componentResized(ComponentEvent e)
      {
        resizeHelper();
      }
    });

    // Listener to check for the frame closing and cleanly exit.
    addWindowListener(new WindowAdapter()
    {

      public void windowClosing(WindowEvent e)
      {
        quit();
      }
    });

    timeSec_start = System.nanoTime() * NANO_TO_SEC;
    timeSec_LastTick = timeSec_start;

    // Timer needs to run at 2x frame speed to set parallel port triggers so 0
    // between signals
    timer = new Timer(Library.MIN_FRAME_MILLISEC, this);
    timer.start();
  }

  /**
   * Perform framewise updates.
   */
  private void update(double deltaSec)
  {

    int keycode = controller.updatePlayerInput();
    switch (gameState)
    {
    case PLAYING:
      playUpdate(deltaSec);
      if (player.getHealth() <= 0)
      {
        killPlayer();
      }
      break;
    case PAUSED:
      if (keycode == KeyEvent.VK_P) togglePause();
      break;
    case TITLE:
      titlePanel.update(deltaSec, keycode);
      break;
    case GAMEOVER:
      gameOverUpdate(deltaSec, keycode);
      break;
    case ODDBALL:
      boolean oddballRunning = oddball.oddballUpdate(deltaSec, keycode);
      if (!oddballRunning) showTitle();
      break;
    case BANDIT:
      boolean banditRunning = bandit.banditUpdate(deltaSec);
      if (!banditRunning) showTitle();
    default:
      break;
    }

    ArrayList<GameObject> gameObjectList = null;
    if (world != null)
    {
      gameObjectList = world.getObjectList();
    }

    render(gameObjectList);
  }

  public double getGameSec()
  {
    return timeGameSec;
  }

  /**
   * Continue scrolling the game when the player dies
   */
  public void gameOverUpdate(double deltaTime, int keycode)
  {
    double scrollDistance = world.update(deltaTime);
    updateObjectList(world.getObjectList(), deltaTime, scrollDistance);

    if (currentUser.isLogging()) log.update(null, timeCurrent);
    
    if (keycode == KeyEvent.VK_ENTER) startGame(currentUser, false);
    else if (keycode == KeyEvent.VK_ESCAPE) 
    { 
      if (log != null) log.closeLog();
      showTitle();
    }
  }

  public World getWorld()
  {
    return world;
  }

  /**
   * Displays the title screen.
   */
  public void showTitle()
  {
    gameState = GameState.TITLE;
    drawPanel.setVisible(false);
    if (oddball != null) oddball.setVisible(false);
    else if (bandit != null) bandit.setVisible(false);
    titlePanel.showTitleScreen();

  }

  /**
   * Start a new game.
   */
  public void startGame(User currentUser, boolean startNewLog)
  {
    timeGameSec = 0;
    this.currentUser = currentUser;

    titlePanel.setVisible(false);

    world = new World(controller);

    drawPanel.initGame();

    // System.out.println("NeuroGame.newGame()  world="+world);

    Star.initGame();
    Missile.initGame();
    // zappers = world.getZappers();

    drawPanel.setWorld(world);

    controller.initGame();
    player = world.getPlayer();
    Ammo.initGame();
    Enemy.initGame();

    if (currentUser.isLogging())
    {
      //System.out.println("NeuroGame.startGame("+currentUser+", "+startNewLog+")");
      if (startNewLog)
      {
        if (log != null) log.closeLog();
        log = new Logger(controller, currentUser, LogType.GAME);
      }
      log.startGame();
    }

    gameState = GameState.PLAYING;

  }
  

  public void startOddBall(User currentUser)
  {
    this.currentUser = currentUser;
    titlePanel.setVisible(false);

    if (currentUser.isLogging())
    {
      if (log != null) log.closeLog();
      log = new Logger(controller, currentUser, LogType.ODDBALL);
    }
    if (oddball == null)
    {
      oddball = new Oddball(this, controller);
      this.add(oddball);
    }
    oddball.init(currentUser);
    gameState = GameState.ODDBALL;
  }

  public void startBandit(User currentUser)
  {
    this.currentUser = currentUser;
    titlePanel.setVisible(false);

    if (currentUser.isLogging())
    {
      if (log != null) log.closeLog();
      log = new Logger(controller, currentUser, LogType.BANDIT);
    }

    if (bandit == null)
    {
      bandit = new Bandit(this, controller);
      add(bandit);
    }

    bandit.init(currentUser);
    gameState = GameState.BANDIT;
  }

  /**
   * Pause the game.
   */
  public void togglePause()
  {
    if (gameState == GameState.PLAYING)
    {
      gameState = GameState.PAUSED;
    }
    else if (gameState == GameState.PAUSED)
    {
      gameState = GameState.PLAYING;
    }
  }

  private void killPlayer()
  {
    player.die(true);
    currentUser.setHighscore(player.getScore());
    gameState = GameState.GAMEOVER;
  }


  private void playUpdate(double deltaTime)
  {
    timeGameSec += deltaTime;
    // System.out.println("NeuroGame.game() = world=" + world);

    double scrollDistance = world.update(deltaTime);

    updateObjectList(world.getObjectList(), deltaTime, scrollDistance);

    if (currentUser.isLogging()) log.update(world, timeCurrent);
  }

  public GameState getGameState()
  {
    return this.gameState;
  }

  /**
   * Helper method for handling GameObject updates.
   *
   * @param list
   *          List<GameObject> to iterate over and update.
   * @param deltaTime
   */
  public void updateObjectList(List<GameObject> gameObjList, double deltaTime, double scrollDistance)
  {
    for (ListIterator<GameObject> iterator = gameObjList.listIterator(); iterator.hasNext();)
    {
      GameObject obj = iterator.next();

      if (obj.isAlive())
      {
        obj.update(deltaTime, scrollDistance);
      }
      if (obj.isAlive() == false)
      {
        iterator.remove();
      }
    }

    // Check for object / object collisions after all objects have updated
    for (int i = 0; i < gameObjList.size(); i++)
    {
      GameObject obj1 = gameObjList.get(i);
      GameObjectType type1 = obj1.getType();
      if (obj1.isAlive() == false) continue;
      if (type1.hasCollider() == false) continue;

      for (int k = i + 1; k < gameObjList.size(); k++)
      {
        GameObject obj2 = gameObjList.get(k);
        GameObjectType type2 = obj2.getType();
        if (!obj2.isAlive()) continue;
        if (type2.hasCollider() == false) continue;

        if ((!type1.isDynamic()) && (!type2.isDynamic())) continue;

        if (obj1.collision(obj2))
        {
          // System.out.println("HIT: " + obj1 + " <--> " + obj2);
          obj1.hit(obj2);
          obj2.hit(obj1);
        }
      }
    }
  }

  /**
   * Finish any outstanding tasks and exit the game cleanly.
   */
  public void quit()
  {
    if (currentUser != null && currentUser.isLogging())
    {
      if (log != null) log.closeLog();
      log = null;
    }

    System.exit(0);
  }

  /**
   * NeuroGame's main method.
   * 
   * @param args
   *          Command-line arguments.
   */
  public static void main(String[] args)
  {
    // OS X-specific tweaks. Recent versions completely ignore these, but
    // they're provided for legacy purposes.
    System.setProperty("apple.laf.useScreenMenuBar", "true");
    System.setProperty("com.apple.mrj.application.apple.menu.about.name", Library.GAME_TITLE);
    System.setProperty("apple.awt.fullscreenhidecursor", "true");
    try
    {
      javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getCrossPlatformLookAndFeelClassName());
    }
    catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex)
    {
      ex.printStackTrace();
    }

    new NeuroGame();
  }

  // public void setGameController(InputController cont){
  // this.controller = cont;
  // }

  public MainDrawPanel getDrawPanel()
  {
    return drawPanel;
  }

  /**
   * Sets the user for the session
   */
  public void setUser(User newUser)
  {
    this.currentUser = newUser;
  }

  public User getUser()
  {
    return this.currentUser;
  }

  /**
   * Returns the current user
   */
  public User getCurrentUser()
  {
    return this.currentUser;
  }

  /**
   * A helper method that gets called when the window is resized, which releases
   * the old buffered image and generates a new one with the new window
   * dimensions.
   */
  private void resizeHelper()
  {

    boolean sizeIsOkay = calculateWindowSize();

    if (sizeIsOkay)
    {
      drawPanel.resizeHelper(windowPixelWidth, windowPixelHeight);
      titlePanel.resizeHelper(windowPixelWidth, windowPixelHeight);

      System.out.println("NeuroFrame.resizeHelper(): Enter (" + windowPixelWidth + ", " + windowPixelHeight + ")");
    }
  }

  private boolean calculateWindowSize()
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

    if (!sizeIsOkay)
    {
      this.setSize(outsideFrameWidth, outsideFrameHeight);
      return false;
    }

    Library.setWindowPixelSize(windowPixelWidth, windowPixelHeight);
    Library.U_VALUE = windowPixelHeight;

    return true;
  }

  public void render(ArrayList<GameObject> gameObjList)
  {
    drawPanel.render(gameObjList);
  }

  // This is the main game loop controlled by a timer.
  public void actionPerformed(ActionEvent e)
  {
    // tick++;
    // if ((tick % 2) == 1)
    // { if (gameState == GameState.PLAYING)
    // { if (currentUser.isLogging())
    // { log.sendGroundIfNeeded();
    // }
    // }
    // return;
    // }

    timeCurrent = System.nanoTime() * NANO_TO_SEC;
    double deltaSec = timeCurrent - timeSec_LastTick;

    update(deltaSec);
    timeSec_LastTick = timeCurrent;
  }
}
