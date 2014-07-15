package neurogame.gameplay;

import java.awt.Graphics2D;
import neurogame.level.World;

/**
 *
 * @author Danny Gomez
 */
public abstract class GameObject
{
  private static int gameObjectCount;
  
  private final int gameObjectID;
  private GameObjectType type;

  private double x; 
  private double y; 
  private double centerX, centerY;
  private double hitBoxMinX, hitBoxMaxX, hitBoxMinY, hitBoxMaxY;
  protected World world;
  protected boolean isAlive = true; // determines if the object is active/alivezz
  

  public GameObject(GameObjectType type, double x, double y, World world)
  {
    this.type = type;
    this.x = x;
    this.y = y;
    this.world = world;
    
    
    hitBoxMinX =  - 0.90 * (type.getWidth() / 2.0);
    hitBoxMaxX =  + 0.90 * (type.getWidth() / 2.0);
    hitBoxMinY =  - 0.90 * (type.getHeight() / 2.0);
    hitBoxMaxY =  + 0.90 * (type.getHeight() / 2.0);

    setLocation(x, y);

    gameObjectID = gameObjectCount;
    gameObjectCount++;
  }
  
  
  public static void resetGameObjectCount() 
  { gameObjectCount = 0;
  }
  
  public int getGameObjectId() {  return gameObjectID; }
  
  
  public void overrideDefaultHitBoxInPixels(int pixelWidth, int pixelHeight, int x1, int y1, int x2, int y2)
  {
    double scaleX = type.getWidth()/(double) pixelWidth;
    double scaleY = type.getHeight()/(double) pixelHeight;
    double x0 = type.getWidth() / 2.0;
    double y0 = type.getHeight() / 2.0;
    
    hitBoxMinX = ((double)x1 * scaleX) - x0;
    hitBoxMaxX = ((double)x2 * scaleX) - x0;
    hitBoxMinY = ((double)y1 * scaleY) - y0;
    hitBoxMaxY = ((double)y2 * scaleY) - y0;
  }
  

  public abstract void update(double deltaSec, double scrollDistance);

  /**
   * Checks if this objects is colliding with another object.
   * 
   * @param graphics
   */
  public abstract void render(Graphics2D graphics);
  
  public abstract void hit(GameObject other);

  public boolean collision(GameObject other)
  {

    // System.out.println("collision("+name+") " + other.active + ", " +
    // other.isVisible);
    if (other.isAlive == false)
    {
      return false;
    }

    if (getHitMaxX() < other.getHitMinX())
    {
      return false;
    }
    if (getHitMinX() > other.getHitMaxX())
    {
      return false;
    }
    if (getHitMaxY() < other.getHitMinY())
    {
      return false;
    }
    if (getHitMinY() > other.getHitMaxY())
    {
      return false;
    }
    return true;
  }


  

  public double getX() { return x; }

  public double getY() { return y; }

  public double getWidth() { return type.getWidth();  }

  public double getHeight() { return type.getHeight(); }

  public void setLocation(double x, double y)
  {
    this.x = x;
    this.y = y;

    centerX = x + type.getWidth() / 2.0;
    centerY = y + type.getHeight() / 2.0;
  }
  
  public void move(double dx, double dy)
  {
    setLocation(x + dx, y + dy);
  }

  public double getCenterX() { return centerX;}

  public double getCenterY(){return centerY;}

  public boolean isAlive()  { return isAlive; }

  public abstract void die(boolean showDeathEffect);
  
  public double getHitMinX()
  { return centerX + hitBoxMinX;
  }

  public double getHitMaxX()
  { return centerX + hitBoxMaxX;
  }
  
  public double getHitMinY()
  { return centerY + hitBoxMinY;
  }

  public double getHitMaxY()
  { return centerY + hitBoxMaxY;
  }

  
  public GameObjectType getType() {return type;}
  public String getName() {return type.getName();}
  





  protected EnumCollisionType wallCollision()
  {
    EnumCollisionType collision = world.collisionWithWall(getHitMinX(), getHitMinY());
    if (collision != EnumCollisionType.NONE) return collision;

    collision = world.collisionWithWall(getHitMaxX(), getHitMinY());
    if (collision != EnumCollisionType.NONE) return collision;
    
    collision = world.collisionWithWall(getHitMinX(), getHitMaxY());
    if (collision != EnumCollisionType.NONE) return collision;
    
    collision = world.collisionWithWall(getHitMaxX(), getHitMaxY());
    return collision;
  }

  
  public String toString()
  {
    return type.getName() + "["+gameObjectID+"]";
  }
}
