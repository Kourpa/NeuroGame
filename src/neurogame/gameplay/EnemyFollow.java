package neurogame.gameplay;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;

import neurogame.gameplay.Enemy.EnumEnemyType;
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
    super(EnumEnemyType.STRAIGHT, x, y, width, height, name, world);

    maxSpeed = 0.05f;
  }

  public boolean update(double deltaTime, double scrollDistance)
  {
    if (getX() < Library.leftEdgeOfWorld) return false;
    checkCollisionWithPlayer();
    checkCollisionWithWall();

    if (isAlive())
    {
      double dx = player.getCenterX() - getCenterX();
      double dy = player.getCenterY() - getCenterY();
      // move(deltaTime, maxSpeed, dx, dy);
    }
    return true;
  }

  public void render(Graphics2D g)
  {
    int xx = Library.worldPosXToScreen(getX());
    int yy = Library.worldPosYToScreen(getY());
    g.drawImage(image, xx, yy, null);
  }
}
