/**
 * ` * NeuroGame.
 * CS 351, Project 3
 *
 * Team members:
 * Ramon A. Lovato
 * Danny Gomez
 * Marcos Lemus
 */
package neurogame.main;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;

import java.util.ListIterator;

import neurogame.library.*;
import neurogame.gameplay.*;
import neurogame.io.IOExecutor;
import neurogame.level.*;

/**
 * The main game controller for NeuroGame.
 *
 * @author Ramon A. Lovato
 * @team Danny Gomez
 * @team Marcos Lemus
 */
public class GameController
{

  private final NeuroGame game;
  private final PlayerControls controls;
  private final KeyBinds keyBinds;
  private final Map<String, Boolean> inputs;
  private final Map<String, Boolean> previousInputs;
  private NeuroFrame frame;

  private IOExecutor executor;

  private static Graphics2D graphics;

  private boolean loggingMode;
  private boolean soundEnabled;
  private boolean godMode;
  private boolean suicideEnabled;
  private boolean globalDebug;
  private boolean debug;

  private int health;
  private int frameCounter;

  private boolean controllable;
  private boolean scrolling;

  private GameMode mode;
  private TitleScreen title;

  private World world;
  private Player player;
  private PowerUp powerUp;
  private List<GameObject> gameObjectList;
  private List<GameObject> zappers;
  private double deltaX;

  private Controller joystick = null;
  private static final int JOYSTICK_X = 1;
  private static final int JOYSTICK_Y = 0;
  private static final double JOYSTICK_THRESHOLD = 0.01;
  private double joystickLastX, joystickLastY;

  private boolean joystickReady;

  /**
   * Instantiates a new GameController.
   *
   * @param game
   *          NeuroGame that owns this GameController.
   * @param frame
   *          NeuroFrame displaying this game.
   * @param executor
   *          IOExecutor for handling logging and communications.
   */
  public GameController(NeuroGame game, NeuroFrame frame, IOExecutor executor)
  {
    System.out.println("GameController() Enter");
    this.game = game;
    this.frame = frame;
    this.executor = executor;

    controls = new PlayerControls();
    keyBinds = new KeyBinds((JComponent) frame.getContentPane(), controls);
    inputs = controls.getInputs();

    gameObjectList = null;
    zappers = null;
    deltaX = 0;
    loggingMode = false;
    soundEnabled = true;
    godMode = false;
    suicideEnabled = false;
    globalDebug = false;
    debug = false;
    health = Library.HEALTH_MAX;
    powerUp = null;
    frameCounter = 0;
    controllable = false;
    scrolling = false;

    // Joystick
    try
    {
      Controllers.create();
    }
    catch (Exception e)
    {
      e.printStackTrace();
      System.exit(0);
    }

    int count = Controllers.getControllerCount();
    // System.out.println(count + " Controllers Found");

    // Cursor myCursor = org.lwjgl.input.Mouse.getNativeCursor();
    // myCursor.setCursorPosition(0, 0);
    // org.lwjgl.input.Mouse.setGrabbed(true);
    // org.lwjgl.input.Mouse.setCursorPosition(0, 0);
    // Display.getImplementation().setCursorPosition(0, 0);
    joystickReady = false;
    for (int i = 0; i < count; i++)
    {
      Controller controller = Controllers.getController(i);
      if (controller.getName().contains("Gamepad"))
      {
        joystick = controller;
        System.out.println("Gamepad found at index " + i);

        joystick.poll();
        joystickLastX = joystick.getAxisValue(JOYSTICK_X);
        joystickLastY = joystick.getAxisValue(JOYSTICK_Y);

        break;
      }
    }

    // //////////////
    controls.addBinding("sound");
    keyBinds.addBinding(KeyEvent.VK_F1, "sound");

    // A copy of the inputs map used for logging changes in input states.
    // This must be done before adding the debugging key binds or the
    // debugging keys will be logged as well.
    previousInputs = new HashMap<String, Boolean>();
    for (Map.Entry<String, Boolean> e : inputs.entrySet())
    {
      previousInputs.put(e.getKey(), e.getValue().booleanValue());
    }

    // Special key bindings for sound, suicide (God-mode-only), and zoom
    // (debug only). Must be created before showing the title.
    controls.addBinding("suicide");
    keyBinds.addBinding(KeyEvent.VK_BACK_SPACE, "suicide");
    controls.addBinding("zoom_in");
    keyBinds.addBinding(KeyEvent.VK_EQUALS, "zoom_in");
    controls.addBinding("zoom_out");
    keyBinds.addBinding(KeyEvent.VK_MINUS, "zoom_out");
    controls.addBinding("zoom_reset");
    keyBinds.addBinding(KeyEvent.VK_BACK_SLASH, "zoom_reset");
    controls.addBinding("toggle_centered");
    keyBinds.addBinding(KeyEvent.VK_QUOTE, "toggle_centered");
    controls.addBinding("debug");
    keyBinds.addBinding(KeyEvent.VK_BACK_QUOTE, "debug");
    // Enemy spawners.
    controls.addBinding("enemy_a");
    keyBinds.addBinding(KeyEvent.VK_1, "enemy_a");
    controls.addBinding("enemy_b");
    keyBinds.addBinding(KeyEvent.VK_2, "enemy_b");
    controls.addBinding("enemy_c");
    keyBinds.addBinding(KeyEvent.VK_3, "enemy_c");
    // Coin spawner.
    controls.addBinding("coin");
    keyBinds.addBinding(KeyEvent.VK_C, "coin");
    // Zapper spawner.
    controls.addBinding("zapper");
    keyBinds.addBinding(KeyEvent.VK_Z, "zapper");

    showTitle();
  }

