package neurogame.gameplay;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Area;

import neurogame.level.World;
import neurogame.library.Library;

/**
 *
 * @author Danny Gomez
 */
public abstract class GameObject
{

  private static int globalID; // object id
  private final int id;
  private final String name; // type of object
  private double x; // starting x
  private double y; // starting y
  private double width; // width of obj
  private double height; // height of obj
  private double centerX, centerY;
  private double hitMinX, hitMaxX, hitMinY, hitMaxY;
  protected World world;
  private boolean isAlive = true; // determines if the object is active/alive
  private boolean isVisible = false; // determines if the object is on screen

  protected Player player;

  // protected Path2D hitBox = new Path2D.Double(); //it's a hit box
  private boolean empHit = false;

  // movement variables
  protected double maxSpeed = 0.01;
  protected double velD = 0.003;

  protected int health = 1;

  public GameObject(double x, double y, double width, double height, String name, World world)
  {
    this.name = name;
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.world = world;
    this.player = world.getPlayer();

    setLocation(x, y);

    id = globalID;
    globalID++;
  }

  public abstract boolean update(double deltaSec, double scrollDistance);

  /**
   * Checks if this objects is colliding with another object.
   * 
   * @param graphics
   */
  public abstract void render(Graphics2D graphics);

  public boolean collision(GameObject other)
  {

    // System.out.println("collision("+name+") " + other.active + ", " +
    // other.isVisible);
    if (other.isAlive == false)
    {
      return false;
    }
    if (other.isVisible == false)
    {
      return false;
    }

    if (hitMaxX < other.hitMinX)
    {
      return false;
    }
    if (hitMinX > other.hitMaxX)
    {
      return false;
    }
    if (hitMaxY < other.hitMinY)
    {
      return false;
    }
    if (hitMinY > other.hitMaxY)
    {
      return false;
    }
    return true;

  }

  public static int getGlobalID()
  {
    return globalID;
  }

  public int getId()
  {
    return id;
  }

  public String getName()
  {
    return name;
  }

  public double getX()
  {
    return x;
  }

  public double getY()
  {
    return y;
  }

  public double getWidth()
  {
    return width;
  }

  public double getHeight()
  {
    return height;
  }

  public void setLocation(double x, double y)
  {
    this.x = x;
    this.y = y;

    centerX = x + width / 2.0;
    centerY = y + height / 2.0;

    hitMinX = centerX - 0.75 * (width / 2.0);
    hitMaxX = centerX + 0.75 * (width / 2.0);
    hitMinY = centerY - 0.75 * (height / 2.0);
    hitMaxY = centerY + 0.75 * (height / 2.0);
  }

  public double getCenterX()
  {
    return centerX;
  }

  public double getCenterY()
  {
    return centerY;
  }

  public double getHitMinX()
  {
    return hitMinX;
  }

  public double getHitMaxX()
  {
    return hitMaxX;
  }

  public double getHitMinY()
  {
    return hitMinY;
  }

  public double getHitMaxY()
  {
    return hitMaxY;
  }

  public boolean isAlive()
  {
    return isAlive;
  }

  public void die()
  { 
    isAlive = false;
  }

  public void setEmpHit(boolean empHit)
  {
    this.empHit = empHit;
  }

  public boolean isVisible()
  {
    return isVisible;
  }

  public void setIsVisible(boolean isVisible)
  {
    this.isVisible = isVisible;
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
    EnumCollisionType collision = world.collisionWithWall(hitMinX, hitMinY);
    if (collision != EnumCollisionType.NONE) return collision;

    collision = world.collisionWithWall(hitMaxX, hitMinY);
    if (collision != EnumCollisionType.NONE) return collision;
    
    collision = world.collisionWithWall(hitMinX, hitMaxY);
    if (collision != EnumCollisionType.NONE) return collision;
    
    collision = world.collisionWithWall(hitMaxX, hitMaxY);
    return collision;
  }

  public int getHealth() { return health;}

  public void setHealth(int health) { this.health = health; }
}
