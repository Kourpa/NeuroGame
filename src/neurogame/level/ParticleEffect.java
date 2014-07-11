package neurogame.level;

import java.awt.*;

import neurogame.gameplay.GameObject;
import neurogame.gameplay.GameObjectType;
import neurogame.library.Library;

/**
 * Creates particle effects for destroyed objects using the SpriteParticles static class.
 */
public class ParticleEffect extends GameObject
{

  private final Particle[] particles;
  private GravitationalMass gmass;
  private double scrollDistance;
  private float alpha;

  /**
   * Fancy particle effects
   * @param type source for particles
   * @param x position
   * @param y position
   * @param world reference to the world
   */
  public ParticleEffect(GameObjectType type, double x, double y, World world)
  {
    super(GameObjectType.PARTICLE, x, y, world);

    x = Library.worldUnitToScreen(x);
    y = Library.worldPosYToScreen(y);
    double width = Library.worldUnitToScreen(type.getWidth());
    double height = Library.worldUnitToScreen(type.getHeight());

    gmass = new GravitationalMass(type, x + width/2, y + height/2, -.01, -.01);
    alpha = 1;
    scrollDistance = 0;
    particles = SpriteParticles.getPixels(type.getName());
    for(int i = 0; i < particles.length; i++){
      particles[i].move(x, y);
    }
  }

  /**
   * Update all the pixels creating the particle effect.
   * @param deltaSec
   * @param scrollDistance
   */
  @Override
  public void update(double deltaSec, double scrollDistance)
  { alpha -= .01;
    if (alpha <= 0 || !gmass.isAlive()) die(false);
    this.scrollDistance = scrollDistance;

//    gmass.update(deltaSec, scrollDistance);

    double xpull = gmass.getXpull();
    double ypull = gmass.getYpull();
    double dx, dy, mag;
    for(Particle p: particles){
      dx = gmass.getX() - p.getX();
      dy = gmass.getY() - p.getY();

      p.update(xpull * dx, ypull * dy);
    }
  }

  @Override
  public void render(Graphics2D graphics)
  {
    Composite oldAlpha = graphics.getComposite();
    AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
    graphics.setComposite(alphaComposite);

    for(int i = 0; i < particles.length; i++)
    {
      graphics.setColor(particles[i].getColor());
      int xx = (int)particles[i].getX() + Library.worldPosXToScreen(scrollDistance);
      int yy = (int)particles[i].getY();
      graphics.fillRect(xx, yy, 4, 4);
    }

    graphics.setComposite(oldAlpha);
    graphics.setColor(Color.RED);
  }

  @Override
  public void hit(GameObject other){}
  
  public void die(boolean showDeathEffect)
  { 
    isAlive = false;
  }
}
