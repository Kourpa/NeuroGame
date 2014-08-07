/**
 * World generates and updates randomly generated path.
 * @author Marcos
 */
package neurogame.level;
import java.awt.Graphics2D;
import java.util.ArrayList;

import neurogame.gameplay.Star;
import neurogame.gameplay.Enemy;
import neurogame.gameplay.EnumCollisionType;
import neurogame.gameplay.GameObject;
import neurogame.gameplay.Player;
import neurogame.gameplay.Ammo;
import neurogame.library.Library;

public class World
{
  private final Player player;
  private double playerHealthAtStartOfLastChunk;
  private Chunk chunkLeft, chunkRight;
  private ArrayList<GameObject> gameObjectList = new ArrayList<GameObject>();
  private ArrayList<GameObject> objectWaitList = new ArrayList<GameObject>();


  private double windowWidth;
  private int frameCountSinceLastChunkTypeChange;

  private double chunkScolledDistance = 0; // used to determine when to generate
                                           // new chunks

  private double[] skillBasedChunkGapHeight;

  private CrystalGrower crystalWalls; // fractals!

  /**
   * Initializes firstChunk, secondChunk, player and the outer walls. and the
   * fractal.
   */
  public World()
  {
    Library.leftEdgeOfWorld = 0.0;
    windowWidth = Library.getWindowAspect();
    frameCountSinceLastChunkTypeChange = 0;
    playerHealthAtStartOfLastChunk = Library.HEALTH_MAX;

    player = new Player(0.1, 1 / 2.0, this);
    
    gameObjectList.clear();
    gameObjectList.add(player);
    objectWaitList.clear();
    
    
    skillBasedChunkGapHeight = new double[EnumChunkType.SIZE];
    


    for (EnumChunkType type : EnumChunkType.values())
    {
      skillBasedChunkGapHeight[type.ordinal()] = type.getDefaultOpeningHeight();
    }
    

    chunkLeft = new Chunk(null, windowWidth, EnumChunkType.FLAT, EnumChunkType.FLAT.getDefaultOpeningHeight());

    double gapHeight = skillBasedChunkGapHeight[EnumChunkType.SPIKE.ordinal()];
    chunkRight = new Chunk(chunkLeft, windowWidth, EnumChunkType.SPIKE, gapHeight);
    

    crystalWalls = new CrystalGrower(chunkLeft, chunkRight);
  }

  /**
   * updates the chunks regenerating if the width is reached
   * 
   * @param graphics
   * @param deltaTime
   * @return
   */
  public double update(double deltaTime)
  {
    
    for (GameObject obj : objectWaitList) 
    {
      //System.out.println("World.update() adding object from waitlist");
      gameObjectList.add(obj);
    }
    objectWaitList.clear();
    
    double visibleWorldLeftBeforeUpdate = Library.leftEdgeOfWorld;
    // System.out.println("update("+deltaTime+"), chunkLeft.getWidth() =

    double deltaDistance = deltaTime * Library.WORLD_SCROLL_SPEED;

    /** add the scrollSpeed to the distance* */
    chunkScolledDistance += deltaDistance;

    if (chunkScolledDistance >= chunkLeft.getWidth()) createChunk();

    Library.leftEdgeOfWorld = chunkLeft.getStartX() + chunkScolledDistance;

    spawner(deltaTime);

    return Library.leftEdgeOfWorld - visibleWorldLeftBeforeUpdate;
  }
  
