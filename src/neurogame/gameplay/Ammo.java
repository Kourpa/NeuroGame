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

public class Ammo extends GameObject
{
  private static Image image = Library.getSprites().get(GameObjectType.AMMO.getName());
  private static Ammo currentAmmoBox;
  
  private static final double PROBABILITY_SPAWN_AMMO_PER_SEC = 0.07;
  
  public static void initGame()
  {
  }

  public Ammo(double x, double y, World world)
  {
    super(GameObjectType.AMMO, x, y, world);
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
    if ((currentAmmoBox != null) && (currentAmmoBox.isAlive())) return 0;
    

    double r = Library.RANDOM.nextDouble();
    EnumChunkType type = myChunk.getChunkType();
    if (type == EnumChunkType.FLAT) return 0;
    if (type == EnumChunkType.SQUARE) return 0;

    if (r > PROBABILITY_SPAWN_AMMO_PER_SEC * deltaTime) return 0;

    //System.out.println("probabilitySpawnCoinPerSec=" + world.getPlayer().skillProbabilitySpawnCoinPerSec);

    double rightEdgeOfScreen = Library.leftEdgeOfWorld
        + Library.getWindowAspect();
    PathVertex vertex = myChunk.getVertexRightOf(rightEdgeOfScreen);

    if (vertex == null) return 0;

    double x = vertex.getX();
   

    double y = vertex.getTop() + GameObjectType.AMMO.getHeight() / 3;

    if (Library.RANDOM.nextBoolean())
    {
      y = vertex.getBottom() - 1.3 * GameObjectType.AMMO.getHeight();
    }

    currentAmmoBox = new Ammo(x, y, world);
    world.addGameObject(currentAmmoBox);
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

  public static Ammo getCurrentAmmoBox() {return currentAmmoBox;}
}
