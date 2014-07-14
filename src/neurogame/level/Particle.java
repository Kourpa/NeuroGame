package neurogame.level;


import java.awt.Color;

/**
 * Simple Pixel object used to create different types of particle effects.
 * extends Vector2f for simple vector methods.
 * @author Marcos
 */
public class Particle
{
  private double x_vel, y_vel, x, y;
  private final Color color;

  public Particle(double x, double y, Color color)
  {
    this.x = x;
    this.y = y;
    this.x_vel = 0;
    this.y_vel = 0;
    this.color = color;
  }
  
  public void update(double xpush, double ypush)
  {
    x_vel += xpush;
    y_vel += ypush;

    x += x_vel;
    y += y_vel;
  }

  public void move(double x, double y){
    this.x += x;
    this.y += y;
  }

  public Color getColor(){return color;}
  public double getX(){return x;}
  public double getY(){return y;}
}
