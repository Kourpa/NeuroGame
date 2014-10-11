package neurogame.io;


import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import neurogame.gameplay.DirectionVector;
import neurogame.io.User;
import neurogame.library.KeyBinds;
import neurogame.main.NeuroGame;

import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;

/**
 * The main game controller for NeuroGame.
 *
 * @author Ramon A. Lovato
 * @team Danny Gomez
 * @team Marcos Lemus
 */
public class InputController
{
  // private final PlayerControls controls;
  public static final String JOYSTICK_NOT_CONNECTED = null;

  private final KeyBinds keyBinds;
  private final Map<String, Boolean> inputs;
  private final Map<String, Boolean> previousInputs;

  private NeuroGame game;

  private boolean controllable;
  
  private Controller joystick = null;

  private static int joystickAxisX = 3;
  private static int joystickAxisY = 2;

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
  public InputController(NeuroGame game)
  {
    System.out.println("GameController() Enter");
    this.game = game;

    // controls = new PlayerControls();

    inputs = new HashMap<String, Boolean>();
    inputs.put("up", false);
    inputs.put("down", false);
    inputs.put("left", false);
    inputs.put("right", false);
    inputs.put("space", false);
    inputs.put("escape", false);
    inputs.put("enter", false);
    inputs.put("pause", false);

    keyBinds = new KeyBinds((JComponent) game.getContentPane(), this);

    controllable = false;



    // //////////////
    addBinding("sound");
    keyBinds.addBinding(KeyEvent.VK_F1, "sound");
    // keyBinds.addBinding(KeyEvent.VK_SPACE, "shoot");

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
  }
  
  public void setupJoystick(User user)
  {
    if ((user == null) || (user.getController() == null))
    { joystick = null;
      return;
    }
    
    joystickReady = false;
    
    try
    {
      Controllers.create();
    
    

    int count = Controllers.getControllerCount();
    
    for (int i = 0; i < count; i++)
    {
      Controller controller = Controllers.getController(i);
      if (controller.getName().equals(user.getController()))
      {
        joystick = controller;
        System.out.println("Gamepad [" + user.getController() + "] found at index " + i+ "  Testing x and y axis: "+joystickAxisX +", " + joystickAxisY);

        joystick.poll();

        joystickLastX = joystick.getAxisValue(joystickAxisX);
        joystickLastY = joystick.getAxisValue(joystickAxisY);

        return;
      }
    }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      System.out.println("***ERROR***: GameController.setupJoystick("+user+")");
    }
    
  }
    

  public Map<String, Boolean> getInputs()
  {
    return inputs;
  }

  public void setControllable(boolean controllable)
  {
    this.controllable = controllable;
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

    
    updatePlayerInputDirection();
  }

  public static boolean isPlayerPressingButton()
  {
    return playerIsPressingButton;
  }

  private void updateButtonStatus()
  {
    playerIsPressingButton = false;

    if (inputs.get("space"))
    {
      playerIsPressingButton = true;
      return;
    }

    if (joystick != null)
    {
      for (int i = 0; i < 6; i++)
      {
        if (joystick.isButtonPressed(i))
        {
          playerIsPressingButton = true;
          return;
        }
      }
    }
  }

//  /**
//   * Handler for keyboard input when you die
//   */
  public void gameOverKeyHandler()
  {
    boolean ButtonCheck = false;

    if (inputs.get("space"))
    {
      game.showHighScores();
    }

    if (joystick != null)
    {
      joystick.poll();

      for (int i = 0; i < 5; i++)
      {
        if (joystick.isButtonPressed(i))
        {
          ButtonCheck = true;
        }
      }

      if ((ButtonCheck == false) && (ButtonPressed))
      {
        ButtonPressed = false;
        game.showHighScores();
      }
      else if (ButtonCheck == true)
      {
        ButtonPressed = true;
      }
    }
  }



  public void updatePlayerInputDirection()
  {
    boolean n = false;
    boolean s = false;
    boolean w = false;
    boolean e = false;

    playerInputDirectionVector.x = 0;
    playerInputDirectionVector.y = 0;
    
    
    
    //if (!controllable) return;
      
    
    if (joystick == null)
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
      double stickX = joystick.getAxisValue(joystickAxisX);
      double stickY = joystick.getAxisValue(joystickAxisY);
      
      //System.out.println("TitleScreen.updateJoy()=(" + stickX + ", "+stickY+")");

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
    //System.out.println("InputController.updatePlayerInputDirection joystick=(" + playerInputDirectionVector.x +", "+playerInputDirectionVector.y+")");
    
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

  public static DirectionVector getPlayerInputDirectionVector()
  {
    return playerInputDirectionVector;
  }

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
