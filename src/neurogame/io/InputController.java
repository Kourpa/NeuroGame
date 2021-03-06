package neurogame.io;

import neurogame.gameplay.DirectionVector;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

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
  public static final int NO_KEY_PRESSED = -1;

  private Controller joystick = null;
  private int joystickAxisX, joystickAxisY;

  private static final double JOYSTICK_THRESHOLD = 0.01;

  private double joystickLastX, joystickLastY;

  private boolean triggerPressed;
  private boolean triggerReleased = true;
  private boolean spacebarPressed;
  private boolean spacebarReleased = true;
  private boolean escPressed = false;
  private boolean joystickReturnedToCenter;

  private int keyCode;

  private boolean joystickReady;

  private DirectionVector playerInputDirectionVector = new DirectionVector();

  public InputController()
  {
    System.out.println("GameController() Enter");

  }

  public void initGame()
  {
    triggerPressed = false;
    spacebarPressed = false;
    keyCode = NO_KEY_PRESSED;
    joystickReturnedToCenter = false;
  }

  public void setupJoystick(User user)
  {

    if ((user == null) || (user.getController() == null))
    {
      joystick = null;
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
          joystickAxisX = user.getControllerXAxis();
          joystickAxisY = user.getControllerYAxis();
          System.out.println("Gamepad [" + user.getController() + "] found at index " + i + "  Testing x and y axis: "
              + joystickAxisX + ", " + joystickAxisY);

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
      System.out.println("***ERROR***: GameController.setupJoystick(" + user + ")");
    }

  }

  public boolean isPlayerPressingButton()
  {
    return triggerPressed;
  }

  public boolean isPlayerPressingESC()
  {
    // This function wasn't working correctly.
    // I added this in attempt to use it.
    if (escPressed)
    {
      escPressed = false;
      return true;
    }
    else return false;
  }

  private void updateButtonStatus()
  {
    triggerPressed = false;
    if (spacebarPressed)
    {
      if (spacebarReleased)
      {
        spacebarPressed = false;
        triggerPressed = true;
        triggerReleased = false;
        spacebarReleased = false;
        escPressed = false;
      }
      return;
    }

    if (joystick != null)
    {
      for (int i = 0; i < 6; i++)
      {
        if (joystick.isButtonPressed(i))
        {
          //System.out.println("InputController.updateButtonStatus() triggerReleased="+triggerReleased);
          if (triggerReleased)
          {
            triggerPressed = true;
            triggerReleased = false;
            keyCode = KeyEvent.VK_ENTER;
          }
          else if (keyCode == KeyEvent.VK_ENTER)
          { keyCode = NO_KEY_PRESSED;
          }
          return;
        }
      }
    }
    triggerReleased = true;
  }

  public int updatePlayerInput()
  {
    updateButtonStatus();

    if (joystick != null)
    {
      playerInputDirectionVector.x = 0;
      playerInputDirectionVector.y = 0;

      joystick.poll();
      double stickX = joystick.getAxisValue(joystickAxisX);
      double stickY = joystick.getAxisValue(joystickAxisY);

      // System.out.println("TitleScreen.updateJoy()=(" + stickX +
      // ", "+stickY+")");

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

        if ((joystickReturnedToCenter) && (keyCode == NO_KEY_PRESSED))
        {
          if (stickY > 0.75)
          {
            keyCode = KeyEvent.VK_DOWN;
            joystickReturnedToCenter = false;
          }
          else if (stickY < -0.75)
          {
            keyCode = KeyEvent.VK_UP;
            joystickReturnedToCenter = false;
          }
          else if (stickX < -0.75)
          {
            keyCode = KeyEvent.VK_LEFT;
            joystickReturnedToCenter = false;
          }
          else if (stickX > 0.75)
          {
            keyCode = KeyEvent.VK_RIGHT;
            joystickReturnedToCenter = false;
          }
        }
        else
        {
          if ((Math.abs(stickY) < 0.25) && (Math.abs(stickX) < 0.25))
          {
            joystickReturnedToCenter = true;
          }
        }
      }
    }
    // System.out.println("InputController.updatePlayerInputDirection joystick=("
    // + playerInputDirectionVector.x +", "+playerInputDirectionVector.y+")");

    int code = keyCode;
    keyCode = NO_KEY_PRESSED;
    return code;
  }

  public DirectionVector getPlayerInputDirectionVector()
  {
    return playerInputDirectionVector;
  }

  @Override
  public void keyTyped(KeyEvent event)
  {}

  @Override
  public void keyPressed(KeyEvent event)
  {
    keyCode = event.getKeyCode();

    // System.out.println("InputController.keyPressed() keyTyped code= " +
    // code);

    if (keyCode == KeyEvent.VK_SPACE || keyCode == KeyEvent.VK_ENTER)
    {
      if (spacebarReleased) spacebarPressed = true;
    }
    else if (keyCode == KeyEvent.VK_ESCAPE)
    {
      escPressed = true;
    }
    else if (keyCode == KeyEvent.VK_UP) playerInputDirectionVector.y = -1;
    else if (keyCode == KeyEvent.VK_DOWN) playerInputDirectionVector.y = 1;
    else if (keyCode == KeyEvent.VK_RIGHT) playerInputDirectionVector.x = 1;
    else if (keyCode == KeyEvent.VK_LEFT) playerInputDirectionVector.x = -1;
  }

  @Override
  public void keyReleased(KeyEvent event)
  {
    int code = event.getKeyCode();
    // System.out.println("InputController.keyReleased() keyTyped code= " +
    // code);

    if (code == KeyEvent.VK_SPACE || code == KeyEvent.VK_ENTER) spacebarReleased = true;
    else if (code == KeyEvent.VK_UP)
    {
      if (playerInputDirectionVector.y < 0) playerInputDirectionVector.y = 0;
    }
    else if (code == KeyEvent.VK_DOWN)
    {
      if (playerInputDirectionVector.y > 0) playerInputDirectionVector.y = 0;
    }
    else if (code == KeyEvent.VK_RIGHT)
    {
      if (playerInputDirectionVector.x > 0) playerInputDirectionVector.x = 0;
    }
    else if (code == KeyEvent.VK_LEFT)
    {
      if (playerInputDirectionVector.x < 0) playerInputDirectionVector.x = 0;
    }
  }
}
