
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

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.swing.JComponent;

import neurogame.gameplay.DirectionVector;
import neurogame.gameplay.Enemy;
import neurogame.gameplay.GameObject;
import neurogame.gameplay.GameObjectType;
import neurogame.gameplay.Player;
import neurogame.gameplay.PowerUp;
import neurogame.gameplay.Star;
import neurogame.level.World;
import neurogame.library.KeyBinds;
import neurogame.library.Library;
import neurogame.library.PlayerControls;

import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;

/**
 * The main game controller for NeuroGame.
 *
 * @author Ramon A. Lovato
 * @team Danny Gomez
 * @team Marcos Lemus
 */
public class GameController
{

  public enum GameState
  {
    INITIALIZING, //Skip all rendering
    TITLE,        //Title / option screen displayed
    PLAYING,      // Normal game play
    PAUSED,       // Normal game play paused
    DEAD,         // TODO : Player has died, continue showing game moving, no player controls, display "Game Over" overlay.
    GAMEOVER,     // show the high scores 
    ODDBALL,	  // Oddball visual test 
    HIGHSCORE;	  // Highscore screen after winning
  }
  
  private final NeuroGame game;
  private final PlayerControls controls;
  private final KeyBinds keyBinds;
  private final Map<String, Boolean> inputs;
  private final Map<String, Boolean> previousInputs;
  private NeuroFrame frame;
  private Oddball oddball;


  private boolean loggingMode;
  private Logger log;
  
  
  private boolean soundEnabled;
  private boolean godMode;
  private boolean suicideEnabled;
  private boolean globalDebug;
  private boolean debug;

  private int health;

  private boolean controllable;
  private boolean useJoystick;
  
  private GameState gameState;
  private TitleScreen title;
  private GameOverScreen gameOver;

  private World world;
  private Player player;
  private PowerUp powerUp;

  private Controller joystick = null;
  private static final int JOYSTICK_X = 1;
  private static final int JOYSTICK_Y = 0;
  private static final double JOYSTICK_THRESHOLD = 0.01;
  private double joystickLastX, joystickLastY;
  private boolean ButtonPressed;

  private boolean joystickReady;

  private double timepassed;

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
  public GameController(NeuroGame game, NeuroFrame frame)
  {
    System.out.println("GameController() Enter");
    this.game = game;
    this.frame = frame;

    controls = new PlayerControls();
    keyBinds = new KeyBinds((JComponent) frame.getContentPane(), controls);
    inputs = controls.getInputs();

    loggingMode = true;
    soundEnabled = true;
    godMode = false;
    suicideEnabled = false;
    globalDebug = false;
    debug = false;
    health = Library.HEALTH_MAX;
    powerUp = null;
    controllable = false;
    timepassed = 0.0;

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
    //keyBinds.addBinding(KeyEvent.VK_SPACE, "shoot");

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


  public void mainGameLoop()
  {
    long timeMS_start = System.currentTimeMillis();
    long timeMS_curr = timeMS_start;
    long timeMS_last = timeMS_start;
    update(0);

    while (true)
    {

      long deltaMilliSec = System.currentTimeMillis() - timeMS_last;

      if (deltaMilliSec < Library.MIN_FRAME_MILLISEC)
      {
        try { Thread.sleep(Library.MIN_FRAME_MILLISEC - deltaMilliSec); }
        catch (Exception e) { }
      }
      timeMS_curr = System.currentTimeMillis();
      double deltaSec = (timeMS_curr - timeMS_last) / 1000.0;

      timeMS_last = timeMS_curr;
      update(deltaSec);
    }
  }



  /**
   * Perform framewise updates.
   */
  private void update(double deltaSec)
  {
    switch (gameState)
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
    case GAMEOVER:
      gameOverUpdate(deltaSec);
      //highscoreUpdate();
      break;
    case HIGHSCORE:
      highscoreUpdate();
      break;
    case ODDBALL:
      oddballUpdate();
      break;
    default:
      break;
    }

    ArrayList<GameObject> gameObjectList = null;
    if (world != null)
    { gameObjectList = world.getObjectList();
    }

    /**
    * Don't draw to the screen every millisec.
    */
    timepassed += deltaSec;
    if(timepassed > 1.5/60.0){
        timepassed = 0.0;
        frame.render(gameObjectList);
    }
  }

  private void playUpdate(double deltaTime)
  {
    
    // Player input.
    keyHandler();
    double scrollDistance = world.update(deltaTime);

    // Draw the Zappers.
    // updateObjectList(zappers, deltaTime);
    // Update and draw GameObjects.
    updateObjectList(world.getObjectList(), deltaTime, scrollDistance);

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
    frame.setStats(player.getScore(), health, powerUp);
    
    if (loggingMode) log.update(world);
  }


  /**
 * Continue scrolling the game when the player dies
 */
private void gameOverUpdate(double deltaTime)
  {
    // Player input.
	gameOverKeyHandler();
    double scrollDistance = world.update(deltaTime);

    // Draw the Zappers.
    updateObjectList(world.getObjectList(), deltaTime, scrollDistance);
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

    //Check for object / object collisions after all objects have updated
    for (int i = 0; i < gameObjList.size(); i++)
    {
      GameObject obj1 = gameObjList.get(i);
      GameObjectType type1 = obj1.getType();
      if (obj1.isAlive() == false) continue;
      if (type1.hasCollider() == false) continue;

      for (int k = i+1; k < gameObjList.size(); k++)
      {
        GameObject obj2 = gameObjList.get(k);
        GameObjectType type2 = obj2.getType();
        if (!obj2.isAlive()) continue;
        if (type2.hasCollider() == false) continue;

        if ((!type1.isDynamic()) && (!type2.isDynamic())) continue;

        if (obj1.collision(obj2))
        {
          //System.out.println("HIT: " + obj1 + " <--> " + obj2);
          obj1.hit(obj2);
          obj2.hit(obj1);
        }
      }
    }
  }

