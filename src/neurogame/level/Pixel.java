package neurogame.level;

import neurogame.library.Library;
import org.lwjgl.util.vector.Vector2f;

import java.awt.Color;

/**
 * Simple Pixel object used to create different types of particle effects.
 * extends Vector2f for simple vector methods.
 * @author Marcos
 */
public class Pixel extends Vector2f
{
  private float x_vel, y_vel, x_force, y_force;
  private final Color color;
  private boolean dead;

  public Pixel(float x, float y, Color color)
  { super(x, y);
    this.x_force = 0;
    this.y_force = 0;
    this.x_vel = 0;
    this.y_vel = 0;
    this.color = color;
    dead = false;
  }
  
  public void update()
  {
    x_vel += x_force;
    y_vel += y_force;

    x += x_vel;
    y += y_vel;
    
    x_force = 0;
    y_force = 0;
    x_vel /= 10;
    y_vel /= 10;

    if(Library.RANDOM.nextInt(10) == 0) dead = true;
  }

  public void move(double x, double y){
    this.x += x;
    this.y += y;
  }

  public void applyForces(double x_force, double y_force)
  { this.x_force -= x_force;
    this.y_force -= y_force;
  }
  
  public Color getColor(){return color;}
  public boolean getDead(){return dead;}
}
