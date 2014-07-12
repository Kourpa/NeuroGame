/**
 * World generates and updates randomly generated path.
 * @author Marcos
 */
package neurogame.level;
import java.awt.Graphics2D;
import java.util.ArrayList;

import neurogame.gameplay.Coin;
import neurogame.gameplay.Enemy;
import neurogame.gameplay.EnumCollisionType;
import neurogame.gameplay.GameObject;
import neurogame.gameplay.InfoMessage;
import neurogame.gameplay.Player;
import neurogame.gameplay.PowerUp;
import neurogame.library.Library;

public class World
{
  private final Player player;
  private Chunk chunkLeft, chunkRight;
  // private final Area walls = new Area(); // The wrong way to do walls
  private ArrayList<GameObject> gameObjectList = new ArrayList<GameObject>();
  private ArrayList<GameObject> objectWaitList = new ArrayList<GameObject>();

  private double windowWidth;
  private int frameCountSinceLastChunkTypeChange;

  // private double visibleWorldLeft = 0; //total horizontal change
  private double chunkScolledDistance = 0; // used to determine when to generate
                                           // new chunks

  private double skillBasedChunkGapHeight;

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

    player = new Player(0.1, 1 / 2.0, this);
    gameObjectList.clear();
    gameObjectList.add(player);

    chunkLeft = new Chunk(null, windowWidth, EnumChunkType.FLAT,
        EnumChunkType.FLAT.getDefaultOpeningHeight());

    skillBasedChunkGapHeight = EnumChunkType.SMOOTH.getDefaultOpeningHeight();
    chunkRight = new Chunk(chunkLeft, windowWidth, EnumChunkType.SMOOTH,
        skillBasedChunkGapHeight);
    
//    skillBasedChunkGapHeight = EnumChunkType.CURVED.getDefaultOpeningHeight();
//    chunkRight = new Chunk(chunkLeft, windowWidth, EnumChunkType.CURVED,
//        skillBasedChunkGapHeight);


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

    /** if the leading chunk has left the screen re-randomize it.* */
    // if(chunkScolledDistance >= chunkSize * pathType.getStepSize()){
    if (chunkScolledDistance >= chunkLeft.getWidth())
    {
      EnumChunkType pathType = chunkRight.getChunkType();
      int chunkBonusScore = (int)(100*Math.max(0, pathType.getDefaultOpeningHeight() - skillBasedChunkGapHeight)) +
          player.getMaxEnemy(pathType)*25;
      
      player.addScore(chunkBonusScore);
      InfoMessage scoreInfo = new InfoMessage(player.getCenterX(), player.getCenterY(), this, String.valueOf(chunkBonusScore));
      addGameObject(scoreInfo);
      
      

      
      
      
      chunkLeft = chunkRight;

      if (player.getCollisionCountInCurrentChunk() == 0)
      {
        skillBasedChunkGapHeight = skillBasedChunkGapHeight * 0.9;
        if (skillBasedChunkGapHeight < player.getHeight() * 3.0) skillBasedChunkGapHeight = player
            .getHeight() * 3.0;
      }
      else if (player.getCollisionCountInCurrentChunk() > 3)
      {
        skillBasedChunkGapHeight = skillBasedChunkGapHeight * 1.1;
        if (skillBasedChunkGapHeight > EnumChunkType.FLAT
            .getDefaultOpeningHeight())
        {
          skillBasedChunkGapHeight = EnumChunkType.FLAT
              .getDefaultOpeningHeight();
        }
      }
      player.resetCollisionCountInCurrentChunk();

      
      //System.out.println("frameCountSinceLastChunkTypeChange="+frameCountSinceLastChunkTypeChange);
      
      if ((frameCountSinceLastChunkTypeChange > 5) && (Library.RANDOM.nextInt(30) < frameCountSinceLastChunkTypeChange))
      {
        pathType = EnumChunkType.getRandomType();

        if (pathType != chunkRight.getChunkType())
        { // reset for new chunkType;
          skillBasedChunkGapHeight = pathType.getDefaultOpeningHeight();
          frameCountSinceLastChunkTypeChange = 0;
        }
      }
      
      if (pathType == chunkRight.getChunkType()) frameCountSinceLastChunkTypeChange++;

      chunkRight = new Chunk(chunkLeft, windowWidth, pathType,
          skillBasedChunkGapHeight);

      chunkScolledDistance = 0;
      Library.leftEdgeOfWorld = chunkLeft.getStartX();

      crystalWalls.addChunk(chunkRight);
    }

    Library.leftEdgeOfWorld = chunkLeft.getStartX() + chunkScolledDistance;

    spawner(deltaTime);

    return Library.leftEdgeOfWorld - visibleWorldLeftBeforeUpdate;
  }

  /**
   * Spawns the variety of GameObjects into the world.
   */
  private void spawner(double deltaTime)
  {
    Coin.spawn(chunkRight, this, deltaTime);
    Enemy.spawn(chunkRight, this, deltaTime);
    PowerUp.spawn(chunkRight, this, deltaTime);

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
