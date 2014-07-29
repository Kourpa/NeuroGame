package neurogame.level;


/**
 * Created by kourpa on 7/8/14.
 */
public class GravitationalMass {
  private double x, y, xpull, ypull;
  private boolean alive;

  public GravitationalMass(double x, double y, double xpull, double ypull){
    this.x = x;
    this.y = y;
    this.xpull = xpull;
    this.ypull = ypull;
    alive = true;
  }

  public boolean isAlive(){return alive;}
  public double getX(){return x;}
  public double getY(){return y;}
  public void setX(double x){this.x = x;}
  public void setY(double y){this.y = y;}
  public double getXpull(){return xpull;}
  public double getYpull(){return ypull;}
}
