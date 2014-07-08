package neurogame.level;

import neurogame.gameplay.GameObjectType;
import neurogame.library.Library;

/**
 * Created by kourpa on 7/8/14.
 */
public class GravitationalMass {
  private GameObjectType type;
  private double x, y, xpull, ypull;
  private boolean alive;

  public GravitationalMass(GameObjectType type, double x, double y, double xpull, double ypull){
    this.type = type;
    this.x = x;
    this.y = y;
    this.xpull = xpull;
    this.ypull = ypull;
    alive = true;
  }

  public void update(double deltaSec, double scrollDistance){
    if(x < Library.leftEdgeOfWorld) alive = false;
    x += scrollDistance - deltaSec * .6 * scrollDistance;
  }

  public boolean isAlive(){return alive;}
  public double getX(){return x;}
  public double getY(){return y;}
  public double getXpull(){return xpull;}
  public double getYpull(){return ypull;}
}
