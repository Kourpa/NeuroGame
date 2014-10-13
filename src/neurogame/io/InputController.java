package neurogame.io;


import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import neurogame.gameplay.DirectionVector;
import neurogame.io.User;
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
public class InputController implements KeyListener
{
  // private final PlayerControls controls;
  public static final String JOYSTICK_NOT_CONNECTED = null;

  private NeuroGame game;

  private boolean controllable;
  
  private Controller joystick = null;

  private static int joystickAxisX = 3;
  private static int joystickAxisY = 2;

  private static final double JOYSTICK_THRESHOLD = 0.01;
  private double joystickLastX, joystickLastY;
  private boolean ButtonPressed;

  private static boolean playerIsPressingButton = false;
  private static boolean playerPressedPause = false;

  private boolean joystickReady;

  private static DirectionVector playerInputDirectionVector = new DirectionVector();
  
  public InputController(NeuroGame game)
  {
    System.out.println("GameController() Enter");
    this.game = game;
  
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
    

  public void setControllable(boolean controllable)
  {
    this.controllable = controllable;
  }





  public static boolean isPlayerPressingButton()
  {
    return playerIsPressingButton;
  }
  
  public boolean popPlayerPressingButton()
  {
    if (playerIsPressingButton)
    { 
      playerIsPressingButton = false;
      return true;
    }
    return false;
  }

  private void updateButtonStatus()
  {
    playerIsPressingButton = false;



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




  public void updatePlayerInputDirection()
  {
    //if (!controllable) return;
      
    
    if (joystick != null)
    {
      playerInputDirectionVector.x = 0;
      playerInputDirectionVector.y = 0;
      
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
  

  public static DirectionVector getPlayerInputDirectionVector()
  {
    return playerInputDirectionVector;
  }

  public boolean popPause()
  {
    if (playerPressedPause)
    { playerPressedPause = false;
      return true;
    }
    return false;
  }
  
  @Override
  public void keyTyped(KeyEvent event)
  {
  }

  @Override
  public void keyPressed(KeyEvent event) 
  {
    int code = event.getKeyCode();
    System.out.println("InputController.keyPressed() keyTyped code= " + code);
    
    if (code == KeyEvent.VK_SPACE) playerIsPressingButton = true;
    else if (code == KeyEvent.VK_UP) playerInputDirectionVector.y = -1;
    else if (code == KeyEvent.VK_DOWN) playerInputDirectionVector.y = 1;
    else if (code == KeyEvent.VK_RIGHT) playerInputDirectionVector.x = 1;
    else if (code == KeyEvent.VK_LEFT)  playerInputDirectionVector.x = -1;
    
  }

  @Override
  public void keyReleased(KeyEvent event)
  {
    int code = event.getKeyCode();
    System.out.println("InputController.keyReleased() keyTyped code= " + code);
    
    if (code == KeyEvent.VK_SPACE) playerIsPressingButton = false;
    else if (code == KeyEvent.VK_UP) playerInputDirectionVector.y = 0;
    else if (code == KeyEvent.VK_DOWN) playerInputDirectionVector.y = 0;
    else if (code == KeyEvent.VK_RIGHT) playerInputDirectionVector.x = 0;
    else if (code == KeyEvent.VK_LEFT)  playerInputDirectionVector.x = 0;
    else if (code == KeyEvent.VK_P)  playerPressedPause = true;
  }
}
