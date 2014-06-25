package neurogame.gameplay;

import java.awt.Graphics2D;
import neurogame.level.World;

/**
 *
 * @author Danny Gomez
 */
public abstract class GameObject
{
  private static int globalID; // object id
  
  private final int id;
  private GameObjectType type;

  private double x; // starting x
  private double y; // starting y
  private double centerX, centerY;
  private double hitBoxMinX, hitBoxMaxX, hitBoxMinY, hitBoxMaxY;
  protected World world;
  private boolean isAlive = true; // determines if the object is active/alive

  protected Player player;

  // protected Path2D hitBox = new Path2D.Double(); //it's a hit box
  private boolean empHit = false;

  // movement variables
  protected double maxSpeed = 0.01;
  protected double velD = 0.003;

  protected int health = 1;

  public GameObject(GameObjectType type, double x, double y, World world)
  {
    this.type = type;
    this.x = x;
    this.y = y;
    this.world = world;
    this.player = world.getPlayer();
    
    
    hitBoxMinX =  - 0.80 * (type.getWidth() / 2.0);
    hitBoxMaxX =  + 0.80 * (type.getWidth() / 2.0);
    hitBoxMinY =  - 0.80 * (type.getHeight() / 2.0);
    hitBoxMaxY =  + 0.80 * (type.getHeight() / 2.0);

    setLocation(x, y);

    id = globalID;
    globalID++;
  }
  
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

  public static int getGlobalID() { return globalID;  }

  public int getId() {  return id; }

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

  public double getCenterX() { return centerX;}

  public double getCenterY(){return centerY;}

  public boolean isAlive()  { return isAlive; }
  
  public void die() {   isAlive = false; }
  
  
  
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
  

  public void setEmpHit(boolean empHit)
  {
    this.empHit = empHit;
  }




  public void killedByLaser()
  {
    isAlive = false;
    // Library.log(name + " killed by laser", world.getRisk());
  }

  public void killedBySuper()
  {
    isAlive = false;
    // Library.log(name + " killed by super", world.getRisk());
  }

  public void killedByBoost()
  {
    isAlive = false;
    // Library.log(name + " killed by boost", world.getRisk());
  }

  public void hitByEmp()
  {
    empHit = true;
    // Library.log(name + " hit by emp", world.getRisk());
  }

  public void move(double dx, double dy)
  {
    setLocation(x + dx, y + dy);
  }

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

  public int getHealth() { return health;}

  public void setHealth(int health) { this.health = health; }
}
