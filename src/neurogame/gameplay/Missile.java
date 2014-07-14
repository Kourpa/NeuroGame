package neurogame.gameplay;

import java.awt.Graphics2D;
import java.awt.Image;

import neurogame.level.World;
import neurogame.library.Library;

public class Missile extends GameObject
{
  private static Image image = Library.getSprites().get(GameObjectType.MISSILE.getName());

  
  public Missile(double x, double y, World world)
  {
    super(GameObjectType.MISSILE, x, y, world);    
  }
  

  
  public void die(boolean showDeathEffect)
  { 
    if (isAlive())
    { isAlive = false;
    }
  }
  
  

  
  public void update(double deltaSec, double scrollDistance)
  {
    if (!Library.isOnScreen(getX(), getY())) die(false);
    
    else if (checkCollisionWithWall()) die(true);

    if (!isAlive()) return;

    double maxDistanceChange = GameObjectType.MISSILE.getMaxSpeed() * deltaSec;

    move(maxDistanceChange, 0);
    //System.out.println("Missile Update: (" + getX() + ", " + getY() + ")" );
    
  }
  
  
  public void hit(GameObject obj)
  { 
    GameObjectType type = obj.getType();
    
    if (type.isEnemy()) world.getPlayer().killedOrAvoidedEnemy(obj);
    die(true);
  }
    


  
 

  private boolean checkCollisionWithWall()
  {
    if (!isAlive()) return false;

    if (wallCollision() != EnumCollisionType.NONE)
    {
      return true;
    }
    return false;
  }
  
  
  
  public void render(Graphics2D g)
  {
    int xx = Library.worldPosXToScreen(getX());
    int yy = Library.worldPosYToScreen(getY());
    g.drawImage(image, xx, yy, null);
  }

}