  public static void setGraphics(Graphics2D g)
  {
    graphics = g;
  }

  public void mainGameLoop()
  {
    long timeMS_start = System.currentTimeMillis();
    long timeMS_curr = timeMS_start;
    long timeMS_last = timeMS_start;
    update(0);

    while (true)
    {
      while (mode == GameMode.PAUSED)
      {
        try
        {
          Thread.sleep(Library.MIN_FRAME_MILLISEC);
        }
        catch (Exception e)
        {
        }
      }

      long deltaMilliSec = System.currentTimeMillis() - timeMS_last;

      if (deltaMilliSec < Library.MIN_FRAME_MILLISEC)
      {
        try
        {
          Thread.sleep(Library.MIN_FRAME_MILLISEC - deltaMilliSec);
        }
        catch (Exception e)
        {
        }
      }
      timeMS_curr = System.currentTimeMillis();
      double deltaSec = (timeMS_curr - timeMS_last) / 1000.0;

      timeMS_last = timeMS_curr;
      update(deltaSec);
    }
  }

  /**
   * Kill the IOExecutor. Called when exiting the program.
   */
  public void killExecutor()
  {
    executor.killAll();
  }

  /**
   * Perform framewise updates.
   */
  private void update(double deltaSec)
  {
    frameCounter++;

    switch (mode)
    {
    case PLAYING:
      playUpdate(deltaSec);
      if (health <= 0)
      {
        killPlayer();
        gameOver();
      }
      break;
    case PAUSED:
      pauseUpdate();
      break;
    case TITLE:
      titleUpdate();
      break;
    default:
      break;
    }

    frame.render(gameObjectList);
  }

  private void playUpdate(double deltaTime)
  {
    player.addScore(deltaTime * Library.SCORE_PER_SEC);
    // Player input.
    keyHandler();
    double scrollDistance = world.update(deltaTime);

    // Draw the Zappers.
    // updateObjectList(zappers, deltaTime);
    // Update and draw GameObjects.
    updateObjectList(gameObjectList, deltaTime, scrollDistance);
    player.update(deltaTime, scrollDistance);

    health = (godMode ? Library.HEALTH_MAX : player.getHealth());

    // Update the PowerUp.
    // powerUp = (player.getPowerUp() != null ? player.getPowerUp() : null);
    // if(powerUp != null){
    // powerUp.update(deltaTime, );
    //
    // if(powerUp.isAlreadyUsed()){
    // player.setPowerUp(null);
    // powerUp = null;
    // }
    // }

    // Set the info for the HUD.
    frame.setStats(player.getScore(), player.getTotalCoinsEarnedThisGame(),
        health, powerUp);
  }