  private void createChunk()
  {
    
    EnumChunkType pathType = chunkRight.getChunkType();
    
    chunkLeft = chunkRight;
   
    if ((frameCountSinceLastChunkTypeChange > 5) && (Library.RANDOM.nextInt(30) < frameCountSinceLastChunkTypeChange))
    {
      pathType = EnumChunkType.getRandomType();

      if (pathType != chunkRight.getChunkType())
      { 
        frameCountSinceLastChunkTypeChange = 0;
      }
    }
    
    double gapHeight = skillBasedChunkGapHeight[pathType.ordinal()];
    double currentPlayerHealth = player.getHealth();
    
    if (currentPlayerHealth >= playerHealthAtStartOfLastChunk)
    {
      gapHeight = gapHeight * 0.9;
      if (gapHeight < pathType.getMinimumOpeningHeight()) gapHeight = pathType.getMinimumOpeningHeight();
    }
    else
    {
      double percentHealthLoss = (playerHealthAtStartOfLastChunk - currentPlayerHealth)/playerHealthAtStartOfLastChunk;
      gapHeight = gapHeight * (1 + percentHealthLoss);;
      if (gapHeight > pathType.getDefaultOpeningHeight()) gapHeight = pathType.getDefaultOpeningHeight();
    }
    
    System.out.println("World.createChunk(type="+pathType+"): gapHeight="+gapHeight);
    skillBasedChunkGapHeight[pathType.ordinal()] = gapHeight;
    playerHealthAtStartOfLastChunk = currentPlayerHealth;
    
    if (pathType == chunkRight.getChunkType()) frameCountSinceLastChunkTypeChange++;

    chunkRight = new Chunk(chunkLeft, windowWidth, pathType, gapHeight);

    chunkScolledDistance = 0;
    Library.leftEdgeOfWorld = chunkLeft.getStartX();

    crystalWalls.addChunk(chunkRight);
  }
  
  public EnumChunkType getRightChunkType()
  {
    return chunkRight.getChunkType();
  }
  

  /**
   * Spawns the variety of GameObjects into the world.
   */
  private void spawner(double deltaTime)
  {
    Star.spawn(chunkRight, this, deltaTime);
    Enemy.spawn(chunkRight, this, deltaTime);
    Ammo.spawn(chunkRight, this, deltaTime);

  }
  
  public void addGameObject(GameObject obj)
  {
    //This method may be called while another part of the program is iterating through the game list
    //Therefore, add the object to a waitlist that gets updated on the next call to world.update
    
    objectWaitList.add(obj);
    
    
  }

  public void render(Graphics2D g)
  {
    crystalWalls.render(g, chunkScolledDistance);
  }

  // geters for the worldObjects
  public Player getPlayer()
  {
    return player;
  }

  public ArrayList<GameObject> getObjectList()
  {
    return gameObjectList;
  }

  public double getVisibleWorldLeft()
  {
    return Library.leftEdgeOfWorld;
  }

  public double getVisibleWorldRight()
  {
    return Library.leftEdgeOfWorld + windowWidth;
  }
  
  public double getSkillBasedChunkGapHeight()
  {
    EnumChunkType pathType = chunkRight.getChunkType();
    return skillBasedChunkGapHeight[pathType.ordinal()];
  }
  
  
  public PathVertex getInterpolatedWallTopAndBottom(double x)
  {
    if (x < Library.leftEdgeOfWorld) return null;

    if (x > chunkRight.getStartX() + chunkRight.getWidth()) return null;
    
    if (x < chunkRight.getStartX())
    { 
      return chunkLeft.getInterpolatedWallTopAndBottom(x);
    }
    else
    { 
      return chunkRight.getInterpolatedWallTopAndBottom(x);
    }
  }

  public EnumCollisionType collisionWithWall(double x, double y)
  {
    
    if (x < Library.leftEdgeOfWorld) return EnumCollisionType.NONE;

    if (x > chunkRight.getStartX() + chunkRight.getWidth()) return EnumCollisionType.NONE;


    if (x < chunkRight.getStartX())
    { 
      if (chunkLeft.getTop().contains(x, y)) return EnumCollisionType.WALL_TOP;
      if (chunkLeft.getBottom().contains(x, y)) return EnumCollisionType.WALL_BOTTOM;
    }
    else
    { 
      if (chunkRight.getTop().contains(x, y)) return EnumCollisionType.WALL_TOP;
      if (chunkRight.getBottom().contains(x, y)) return EnumCollisionType.WALL_BOTTOM;
    }

    return EnumCollisionType.NONE;

  }

}
