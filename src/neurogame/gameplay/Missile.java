package neurogame.gameplay;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;

import neurogame.level.World;
import neurogame.library.Library;

public class Missile extends GameObject
{
  private static Image image = Library.getSprites().get(GameObjectType.MISSILE.getName());
  private static Missile currentMissile; 

  
  public Missile(double x, double y, World world)
  {
    super(GameObjectType.MISSILE, x, y, world);  
    //overrideDefaultHitBoxInPixels(34, 11, -34, 0, 34, 11);
    currentMissile = this;
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
    
    if (type.isEnemy()) world.getPlayer().killedOrAvoidedEnemy(obj, true);
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
  
  public static Missile getCurrentMissile() {return currentMissile;}
  
  public void render(Graphics2D canvas)
  {
    int xx = Library.worldPosXToScreen(getX());
    int yy = Library.worldPosYToScreen(getY());
    canvas.drawImage(image, xx, yy, null);
    
    if (Library.DEBUG_SHOW_HITBOXES)
    { int x1 = Library.worldPosXToScreen(getHitMinX());
      int y1 = Library.worldPosYToScreen(getHitMinY());
      int x2 = Library.worldPosXToScreen(getHitMaxX());
      int y2 = Library.worldPosYToScreen(getHitMaxY());
      canvas.setColor(Color.RED);
      canvas.drawRect(x1,y1, x2-x1, y2-y1);
    }
  }

}
