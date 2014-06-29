package neurogame.level;

import java.awt.*;
import java.util.ArrayList;
import neurogame.gameplay.GameObject;
import neurogame.gameplay.GameObjectType;
import neurogame.library.Library;
import neurogame.library.Vector2;
import org.lwjgl.util.vector.Vector2f;

public class Particles extends GameObject
{

  private final ArrayList<Pixel> pixels;
  private double startTime;
  private long maxTime = 500;
  private int scrollDistance;

  public Particles(GameObjectType type, double x, double y, double x_vel, double y_vel, World world, double startTime)
  {
    super(GameObjectType.PARTICLE, x, y, world);
    scrollDistance = 0;
    this.startTime = startTime;
    pixels = SpriteParticles.getPixels(type.getName());
    pixels.forEach((p) -> p.move(Library.worldUnitToScreen(x), Library.worldUnitToScreen(y)));
  }

  @Override
  public void update(double deltaSec, double scrollDistance)
  { if (getX()+getWidth() < Library.leftEdgeOfWorld) die();
    Pixel p1, p2;
    this.scrollDistance = (int)scrollDistance;
    float[] xy_push;
    for (int i = 0; i < pixels.size(); i++) {
      p1 = pixels.get(i);
      if(p1.dead) die();
      for (int j = i + 1; j < pixels.size(); j++) {
        p2 = pixels.get(j);
        xy_push = getPush(p1, p2);

        p1.applyForces(-xy_push[0], -xy_push[1]);
        p2.applyForces(xy_push[0], xy_push[1]);
      }
      p1.update();
    }
  }

  private float[] getPush(Pixel p1, Pixel p2)
  { double x_push, y_push;
    double distance;
    double angle;
    
    Pixel p3 = new Pixel(p1.getX() - p2.getX(), p1.getY() - p2.getY(), null);
    p3.normalise();

    x_push = p3.getX();
    y_push = p3.getY();

    return new float[]{(float)x_push, (float)y_push};
  }

  @Override
  public void render(Graphics2D graphics)
  { pixels.forEach(p->{
      graphics.setColor(p.getColor());
      int xx = (int)p.getX() + Library.worldPosXToScreen(scrollDistance);
      int yy = (int)p.getY();
      graphics.fillRect(xx, yy, 4, 4);
    });
  }

  @Override
  public void hit(GameObject other)
  {
  }
}
