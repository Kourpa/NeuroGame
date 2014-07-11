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
import java.awt.image.BufferedImage;
import java.util.List;

import neurogame.level.Chunk;
import neurogame.level.EnumChunkType;
import neurogame.level.PathVertex;
import neurogame.level.World;
import neurogame.library.Library;

/**
 * A Coin object for NeuroGame.
 * 
 * @author Ramon A. Lovato
 * @team Danny Gomez
 * @team Marcos Lemus
 */
public class Coin extends GameObject
{
  private static final BufferedImage image = Library.getSprites().get(GameObjectType.COIN.getName());
  private static final int spriteWidth = 64;
  private static final int spriteHeight = 64;
  private static final int spriteSheetHeight = 2560;

  public static final double MIN_PROBALITY_SPAWN_PER_SEC = 0.2;
  public static final double MAX_PROBALITY_SPAWN_PER_SEC = 0.9;

  private static double lastCoinSpawnX;

  private int frameCounter;

  private int spriteY;

  private static int totalCount = 0;
  private int id;

  /**
   * Instantiate a new Coin at the specified coordinates.
   * 
   * @param x
   *          X-coordinate at which to place the Coin.
   * @param y
   *          Y-coordinate at which to place the coin.
   * @param world
   *          World in which to place the coin.
   */
  public Coin(double x, double y, World world)
  {
    super(GameObjectType.COIN, x, y, world);
    spriteY = 0;
    totalCount++;
    id = totalCount;
    lastCoinSpawnX = x;
  }
  
  public static void initGame()
  {
    lastCoinSpawnX = 0;
  }

  /**
   * Override of GameObject's update - advances the frameCounter and which
   * sprite is being clipped from the sheet.
   */

  public void update(double deltaTime, double scrollDistance)
  {
    // System.out.println("coin["+id+"]: ("+getX() + ", " + getY() +
    // ") left world edge="+Library.leftEdgeOfWorld);

    if (getX()+getWidth() < Library.leftEdgeOfWorld) die(false);
    
    if (!isAlive()) return;
    

    // Animation.
    frameCounter++;
  }
  
  public void die(boolean showDeathEffect)
  { 
    isAlive = false;
  }
  
  public void hit(GameObject obj)
  { die(true);
  }

  public void render(Graphics2D g)
  {
    if (frameCounter % 2 == 0)
    { spriteY = (spriteY + spriteHeight) % spriteSheetHeight;
    }
    

    int xx = Library.worldPosXToScreen(getX());
    int yy = Library.worldPosYToScreen(getY());
    //Image curImage = image.getSubimage(0, spriteY, spriteWidth, spriteHeight);
    //int dstx1, int dsty1, int dstx2, int dsty2,
    //int srcx1, int srcy1, int srcx2, int srcy2,
    g.drawImage(image, xx, yy, xx+spriteWidth, yy+spriteHeight, 
                       0, spriteY, spriteWidth, spriteY+spriteHeight, null);
  }

  public static int spawn(Chunk myChunk, World world, List<GameObject> gameObjects, double deltaTime)
  {

    double r = Library.RANDOM.nextDouble();
    EnumChunkType type = myChunk.getChunkType();
    if (type == EnumChunkType.FLAT) return 0;

    if (r > world.getPlayer().skillProbabilitySpawnCoinPerSec * deltaTime) return 0;

    //System.out.println("probabilitySpawnCoinPerSec=" + world.getPlayer().skillProbabilitySpawnCoinPerSec);

    double rightEdgeOfScreen = Library.leftEdgeOfWorld
        + Library.getWindowAspect();
    PathVertex vertex = myChunk.getVertexRightOf(rightEdgeOfScreen);

    if (vertex == null) return 0;

    double x = vertex.getX();
    if (x < lastCoinSpawnX + 4 * GameObjectType.COIN.getWidth()) return 0;

    

    int targetSpawnCount = Library.RANDOM.nextInt(5) + 1;
    int numCoinsSpawned = 0;

    double y = vertex.getTopY() + GameObjectType.COIN.getHeight() / 3;
    double direction = 1.0;

    if (Library.RANDOM.nextBoolean())
    {
      y = vertex.getBottomY() - 1.3 * GameObjectType.COIN.getHeight();
      direction = -1.0;
    }

    for (int i = 0; i < targetSpawnCount; i++)
    {
      Coin myCoin = new Coin(x, y, world);
      if (myCoin.wallCollision() != EnumCollisionType.NONE) return numCoinsSpawned;
      gameObjects.add(myCoin);
      numCoinsSpawned++;

      if (Library.RANDOM.nextDouble() > .75) x += GameObjectType.COIN.getWidth()
          * (Library.RANDOM.nextDouble() * 2.0);
      y += direction * GameObjectType.COIN.getHeight() * (1.0 + Library.RANDOM.nextDouble() / 2);
    }

    return numCoinsSpawned;

  }

}