  /**
   * Helper method for handling GameObject updates.
   *
   * @param list
   *          List<GameObject> to iterate over and update.
   * @param deltaTime
   */
  public void updateObjectList(List<GameObject> gameObjList, double deltaTime,
      double scrollDistance)

  {
    for (ListIterator<GameObject> iterator = gameObjList.listIterator(); iterator
        .hasNext();)
    {
      GameObject obj = iterator.next();

      boolean isAlive = obj.update(deltaTime, scrollDistance);
      
      if (!isAlive) 
      { obj.die();
        iterator.remove();
      }
      else
      {
      }

    }
  }

  /**
   * Handler for keyboard input.
   */
  private void keyHandler()
  {
    // Log key changes.
    logInputs();
    // Use power-up.
    if (inputs.get("space"))
    {
      usePowerUp();
    }

    if (joystick != null)
    {
      if (joystick.isButtonPressed(0))
      {
        usePowerUp();
      }
      else if (joystick.isButtonPressed(1))
      {
        usePowerUp();
      }
      else if (joystick.isButtonPressed(2))
      {
        usePowerUp();
      }
      else if (joystick.isButtonPressed(3))
      {
        usePowerUp();
      }
      else if (joystick.isButtonPressed(4))
      {
        usePowerUp();
      }
      else if (joystick.isButtonPressed(5))
      {
        usePowerUp();
      }
    }

    // Pause/unpause.
    if (inputs.get("pause"))
    {
      controls.disableAll();
      if (mode == GameMode.PAUSED)
      {
        unpause();
      }
      else
      {
        pause();
      }
    }
    // Toggle full-screen mode.
    if (inputs.get("escape"))
    {
      inputs.put("escape", false);
      controls.disableAll();
      // When toggling screen mode, we need to stop the timer to make
      // sure render() doesn't get called while the switch is taking
      // place, as this could cause multithreading issues.
      mode = GameMode.PAUSED;
      mode = GameMode.PLAYING;
    }
    // Movement. If controllable is false, ignore.
    if (controllable)
    {
      DirectionVector dir = moveHelper();
      if (player != null)
      {
        player.setDirection(dir);
      }
    }

    // Debugging keys.
    if (globalDebug)
    {
      // Toggle debug - only if globalDebug.
      if (inputs.get("debug"))
      {
        inputs.put("debug", false);
        setDebug(!debug);
      }
      // Suicide - only if suicideEnabled or debugging active.
      if (inputs.get("suicide") && suicideEnabled)
      {
        inputs.put("suicide", false);
        killPlayer();
        gameOver();
      }

      // // Toggle centered - only if debug.
      // if (inputs.get("toggle_centered") && debug)
      // {
      // inputs.put("toggle_centered", false);
      // engine.toggleCentered();
      // }
      // Debug enemy spawners.
//      if (inputs.get("enemy_a") && debug)
//      {
//        inputs.put("enemy_a", false);
//        gameObjectList.add(new EnemyStraight(deltaX + 1.0, 0.5, world));
//      }
//      if (inputs.get("enemy_b") && debug)
//      {
//        inputs.put("enemy_b", false);
//        gameObjectList.add(new EnemySinusoidal(deltaX + 1.0, 0.5, world));
//      }
//      if (inputs.get("enemy_c") && debug)
//      {
//        inputs.put("enemy_c", false);
//        gameObjectList.add(new EnemyFollow(deltaX + 1.0, 0.5, world));
//      }
//      // Debug coin spawner.
//      if (inputs.get("coin") && debug)
//      {
//        inputs.put("coin", false);
//        gameObjectList.add(new Coin(deltaX + 1.0, 0.5, world));
//      }
//      // Debug zapper spawner.
//      if (inputs.get("zapper") && debug)
//      {
//        inputs.put("zapper", false);
//        gameObjectList.add(new Zapper(deltaX + 1.25, 0.3, deltaX + 1.25, 0.7,
//            world));
//      }
    }
  }

