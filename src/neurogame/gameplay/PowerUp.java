/**
 * NeuroGame.
 * CS 351, Project 3
 * 
 * Team members:
 * Ramon A. Lovato
 * Danny Gomez
 * Marcos Lemus
 */

package neurogame.gameplay;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Path2D;

import neurogame.level.World;
import neurogame.library.Library;

/**
 * A PowerUp class for NeuroGame.
 * 
 * @author Ramon A. Lovato
 * @team Danny Gomez
 * @team Marcos Lemus
 */

public class PowerUp extends GameObject
{
  private static Image image = Library.getSprites().get(GameObjectType.POWER_UP.getName());

  public PowerUp(GameObjectType type, double x, double y, World world)
  {
    super(type, x, y, world);
  }

  /**
   * Override of GameObject's update - updates object state every frame.
   */
  @Override
  public void update(double deltaTime, double scrollDistance)
  {
    if (getX()+getWidth() < Library.leftEdgeOfWorld) die(false);
    
    if (!isAlive()) return;
    

  }
  
  public void hit(GameObject obj)
  { die(false);
  }


  public void die(boolean showDeathEffect)
  { 
    isAlive = false;
  }


  /**
   * Pick up the PowerUp - links it to the Player and stops drawing it in the
   * world.
   */
  public void pickUp()
  {
    // If the PowerUp is inactive, it was already picked up, so ignore it.
    if (isAlive())
    {
      player.addMissileCount(10);
      die(true);
    }


  }

  public void render(Graphics2D g)
  {
    int xx = Library.worldPosXToScreen(getX());
    int yy = Library.worldPosYToScreen(getY());
    g.drawImage(image, xx, yy, null);
  }


}
