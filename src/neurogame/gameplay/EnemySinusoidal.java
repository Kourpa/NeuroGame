package neurogame.gameplay;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;

import neurogame.level.World;
import neurogame.library.Library;

public class EnemySinusoidal extends Enemy
{
  public static final double width = 0.05;
  public static final double height = 0.05;
  
  private static String name = "EnemySinusoidal";
  private static Image image = Library.getSprites().get(name);


  private long time = 0;
  

  public EnemySinusoidal(double x, double y, World world)
  {
    super(x, y, width, height, name, image, world);

    maxSpeed = 0.02;
  }


  public void update(long deltaTime)
  {
    checkCollisionWithPlayer();
    checkCollisionWithWall();

    if (isActive())
    {
      time += deltaTime;
      
      double dx = -Math.abs(Math.cos(time/500.0));
      double dy =  Math.sin(time/500.0);
      move(deltaTime, maxSpeed, dx, dy);
    }
  }

  public void render(Graphics2D g)
  {
    int xx = Library.worldToScreen(getX() - world.getDeltaX());
    int yy = Library.worldToScreen(getY());
    g.drawImage(image, xx, yy, null);
  }
}