  /**
   * Activate the powerUp (if held).
   */
  private void usePowerUp()
  {
    if (powerUp != null)
    {
      powerUp.activate();
    }
  }

  /**
   * Helper method for keyHandler that processes movement keypresses.
   *
   * @return Directions enum for how the ship should move, or null if it should
   *         remain stationary.
   */
  private DirectionVector moveHelper()
  {
    DirectionVector dir = new DirectionVector();

    boolean n = false;
    boolean s = false;
    boolean w = false;
    boolean e = false;

    if (joystick == null)
    {
      n = inputs.get("up");
      s = inputs.get("down");
      w = inputs.get("left");
      e = inputs.get("right");

      // System.out.println(n + " " + e + " " + s + " " + w);
      if (n)
      {
        dir.y = -1;
      }
      else if (s)
      {
        dir.y = 1;
      }

      if (e)
      {
        dir.x = 1;
      }
      else if (w)
      {
        dir.x = -1;
      }
    }

    else
    {
      joystick.poll();
      double stickX = joystick.getAxisValue(JOYSTICK_X);
      double stickY = joystick.getAxisValue(JOYSTICK_Y);

      if (!joystickReady)
      {
        double deltaX = Math.abs(joystickLastX - stickX);
        double deltaY = Math.abs(joystickLastY - stickY);
        if ((deltaX > JOYSTICK_THRESHOLD) || (deltaY > JOYSTICK_THRESHOLD))
        {
          joystickReady = true;
        }
      }

      if (joystickReady)
      {
        dir.x = stickX;
        dir.y = stickY;
      }
    }
    return dir;

  }

  /**
   * Kill the player.
   */
  private void killPlayer()
  {
    controllable = false;
    scrolling = false;
  }

  /**
   * Start a new game.
   */
  private void newGame()
  {
    if (loggingMode)
    {
      executor.setupLogger();
    }
    mode = GameMode.PLAYING;
    deltaX = 0;

    
    world = new World();
    gameObjectList = world.getObjectList();
    Coin.initGame();
    // zappers = world.getZappers();
    frame.startGame(world);
    controllable = true;
    scrolling = true;
    player = world.getPlayer();
    health = player.getHealth();

    log("New game.");
  }

  /**
   * Pause the game.
   */
  private void pause()
  {
    mode = GameMode.PAUSED;
    frame.pause();
    log("Game paused.");
  }

  /**
   * Unpause the game.
   */
  private void unpause()
  {
    mode = GameMode.PLAYING;
    frame.unpause();
    log("Game unpaused.");
  }

  /**
   * End the current game.
   */
  private void gameOver()
  {
    log("Game over.");
    executor.killLogger();
    showTitle();
  }

  /**
   * A helper method to handle keyboard input on the title screen.
   */
  private void titleUpdate()
  {
    if (title != null)
    {
      // Controls.
      /*if (inputs.get("left") || inputs.get("right"))
      {
        controls.disableAll();
        title.switchButton();
      }
      if (inputs.get("enter"))
      {
        if (title.getSelected() == "start")
        {
          controls.disableAll();
          newGame();
        }
        else
        {
          controls.disableAll();
          game.quit();
        }
      }*/
      
      if(title.IsStarting){
  		controls.disableAll();
  		newGame();
  		SelectJoystick(title.selectedJoystick);
  		
	  	}
	  	else if(title.IsExiting){
	  		controls.disableAll();
	  		game.quit();
	  	}
	  	else if(title.IsOption){
	  		
	  	}

      if (inputs.get("sound"))
      {
        soundEnabled = !soundEnabled;
      }
    }
  }
  
  /**
   * Selects Joystick based on Options Dialog choice
   * @param selectedIndex
   */
  private void SelectJoystick(int selectedIndex){
      try{
          Controllers.create();
      } catch(Exception e){
          e.printStackTrace();
          System.exit(0);
      }

      joystickReady = false;
      if(selectedIndex > 0){
	        Controller controller = Controllers.getController(selectedIndex-1);
	        joystick = controller;
	        joystick.poll();
	        joystickLastX = joystick.getAxisValue(JOYSTICK_X);
	        joystickLastY = joystick.getAxisValue(JOYSTICK_Y);
      }
  }

