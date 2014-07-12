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
import java.util.List;

import neurogame.level.Chunk;
import neurogame.level.EnumChunkType;
import neurogame.level.PathVertex;
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
  private static final double MIN_SECONDS_BETWEEN_SPAWNS = 10;
  private static double spawnCoolDownRemaining;
  
  public static void initGame()
  {
    spawnCoolDownRemaining = MIN_SECONDS_BETWEEN_SPAWNS;
  }

  public PowerUp(double x, double y, World world)
  {
    super(GameObjectType.POWER_UP, x, y, world);
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

  
  public static int spawn(Chunk myChunk, World world, double deltaTime)
  {
    if (spawnCoolDownRemaining > 0) 
    { spawnCoolDownRemaining -= deltaTime;
      return 0;
    }

    double r = Library.RANDOM.nextDouble();
    EnumChunkType type = myChunk.getChunkType();
    if (type == EnumChunkType.FLAT) return 0;

    if (r > world.getPlayer().skillProbabilitySpawnPowerUpPerSec * deltaTime) return 0;

    //System.out.println("probabilitySpawnCoinPerSec=" + world.getPlayer().skillProbabilitySpawnCoinPerSec);

    double rightEdgeOfScreen = Library.leftEdgeOfWorld
        + Library.getWindowAspect();
    PathVertex vertex = myChunk.getVertexRightOf(rightEdgeOfScreen);

    if (vertex == null) return 0;

    double x = vertex.getX();
   
    spawnCoolDownRemaining = MIN_SECONDS_BETWEEN_SPAWNS;

    double y = vertex.getTopY() + GameObjectType.POWER_UP.getHeight() / 3;

    if (Library.RANDOM.nextBoolean())
    {
      y = vertex.getBottomY() - 1.3 * GameObjectType.POWER_UP.getHeight();
    }

    PowerUp ammoBox = new PowerUp(x, y, world);
    world.addGameObject(ammoBox);
    return 1;

  }

  
  

  public void die(boolean showDeathEffect)
  { 
    isAlive = false;
  }


  public void render(Graphics2D g)
  {
    int xx = Library.worldPosXToScreen(getX());
    int yy = Library.worldPosYToScreen(getY());
    g.drawImage(image, xx, yy, null);
  }


}
