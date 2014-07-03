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

  private final Pixel[] pixels;
  private int scrollDistance;
  private float alpha;
  private int i = 0,  j = 0;

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
    alpha = 1;
    scrollDistance = 0;
    pixels = SpriteParticles.getPixels(type.getName());
    for(int i = 0; i < pixels.length; i++){
      pixels[i].move(Library.worldUnitToScreen(x), Library.worldUnitToScreen(y));
    }
  }

  /**
   * Update all the pixels creating the particle effect.
   * @param deltaSec
   * @param scrollDistance
   */
  @Override
  public void update(double deltaSec, double scrollDistance)
  { alpha -= .02;
    if (getX()+getWidth() < Library.leftEdgeOfWorld || alpha <= 0) die();
    Pixel p1, p2;
    this.scrollDistance = (int)scrollDistance;

    float[] xy_push;
    for (int i = 0; i < pixels.length; i++)
    {
      p1 = pixels[i];
      if(p1.getDead()) continue;
      for (int j = i + 1; j < pixels.length; j++) {
        p2 = pixels[j];
        if(p2.getDead()) continue;
        xy_push = getPush(p1, p2);

        /* divide by 1000 to slow it down a bit */
        p1.applyForces(-xy_push[0]/100, -xy_push[1]/100);
        p2.applyForces(xy_push[0]/100, xy_push[1]/100);
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
  { float x_push, y_push;

    x_push = p1.getX() - p2.getX();
    y_push = p1.getY() - p2.getY();
    double mag = Math.sqrt(x_push*x_push + y_push*y_push);

    x_push/=mag;
    y_push/=mag;

    return new float[]{x_push, y_push};
  }

  @Override
  public void render(Graphics2D graphics)
  {
    Composite oldAlpha = graphics.getComposite();
    AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
    graphics.setComposite(alphaComposite);

    for(int i = 0; i < pixels.length; i++)
    {
      if(pixels[i].getDead()) continue;
      graphics.setColor(pixels[i].getColor());
      int xx = (int)pixels[i].getX() + Library.worldPosXToScreen(scrollDistance);
      int yy = (int)pixels[i].getY();
      graphics.fillRect(xx, yy, 4, 4);
    }

    graphics.setComposite(oldAlpha);
  }

  @Override
  public void hit(GameObject other){}
}
