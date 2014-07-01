package neurogame.level;

import java.awt.*;
import java.util.ArrayList;
import neurogame.gameplay.GameObject;
import neurogame.gameplay.GameObjectType;
import neurogame.library.Library;
import neurogame.library.Vector2;
import org.lwjgl.util.vector.Vector2f;

/**
 * Creates particle effects for destroyed objects using the SpriteParticles static class.
 */
public class Particles extends GameObject
{

  private final ArrayList<Pixel> pixels;
  private int scrollDistance;

  /**
   * Fancy particle effects
   * @param type source for particles
   * @param x position
   * @param y position
   * @param world reference to the world
   */
  public Particles(GameObjectType type, double x, double y, World world)
  {
    super(GameObjectType.PARTICLE, x, y, world);
    scrollDistance = 0;
    pixels = SpriteParticles.getPixels(type.getName());
    for (Pixel p : pixels) 
    {
    
//      p.applyForces(Library.worldPosXToScreen(x - lastX)/100, 0);//Library.worldUnitToScreen(y - lastY)/100);
      p.move(Library.worldUnitToScreen(x), Library.worldUnitToScreen(y));
    }
  }

  /**
   * Update all the pixels creating the particle effect.
   * @param deltaSec
   * @param scrollDistance
   */
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

        /* divide by 1000 to slow it down a bit */
        p1.applyForces(-xy_push[0]/1000, -xy_push[1]/1000);
        p2.applyForces(xy_push[0]/1000, xy_push[1]/1000);
      }
      p1.update();
    }
  }

  /**
   * get the amount p1 pushes onto p2 and vice versa
   * @param p1
   * @param p2
   * @return
   */
  private float[] getPush(Pixel p1, Pixel p2)
  { double x_push, y_push;

    Pixel p3 = new Pixel(p1.getX() - p2.getX(), p1.getY() - p2.getY(), null);
    p3.normalise();

    x_push = p3.getX();
    y_push = p3.getY();

    return new float[]{(float)x_push, (float)y_push};
  }

  @Override
  public void render(Graphics2D graphics)
  { 
    for (Pixel p : pixels) 
    {
      graphics.setColor(p.getColor());
      int xx = (int)p.getX() + Library.worldPosXToScreen(scrollDistance);
      int yy = (int)p.getY();
      graphics.fillRect(xx, yy, 4, 4);
    }
  }

  @Override
  public void hit(GameObject other){}
}
