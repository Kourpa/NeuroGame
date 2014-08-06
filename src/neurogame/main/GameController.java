
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
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import neurogame.gameplay.DirectionVector;
import neurogame.library.KeyBinds;

import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;

/**
 * The main game controller for NeuroGame.
 *
 * @author Ramon A. Lovato
 * @team Danny Gomez
 * @team Marcos Lemus
 */
public class GameController{
  //private final PlayerControls controls;
  private final KeyBinds keyBinds;
  private final Map<String, Boolean> inputs;
  private final Map<String, Boolean> previousInputs;
  
  private NeuroGame game;
  private NeuroFrame frame;
 
  private boolean controllable;
  private boolean useJoystick;
  
  private Controller joystick = null;
  private static final int JOYSTICK_X = 1;
  private static final int JOYSTICK_Y = 0;
  private static final double JOYSTICK_THRESHOLD = 0.01;
  private double joystickLastX, joystickLastY;
  private boolean ButtonPressed;
  
  private static boolean playerIsPressingButton = false;

  private boolean joystickReady;
  
  private static DirectionVector playerInputDirectionVector = new DirectionVector();
  

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

    //controls = new PlayerControls();
    
    inputs = new HashMap<String, Boolean>();
    inputs.put("up", false);
    inputs.put("down", false);
    inputs.put("left", false);
    inputs.put("right", false);
    inputs.put("space", false);
    inputs.put("escape", false);
    inputs.put("enter", false);
    inputs.put("pause", false);
    
    
    
    keyBinds = new KeyBinds((JComponent) frame.getContentPane(), this);
    

    controllable = false;

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
    addBinding("sound");
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
    addBinding("suicide");
    keyBinds.addBinding(KeyEvent.VK_BACK_SPACE, "suicide");
    addBinding("zoom_in");
    keyBinds.addBinding(KeyEvent.VK_EQUALS, "zoom_in");
    addBinding("zoom_out");
    keyBinds.addBinding(KeyEvent.VK_MINUS, "zoom_out");
    addBinding("zoom_reset");
    keyBinds.addBinding(KeyEvent.VK_BACK_SLASH, "zoom_reset");
    addBinding("toggle_centered");
    keyBinds.addBinding(KeyEvent.VK_QUOTE, "toggle_centered");
    addBinding("debug");
    keyBinds.addBinding(KeyEvent.VK_BACK_QUOTE, "debug");
    // Enemy spawners.
    addBinding("enemy_a");
    keyBinds.addBinding(KeyEvent.VK_1, "enemy_a");
    addBinding("enemy_b");
    keyBinds.addBinding(KeyEvent.VK_2, "enemy_b");
    addBinding("enemy_c");
    keyBinds.addBinding(KeyEvent.VK_3, "enemy_c");
    // Coin spawner.
    addBinding("coin");
    keyBinds.addBinding(KeyEvent.VK_C, "coin");
    // Zapper spawner.
    addBinding("zapper");
    keyBinds.addBinding(KeyEvent.VK_Z, "zapper");

