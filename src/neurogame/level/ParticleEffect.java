package neurogame.level;

import neurogame.gameplay.GameObject;
import neurogame.gameplay.GameObjectType;
import neurogame.gameplay.Player;
import neurogame.library.Library;

import java.awt.*;

/**
 * Creates particle effects for destroyed objects using the SpriteParticles static class.
 */
public class ParticleEffect extends GameObject
{
  private final Particle[] particles;
  private GravitationalMass gmass;
  private Player player;

  private double scrollDistance;
  private float alpha;

  /**
   * Fancy particle effects
   * @param obj source for particles
   * @param x position
   * @param y position
   * @param world reference to the world
   */
  public ParticleEffect(GameObject obj, double x, double y, World world)
  {
    super(GameObjectType.PARTICLE, x, y, world);

    x = Library.worldUnitToScreen(x);
    y = Library.worldUnitToScreen(y - .025);
    double width = Library.worldUnitToScreen(obj.getWidth());
    double height = Library.worldPosYToScreen(obj.getHeight());

    gmass = new GravitationalMass(x + width / 2, y + height / 2, -.2, -.2);

    alpha = 1;
    scrollDistance = 0;
    particles = SpriteParticles.getPixels(obj.getName());
    for(int i = 0; i < particles.length; i++){
      particles[i].move(x, y);
    }
  }

  /**
   * Constructor with the player acting as the gravitational force
   */
  public ParticleEffect(GameObject obj, World world){
    super(GameObjectType.PARTICLE, obj.getX(), obj.getY(), world);

    particles = SpriteParticles.getPixels(obj.getName());

    player = world.getPlayer();
    double x = Library.worldPosXToScreen(player.getCenterX());
    double y = Library.worldPosYToScreen(player.getCenterY());

    gmass = new GravitationalMass(x, y, 1, 1);

    alpha = 1;
    scrollDistance = 0;

    double objX = Library.worldUnitToScreen(obj.getX());
    double objY = Library.worldUnitToScreen(obj.getY());
    for(int i = 0; i < particles.length; i++){
      particles[i].move(objX, objY);
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
    if (alpha <= 0 || !gmass.isAlive()) die(false);
    this.scrollDistance = scrollDistance;

    if(player != null)
    { gmass.setX(Library.worldUnitToScreen(player.getCenterX()));
      gmass.setY(Library.worldPosYToScreen(player.getCenterY()));
    }

    double xpull = gmass.getXpull();
    double ypull = gmass.getYpull();
    double dx, dy;
    for(Particle p: particles){
      dx = gmass.getX() - p.getX();
      dy = gmass.getY() - p.getY();
      double distance = Math.sqrt(dx * dx + dy * dy);
      double mag = 1/distance;

      p.update(xpull * dx * mag * Library.RANDOM.nextDouble(), ypull * dy * mag * Library.RANDOM.nextDouble());
    }
  }
  
  @Override
  public void render(Graphics2D graphics)
  { 
    boolean useAlphaComposite = false;
    Composite oldAlpha = null;
    // For some reason I (Joel) have not yet figured out, the AlphaComposite stuff is causing massive flicker.
    if (useAlphaComposite)
    { oldAlpha = graphics.getComposite();
      AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
      graphics.setComposite(alphaComposite);
    }

    for(int i = 0; i < particles.length; i++)
    {
      graphics.setColor(particles[i].getColor());
      int xx = (int)particles[i].getX() + Library.worldPosXToScreen(scrollDistance);
      int yy = (int)particles[i].getY();
      graphics.fillRect(xx, yy, 2, 2);
    }

    if (useAlphaComposite)
    { graphics.setComposite(oldAlpha);
//      graphics.setColor(Color.RED);
    }
  }

  @Override
  public void hit(GameObject other){}
  
  public void die(boolean showDeathEffect)
  { 
    isAlive = false;
  }
}
