package neurogame.gameplay;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;

import neurogame.level.World;
import neurogame.library.Library;

public class EnemyStraight extends Enemy
{
  public static final double width = 0.05;
  public static final double height = 0.05;
  
  private static String name = "EnemyStraight";
  private static Image image = Library.getSprites().get(name);
  
  private static double startY;

  public EnemyStraight(double x, double y, World world)
  {
    super(x, y, width, height, name, image, world);

    startY = y;
    maxSpeed = 0.2f;
  }

  public void update(long deltaTime)
  {
    checkCollisionWithPlayer();
    checkCollisionWithWall();

    if (isActive())
    {
      double dx =  maxSpeed;
      double dy =  0;
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
