package neurogame.level;

import org.lwjgl.util.vector.Vector2f;

import java.awt.Color;

/**
 *
 * @author kourpa
 */
public class Pixel extends Vector2f
{
  private float x_vel, y_vel, x_force, y_force;
  private final Color color;
  private int alpha = 255;
  public boolean dead = false;

  public Pixel(float x, float y, Color color)
  { super(x, y);
    this.x_force = 0;
    this.y_force = 0;
    this.x_vel = 0;
    this.y_vel = 0;
    this.color = color;
  }
  
  public void update()
  { x_vel += x_force;
    y_vel += y_force;

    x += x_vel;
    y += y_vel;
    
    x_force = 0;
    y_force = 0;
    x_vel /= 100;
    y_vel /= 100;
  }

  public void move(double x, double y){
    this.x += x;
    this.y += y;
  }

  public void applyForces(double x_force, double y_force)
  { this.x_force -= x_force/100;
    this.y_force -= y_force/100;
  }
  
  public Color getColor()
  { if(alpha > 10) alpha-=7;
    else dead = true;
    return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
  }
}