  /**
   * Displays the title screen.
   */
  private void showTitle()
  {
    mode = GameMode.TITLE;
    title = frame.showTitle();
  }

  /**
   * Handles keyboard events while paused.
   */
  private void pauseUpdate()
  {

    if (inputs.get("pause"))
    {
      inputs.put("pause", false);
      unpause();
    }
    if (inputs.get("sound"))
    {
      soundEnabled = !soundEnabled;
    }
  }

  /**
   * Send the passed String to the IOExecutor to be queued for logging. Does
   * nothing if logging is disabled.
   *
   * @param s
   *          String to send to the IOExecutor. The executor's Logger will add
   *          the time stamp automatically.
   */
  public void log(String s)
  {
    if (loggingMode)
    {
      executor.logEntry(s);
    }
  }

  /**
   * Send the passed String to the IOExecutor to be queued for logging with an
   * attached risk. Does nothing if logging is disabled.
   *
   * @param s
   *          String to send to the IOExecutor. The executor's Logger will add
   *          the time stamp automatically.
   * @param risk
   *          Double representing the risk to attach to the log entry.
   */
  public void log(String s, double risk)
  {
    if (loggingMode)
    {
      executor.logEntry(s, risk);
    }
  }

  /**
   * Log changes in the inputs map.
   */
  public void logInputs()
  {
    for (Map.Entry<String, Boolean> e : inputs.entrySet())
    {
      String key = e.getKey();
      // Prevent debugging keys from being logged.
      if (previousInputs.containsKey(key))
      {
        boolean value = e.getValue().booleanValue();
        boolean previousValue = previousInputs.get(key);
        if (value != previousValue)
        {
          String logString = (value ? "pressed" : "released");
          if (world != null)
          {
            // log("Input " + key + " " + logString + ".", world.getRisk());
          }
          else
          {
            log("Input " + key + " " + logString + ".");
          }
        }
        previousInputs.put(key, value);
      }
    }
  }

  /**
   * Setter for loggingMode.
   *
   * @param loggingMode
   *          Boolean for enabling/disabling local logging.
   */
  public void setLoggingMode(boolean loggingMode)
  {
    this.loggingMode = loggingMode;
  }

  /**
   * Setter for enabling/disabling the sound.
   *
   * @param soundEnabled
   *          Enable/disable the sound if true/false, respectively.
   */
  public void setSound(boolean soundEnabled)
  {
    this.soundEnabled = soundEnabled;
  }

  /**
   * Setter for God mode. The global God mode is unaffected by the immunity
   * granted by the super PowerUp.
   *
   * @param godMode
   *          Boolean for enabling/disabling God mode.
   */
  public void setGodMode(boolean godMode)
  {
    this.godMode = godMode;
  }

  /**
   * Setter for suicideEnabled - the suicide button is inactive unless God mode
   * is explicitly invoked from the command line.
   *
   * @param suicideEnabled
   *          Boolean for enabling/disabling the suicide button.
   */
  public void setSuicideEnabled(boolean suicideEnabled)
  {
    this.suicideEnabled = suicideEnabled;
  }

  /**
   * Setter for debug mode.
   *
   * @param debug
   *          Boolean for enabling/disabling debug mode.
   */
  public void setDebug(boolean debug)
  {
    this.debug = debug;
    frame.setDebug(debug);
  }

  /**
   * Setter for global debug mode.
   *
   * @param globalDebug
   *          Boolean for enabling/disabling global debug mode.
   */
  public void setGlobalDebug(boolean globalDebug)
  {
    this.globalDebug = globalDebug;
    Library.setDebug(debug);
  }

  /**
   * A nested enum for the current game mode.
   */
  public enum GameMode
  {

    INITIALIZING, TITLE, PLAYING, PAUSED, DEAD;
  }

}