  /**
   * Handler for keyboard input.
   */
  private void keyHandler()
  {
    // Use power-up.
    if (inputs.get("space"))
    {
      usePowerUp();
    }

    if (joystick != null && useJoystick==true)
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
      if (gameState == GameState.PAUSED)
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
      gameState = GameState.PAUSED;
      gameState = GameState.PLAYING;
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

    }
  }
  
  /**
   * Handler for keyboard input when you die
   */
  private void gameOverKeyHandler()
  {
	  boolean ButtonCheck = false;
	  
    if (inputs.get("space"))
    {
    	showHighScores();
    }

    if(joystick!=null){
		joystick.poll();

		for (int i = 0; i < 5; i++) {
			if (joystick.isButtonPressed(i)) {
				ButtonCheck = true;
			}
		}
		
		if((ButtonCheck == false) && (ButtonPressed == true)){
			ButtonPressed = false;
			this.showHighScores();
		}
		else if(ButtonCheck == true){
			ButtonPressed = true;
		}
	}
  }

  /**
   * Activate the powerUp (if held).
   */
  private void usePowerUp()
  {
    player.shootMissile();
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

    if (joystick == null || this.useJoystick == false)
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
      
      //joystickLastX = joystick.getAxisValue(JOYSTICK_X);
      //joystickLastY = joystick.getAxisValue(JOYSTICK_Y);

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
  }

  /**
   * Start a new game.
   */
  private void newGame()
  {
    System.out.println("GameController.newGame()  loggingMode="+loggingMode );
    gameState = GameState.PLAYING;

    
    world = new World();
    Star.initGame();
    // zappers = world.getZappers();
    frame.startGame(world);
    controllable = true;
    player = world.getPlayer();
    health = player.getHealth();
    GameObject.resetGameObjectCount();
    PowerUp.initGame();
    Enemy.initGame();

    if (loggingMode) log = new Logger();
  }
  
  private void showOddBall(){  
	oddball = new Oddball(frame);
	gameState = GameState.ODDBALL;
  }
  /**
   * Pause the game.
   */
  private void pause()
  {
    gameState = GameState.PAUSED;
    frame.pause();
  }

  /**
   * Unpause the game.
   */
  private void unpause()
  {
    gameState = GameState.PLAYING;
    frame.unpause();
  }

  /**
   * End the current game.
   */
  private void gameOver()
  {
    if (loggingMode) 
    { log.closeLog();
      log = null;
    }
    
    
  	frame.getUser().saveHighscore(player.getScore());
   	Library.saveUser(frame.getUser());

    showGameOver();
  }
 

 /**
 * Polls the highscore panel and gives joystick inputs 
 */
private void highscoreUpdate(){
	  if (gameOver != null)
	    {	    	
	    	if(joystick!=null){
	    		gameOver.updateJoystick(joystick, JOYSTICK_X, JOYSTICK_Y);
	    	}
	    	
		  if (gameOver.IsStarting)
	      {
	        controls.disableAll();
	        this.showTitle();
	      }
	      else if (gameOver.IsExiting)
	      {
	        controls.disableAll();
	        game.quit();
	      }
	      else if(gameOver.IsRestarting){
	    	  controls.disableAll();
	          newGame();
	      }
	    }

  }
  
  private void oddballUpdate(){
	  if(oddball.isFinished() == true){
		  showTitle();
	  }
	  
	  if (oddball != null)
	    {	    	
	    	if(joystick!=null){
	    		oddball.updateJoystick(joystick, JOYSTICK_X, JOYSTICK_Y);
	    	}
	    }
  }
  
  
  /**
   * A helper method to handle keyboard input on the title screen.
   */
  private void titleUpdate()
  {
    if (title != null)
    {
    	title.ScrollCredits();
    	SelectJoystick(title.GetSelectedJoystick(), title.GetSelectedJoystickIndex());
    	
    	if(joystick!=null && useJoystick==true){
    		title.updateJoystick(joystick, JOYSTICK_X, JOYSTICK_Y);
    	}

      if (title.IsStarting)
      {
        controls.disableAll();
        setLoggingMode(title.GetLogging());
        SelectJoystick(title.GetSelectedJoystick(), title.GetSelectedJoystickIndex());

        frame.setUser(title.GetSelectedUser());
        newGame();

      }
      else if (title.IsExiting)
      {
        controls.disableAll();
        game.quit();
      }
      else if (title.IsOption)
      {
			System.out.println("OddBall Test");
			showOddBall();
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
  private void SelectJoystick(int selectedIndex, int JoyIndex){
      try{
          Controllers.create();
      } catch(Exception e){
          e.printStackTrace();
          System.exit(0);
      }

      joystickReady = false;
      useJoystick = false;
      
      if(selectedIndex > 0){
    	  	useJoystick = true;
    	  	
	        Controller controller = Controllers.getController(selectedIndex-1);
	        joystick = controller;
	        joystick.poll();
	        
	        try{
	        	joystickLastX = joystick.getAxisValue(0);
	        	joystickLastY = joystick.getAxisValue(1);
	        }catch(Exception e){
	        	useJoystick = false;
	        }
      }
  }

  /**
   * Displays the title screen.
   */
  private void showTitle(){
    gameState = GameState.TITLE;
    title = frame.showTitle();
  }
  
  private void showGameOver(){
	  gameState = GameState.GAMEOVER;
	  frame.showGameOver();
	  this.player.die(true);
  }
  
  private void showHighScores(){
	  gameState = GameState.HIGHSCORE;
	  gameOver = frame.showHighScores();
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



}
