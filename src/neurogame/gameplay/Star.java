/**
p * NeuroGame.
 * CS 351, Project 3
 * 
 * Team members:
 * Ramon A. Lovato
 * Danny Gomez
 * Marcos Lemus
 */

package neurogame.gameplay;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import neurogame.level.*;
import neurogame.library.Library;

/**
 * A Coin object for NeuroGame.
 * 
 * @author Ramon A. Lovato
 * @team Danny Gomez
 * @team Marcos Lemus
 */
public class Star extends GameObject
{
  private static final BufferedImage image = Library.getSprites().get(GameObjectType.STAR.getName());
  private static final int spriteWidth = 64;
  private static final int spriteHeight = 64;
  private static final int spriteSheetHeight = 2560;
  
  private static Star[] starList = new Star[Star.MAX_STAR_COUNT];

  public static final double MAX_PROBALITY_SPAWN_PER_SEC = 0.9;
  
  public static final int MAX_STAR_COUNT = 20;
  private static int currentStarCount;

  private static double lastCoinSpawnX;

  private int frameCounter;

  private int spriteY;

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
  public Star(double x, double y, World world)
  {
    super(GameObjectType.STAR, x, y, world);
    
    spriteY = 0;
    lastCoinSpawnX = x;
    
  }
  
  public static void initGame()
  {
    lastCoinSpawnX = 0;
    currentStarCount = 0;
    for (int i=0; i<MAX_STAR_COUNT; i++)
    {
      starList[i] = null;
    }
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
    
  }
  
  public void die(boolean showDeathEffect)
  {
    if(showDeathEffect)
    {
      //world.addGameObject(new ParticleEffect(this, world));
    }
    isAlive = false;
    currentStarCount--;
  }
  
  public void hit(GameObject obj)
  {
    if(obj.getName() == "player")die(true);
    else die(false);
  }

  public void render(Graphics2D g)
  {
    // Animation.
    frameCounter++;
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
  

  public static int spawn(Chunk myChunk, World world, double deltaTime)
  {
    if (currentStarCount >= MAX_STAR_COUNT) return 0;
    
    
    double r = Library.RANDOM.nextDouble();
    EnumChunkType type = myChunk.getChunkType();
    if (type == EnumChunkType.FLAT) return 0;

    if (r > MAX_PROBALITY_SPAWN_PER_SEC * deltaTime) return 0;

    //System.out.println("probabilitySpawnCoinPerSec=" + world.getPlayer().skillProbabilitySpawnCoinPerSec);

    double rightEdgeOfScreen = Library.leftEdgeOfWorld
        + Library.getWindowAspect();
    PathVertex vertex = myChunk.getVertexRightOf(rightEdgeOfScreen);

    if (vertex == null) return 0;

    double x = vertex.getX();
    if (x < lastCoinSpawnX + 4 * GameObjectType.STAR.getWidth()) return 0;

    

    int targetSpawnCount = Library.RANDOM.nextInt(5) + 1;
    int numCoinsSpawned = 0;

    double y = vertex.getTop() + GameObjectType.STAR.getHeight() / 3;
    double direction = 1.0;

    if (Library.RANDOM.nextBoolean())
    {
      y = vertex.getBottom() - 1.3 * GameObjectType.STAR.getHeight();
      direction = -1.0;
    }

    for (int i = 0; i < targetSpawnCount; i++)
    {
      if (currentStarCount >= MAX_STAR_COUNT) return 0;
      
      Star myStar = new Star(x, y, world);
      
      if (myStar.wallCollision() != EnumCollisionType.NONE) return numCoinsSpawned;
      
      int starIdx = getFreeStarIndex();
      if (starIdx < 0)
      {
        System.out.println("***ERROR*** Star.spawn() getFreeStarIndex() returned -1");
        return 0;
      }
      
      starList[starIdx] = myStar;
      
      
      world.addGameObject(myStar);
      numCoinsSpawned++;
      currentStarCount++;
      

      if (Library.RANDOM.nextDouble() > .75) x += GameObjectType.STAR.getWidth()
          * (Library.RANDOM.nextDouble() * 2.0);
      y += direction * GameObjectType.STAR.getHeight() * (1.0 + Library.RANDOM.nextDouble() / 2);
    }

    return numCoinsSpawned;

  }
  
  private static int getFreeStarIndex()
  {
    for (int i=0; i<MAX_STAR_COUNT; i++)
    {
      if (starList[i] == null) return i;
      if (!starList[i].isAlive()) return i;
    }
    return -1;
  }
  
  public static Star[] getStarList() { return starList; }

}
