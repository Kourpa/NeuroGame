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
  public double x; // starting x
  public double y; // starting y
  private double width; // width of obj
  private double height; // height of obj
  private double centerX, centerY;
  private double hitMinX, hitMaxX, hitMinY, hitMaxY;
  protected World world;
  private boolean active = true; // determines if the object is active/alive
  private boolean isVisible = false; // determines if the object is on screen
  
  protected Player player;

  // protected Path2D hitBox = new Path2D.Double(); //it's a hit box

  private  boolean empHit = false;
  
  // movement variables
  protected double maxSpeed = 0.01;
  protected double velD = 0.003;

  protected int health = 1;


  public GameObject(double x, double y, double width, double height,
      String name, Image image, World world)
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

  public abstract void update(long deltaTime);

  /**
   * Checks if this objects is colliding with another object.
   * 
   * @param object
   *          Object to check collision with.
   * @return True if colliding, false is not colliding
   */
  
  public abstract void render(Graphics2D graphics);
  
  public boolean collision(GameObject other)
  {

    // System.out.println("collision("+name+") " + other.active + ", " +
    // other.isVisible);
    if (other.active == false) return false;
    if (other.isVisible == false) return false;

    // Area areaThis = new Area(object.getHitBox());
    // Area areaThat = new Area(getHitBox());
    //
    // areaThis.intersect(areaThat);
    //
    // return !areaThis.isEmpty();

    if (hitMaxX < other.hitMinX) return false;
    if (hitMinX > other.hitMaxX) return false;
    if (hitMaxY < other.hitMinY) return false;
    if (hitMinY > other.hitMaxY) return false;
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

  public boolean isActive()
  {
    return active;
  }

  public void setActive(boolean state)
  {
    active = state;
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
    active = false;
  }

  public void killedBySuper()
  {
    active = false;
  }

  public void killedByBoost()
  {
    active = false;
  }

  public void hitByEmp()
  {
    empHit = true;
  }
  
  
  public void move(long deltaTime, double speed, double dx, double dy)
  {

    double targetDistance = Math.sqrt(dx*dx + dy*dy);
    if (targetDistance < 0.00001) return;
    
    double distance = speed*deltaTime;
    
    x = x + dx*distance/targetDistance;
    y = y + dy*distance/targetDistance;
  }


  // end of the movement methods-----------------------------------

  protected boolean wallCollision()
  {
    Area walls = world.getCollisionArea();

    // boolean hit = false;
    //System.out.println("walls="+walls.getBounds2D()+", " + getName() + "("+
    //    getHitMinX() + ", " + getHitMinY() + ") - (" + getHitMaxX() + ", " + getHitMaxY() + ")");

    if (walls.contains(hitMinX, hitMinY)) return true;
    if (walls.contains(hitMaxX, hitMinY)) return true;
    if (walls.contains(hitMinX, hitMaxY)) return true;
    if (walls.contains(hitMaxX, hitMaxY)) return true;

    return false;
  }


  public int getHealth()
  {
    return health;
  }

  public void setHealth(int health)
  {
    this.health = health;
  }
}
