package neurogame.library;

public class Vector2
{
  public double x, y;
  public static final double EPSILON = 0.000001; 
  
  public Vector2() 
  { x=0.0;
    y=0.0;
  }
  
  public Vector2(double x, double y)
  { this.x = x;
    this.y = y;
  }
  
  public Vector2(Vector2 vector)
  { x = vector.x;
    y = vector.y;
  }
  
  public void setMaxMagnitude(double maxMagnitude)
  {
    if (maxMagnitude < 0)
    { throw new IllegalArgumentException("setMaxMagnitude("+maxMagnitude+") value must be >= 0");
    }
    
    double magnitude = Math.sqrt(x*x + y*y);
    
    if (magnitude < maxMagnitude) return;
    
    if (maxMagnitude < EPSILON)
    { x = 0.0;
      y = 0.0;
      return;
    }
    
    x = (x / magnitude) * maxMagnitude;
    y = (y / magnitude) * maxMagnitude;
  }
}