    game.showTitle();
  }

  public Map<String, Boolean> getInputs(){
	  return inputs;
  }
  
  public void setControllable(boolean controllable){
	  this.controllable = controllable;
  }
  

  /**
  * Polls the highscore panel and gives joystick inputs 
  */
  public void highscoreUpdate(){
 	  if (game.getGameOverScreen() != null)
 	  {
 	    	if(joystick!=null){
 	    		game.getGameOverScreen().updateJoystick(joystick, JOYSTICK_X, JOYSTICK_Y);
 	    	}
 	    	
 		  if (game.getGameOverScreen().IsStarting)
 	      {
 	        disableAll();
 	        game.showTitle();
 	      }
 	      else if (game.getGameOverScreen().IsExiting)
 	      {
 	        disableAll();
 	        game.quit();
 	      }
 	      else if(game.getGameOverScreen().IsRestarting){
 	    	  disableAll();
 	          game.newGame();
 	      }
 	    }
   }
   
 public void oddballUpdate(){
 	  if(game.getOddballScreen().isFinished() == true){
 		  game.showTitle();
 	  }
 	  
 	 oddballKeyHandler();
 	  
 	  if (game.getOddballScreen() != null)
 	    {	
 	    	if(joystick!=null){
 	    		game.getOddballScreen().updateJoystick(joystick, JOYSTICK_X, JOYSTICK_Y);
 	    	}
 	    }
   }
   
   /**
    * Continue scrolling the game when the player dies
    */
   public void gameOverUpdate(double deltaTime)
     {
       // Player input.
   		gameOverKeyHandler();
   		double scrollDistance = game.getWorld().update(deltaTime);

       // Draw the Zappers.
       game.updateObjectList(game.getWorld().getObjectList(), deltaTime, scrollDistance);
     }

   /**
    * A helper method to handle keyboard input on the title screen.
    */
   public void titleUpdate()
   {
     if (game.getTitleScreen() != null)
     {
     	game.getTitleScreen().ScrollCredits(0.1f);
     	SelectJoystick(game.getTitleScreen().GetSelectedJoystick(), game.getTitleScreen().GetSelectedJoystickIndex());
     	
     	if(joystick!=null && useJoystick==true){
     		game.getTitleScreen().updateJoystick(joystick, JOYSTICK_X, JOYSTICK_Y);
     	}

       if (game.getTitleScreen().IsStarting)
       {
         disableAll();
         //game.setLoggingMode(game.getTitleScreen().GetLogging());
         SelectJoystick(game.getTitleScreen().GetSelectedJoystick(), game.getTitleScreen().GetSelectedJoystickIndex());

         frame.setUser(game.getTitleScreen().GetSelectedUser());
         game.getTitleScreen().showTitleScreen(false);
         game.newGame();
       }
       else if (game.getTitleScreen().IsExiting)
       {
         disableAll();
         game.quit();
       }
       else if (game.getTitleScreen().IsOption)
       {
 			System.out.println("OddBall Test");
 			game.showOddBall();
       }
     }
   }

   
   
  /**
   * Handler for keyboard input.
   */
  public void keyHandler()
  {
    updateButtonStatus();

    // Pause/unpause.
    if (inputs.get("pause"))
    {
      disableAll();
      game.togglePause();
    }
    
    // Movement. If controllable is false, ignore.
    if (controllable)
    {
      moveHelper();
    }
  }
  
  
  
  public static boolean isPlayerPressingButton() { return playerIsPressingButton;}
  
  private void updateButtonStatus()
  { 
    playerIsPressingButton = false;
    
    if (inputs.get("space"))
    {
      playerIsPressingButton = true;
      return;
    }
    

    if ((joystick != null) && useJoystick)
    { 
      for (int i=0; i<6; i++)
      { if (joystick.isButtonPressed(i))
        {
          playerIsPressingButton = true;
          return;
        }
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
    	game.showHighScores();
    }

    if(joystick!=null){
		joystick.poll();

		for (int i = 0; i < 5; i++) {
			if (joystick.isButtonPressed(i)) {
				ButtonCheck = true;
			}
		}
		
		if((ButtonCheck == false) && (ButtonPressed)){
			ButtonPressed = false;
			game.showHighScores();
		}
		else if(ButtonCheck == true){
			ButtonPressed = true;
		}
	}
  }
  
  private void oddballKeyHandler(){
	  System.out.println("Oddball Key Handler: "+inputs.get("escape"));
	  
	  if (inputs.get("escape")){
		  game.getOddballScreen().forceClose();
	  }
  }

  
  /**
   * Helper method for keyHandler that processes movement keypresses.
   *
   * @return Directions enum for how the ship should move, or null if it should
   *         remain stationary.
   */
  private void moveHelper()
  {

    boolean n = false;
    boolean s = false;
    boolean w = false;
    boolean e = false;
    
    playerInputDirectionVector.x= 0;
    playerInputDirectionVector.y = 0;

    if (joystick == null || this.useJoystick == false)
    {
      n = inputs.get("up");
      s = inputs.get("down");
      w = inputs.get("left");
      e = inputs.get("right");

      // System.out.println(n + " " + e + " " + s + " " + w);
      if (n)
      {
        playerInputDirectionVector.y = -1;
      }
      else if (s)
      {
        playerInputDirectionVector.y = 1;
      }

      if (e)
      {
        playerInputDirectionVector.x = 1;
      }
      else if (w)
      {
        playerInputDirectionVector.x = -1;
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
        playerInputDirectionVector.x = stickX;
        playerInputDirectionVector.y = stickY;
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
   * Updates the input map based on the provided input. Called by KeyBinds.
   * 
   * @param str
   *          String representation of the key state change.
   */
  public void updateInput(String str)
  {
    Boolean state = (str.startsWith("released") ? false : true);
    String key = (state ? str : str.substring(9));
    inputs.put(key, state);
    // If one of the directions was pressed, automatically release the
    // opposite direction.
    if (state)
    {
      switch (str)
      {
      case "up":
        inputs.put("down", false);
        break;
      case "down":
        inputs.put("up", false);
        break;
      case "left":
        inputs.put("right", false);
        break;
      case "right":
        inputs.put("left", false);
        break;
      default:
        // Fall through.
      }
    }
    assert (inputs.get(key) == state);
  }

  public static DirectionVector getPlayerInputDirectionVector() {return playerInputDirectionVector;}


  /**
   * Add a key binding option to the map.
   * 
   * @param key
   *          String name for the binding to be added.
   */
  public void addBinding(String key)
  {
    inputs.put(key.toLowerCase(), false);

  }
  
  
  /**
   * Disable the input for the passed key String.
   * 
   * @param key
   *          Key String for the input to disable.
   */
  public void disable(String key)
  {
    inputs.put(key, false);
  }

  /**
   * Disable all input states.
   */
  public void disableAll()
  {
    for (Map.Entry<String, Boolean> entry : inputs.entrySet())
    {
      inputs.put(entry.getKey(), false);
    }
  }
  
  
}
