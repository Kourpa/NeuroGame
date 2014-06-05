package neurogame.gameplay;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;

import neurogame.level.World;
import neurogame.library.Library;

public class EnemyFollow extends Enemy
{
  static String name = "EnemyFollow";
  static Image image = Library.getSprites().get(name);
  public static final double width = 0.05;
  public static final double height = 0.05;
 

  public EnemyFollow(double x, double y, World world)
  {
    super(x, y, width, height, name, image, world);

    maxSpeed = 0.05f;
  }

  public void update(long deltaTime)
  {
    checkCollisionWithPlayer();
    checkCollisionWithWall();

    if (isActive())
    {
      double dx =  player.getCenterX() - getCenterX();
      double dy =  player.getCenterY() - getCenterY();
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
