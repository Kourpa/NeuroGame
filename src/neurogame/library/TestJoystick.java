package neurogame.library;

import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;

public class TestJoystick
{

  private static Controller joystick = null;
  private static int buttonCount;
  private static int axisX;
  private static int axisY;

  private static double lastX = 0.0;
  private static double lastY = 0.0;

  private static void update()
  {
    joystick.poll();
    for (int i = 0; i < buttonCount; i++)
    {
      if (joystick.isButtonPressed(i))
      {
        System.out.println("Pressed Button " + i);
      }
    }
    double x = joystick.getAxisValue(axisX);
    double y = joystick.getAxisValue(axisY);

    // for (int i = 0; i < joystick.getAxisCount(); i++)
    // {
    // double x = joystick.getAxisValue(i);
    // System.out.println("axis " + i + "=" + x);
    //
    // }

    if (lastX != x || lastY != y)
    {
      System.out.println("Joystick: (" + x + ", " + y + ")");
      lastX = x;
      lastY = y;
    }
  }

  public static void main(String[] argv)
  {
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
    System.out.println(count + " Controllers Found");

    for (int i = 0; i < count; i++)
    {
      Controller controller = Controllers.getController(i);
      // System.out.println(controller.getName());
      if (controller.getName().contains("Gamepad"))
      {
        joystick = controller;
        System.out.println("Gamepad found at index " + i);
        break;
      }
    }

    if (joystick == null)
    {
      System.out.println("Gamepad not found");
      System.exit(0);
    }

    buttonCount = joystick.getButtonCount();
    axisX = 0;
    axisY = 1;

    boolean running = true;
    while (running)
    {

      try
      {
        Thread.sleep(100);
      }
      catch (Exception e)
      {
      }

      update();
    }
  }
}