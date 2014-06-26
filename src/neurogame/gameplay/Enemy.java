package neurogame.gameplay;

import java.awt.Graphics2D;
import java.awt.Image;
import java.util.List;

import neurogame.level.Chunk;
import neurogame.level.PathVertex;
import neurogame.level.World;
import neurogame.library.Library;
import neurogame.library.Vector2;

public class Enemy extends GameObject
{
  private static int activeEnemyCount;
  
  private Image image;
  private double lastMovementX, lastMovementY;
  
  public Enemy(GameObjectType type, double x, double y, double width, double height, String name, World world)
  {
    super(type, x, y, world);
    
    lastMovementX = 0.0;
    lastMovementY = 0.0;

    if (type == GameObjectType.ENEMY_STRAIGHT)
    { 
      image = Library.getSprites().get(name);
      maxSpeed = 0.60 + (Library.RANDOM.nextDouble() + Library.RANDOM.nextDouble())/20.0;      
    }
    else if (type == GameObjectType.ENEMY_FOLLOW)
    { 
      image = Library.getSprites().get(name);
      maxSpeed = 0.25 + (Library.RANDOM.nextDouble() + Library.RANDOM.nextDouble())/50.0;
    }
    else if (type == GameObjectType.ENEMY_SINUSOIDAL)
    { 
      image = Library.getSprites().get(name);
      maxSpeed = 0.40 + (Library.RANDOM.nextDouble() + Library.RANDOM.nextDouble())/50.0;
    }
    
  }
  
  
  public void die()
  { 
    if (isAlive())
    { super.die();
      activeEnemyCount--; 
      //System.out.println("   die(): activeEnemyCount=" + activeEnemyCount);
      player.defeatedEnemy(getType());
    }
  }
  
  

  
  public void update(double deltaSec, double scrollDistance)
  {
    if (getX()+getWidth() < Library.leftEdgeOfWorld) die();
    
    else if (checkCollisionWithWall()) die();

    if (!isAlive()) return;
   
    
    
    double maxDistanceChange = maxSpeed * deltaSec;
    
    Vector2 deltaPos;
    
    if (getType() == GameObjectType.ENEMY_STRAIGHT)
    { deltaPos = strategyStraight(maxDistanceChange, scrollDistance);
    }
    else if (getType() == GameObjectType.ENEMY_FOLLOW)
    { deltaPos = strategyFollow(maxDistanceChange, scrollDistance);
    }
    else if (getType() == GameObjectType.ENEMY_SINUSOIDAL)
    { deltaPos = strategySinusoidal(maxDistanceChange, scrollDistance);
    }
    else
    { deltaPos = new Vector2();
    }
    
    move(deltaPos.x, deltaPos.y);
    lastMovementX = deltaPos.x;
    lastMovementY = deltaPos.y;
    
  }
  
  
  public void hit(GameObject obj)
  { 
    GameObjectType type = obj.getType();
    if (type == GameObjectType.COIN) return;
    die();
  }
    
  public Vector2 strategyStraight(double maxDistanceChange, double scrollDistance)
  {
    double dx = scrollDistance - maxDistanceChange;
    double dy = lastMovementY * 0.75;
    Vector2 deltaPos = new Vector2(dx, dy);
    

    
    deltaPos.setMaxMagnitude(maxDistanceChange);
    
   
    double xx = getX()+deltaPos.x;
    double yy = getY()+deltaPos.y;
    
    boolean changedSpeedToAvoidWall = false;
    PathVertex vertex = world.getInterpolatedWallTopAndBottom(xx);
    if (vertex != null)
    { if (yy - getType().getHeight() < vertex.getTopY())
      { changedSpeedToAvoidWall = true;
        deltaPos.y = maxDistanceChange/2.0;
      }
      else if (yy + getType().getHeight()*2.0 > vertex.getBottomY())    
      { changedSpeedToAvoidWall = true;
        deltaPos.y = -maxDistanceChange/2.0;
      }
    
      if (changedSpeedToAvoidWall)
      { deltaPos.setMaxMagnitude(maxDistanceChange);
      }
    }
    return deltaPos;
  }
  
  
  
  public Vector2 strategyFollow(double maxDistanceChange, double scrollDistance)
  {
    
    double dx = scrollDistance + player.getCenterX() - (getX() + getType().getWidth()/2);
    double dy = player.getCenterY() - (getY() + getType().getHeight()/2);
    
    if (getX() < (player.getX() - player.getWidth() * (1.0 + Library.RANDOM.nextDouble()))) 
    { dx = -maxDistanceChange;
    }
    
    Vector2 deltaPos = new Vector2(dx, dy);
    

    
    deltaPos.setMaxMagnitude(maxDistanceChange);
    

    return deltaPos;
  }
  
  
  
  public Vector2 strategySinusoidal(double maxDistanceChange, double scrollDistance)
  {
    double dx = -maxDistanceChange/2.0;
    double dy =  lastMovementY;
    if (dy == 0.0)
    { dy = maxDistanceChange;
      if (Library.RANDOM.nextBoolean())  dy = - maxDistanceChange;
    }

    double xx = getX() + dx;
    PathVertex vertex = world.getInterpolatedWallTopAndBottom(xx);
    if (vertex == null) return new Vector2(lastMovementX, 0);
    
    //double gap = getHeight()/2.0;
    //double stopTop = vertex.getTopY() + gap;
    
    //double stopBot = (vertex.getBottomY() - getHeight()) - gap;
    

    if (getY()+getHeight()/2 > vertex.getCenter())
    { 
      dy = lastMovementY - maxDistanceChange/10;
    }
    else
    { 
      dy = lastMovementY + maxDistanceChange/10;
    }
 
    if (getY() + dy > vertex.getBottomY() - getHeight()) dy = - maxDistanceChange;
    if (getY() + dy < vertex.getTopY()) dy = maxDistanceChange; 
    
    return new Vector2(dx, dy);
  }


  
 

  public boolean checkCollisionWithWall()
  {
    if (!isAlive()) return false;

    if (wallCollision() != EnumCollisionType.NONE)
    {
      die();
      return true;
    }
    return false;
  }
  
  public void checkCollisionWithOtherGameObject()
  {
    List<GameObject> gameObjList = world.getObjectList();
   
    //for (GameObject obj : gameObjList)
    //{
    //  if (this.collision(obj))
    //  { if (obj.enemy
    //}
  }
  
  
  public void render(Graphics2D g)
  {
    int xx = Library.worldPosXToScreen(getX());
    int yy = Library.worldPosYToScreen(getY());
    g.drawImage(image, xx, yy, null);
  }
  
  
  public static int spawn(Chunk myChunk, World world, List<GameObject> gameObjects, double deltaTime)
  {
    GameObjectType type = myChunk.getChunkType().getEnemyType();
    if (type == null) return 0;
    int maxEnemy = world.getPlayer().getMaxEnemy(type);
    
    if (activeEnemyCount >= maxEnemy) return 0;
     
    double r = Library.RANDOM.nextDouble();
    
    //System.out.println("Enemy Spawn: r=" + r + ", activeEnemyCount=" + activeEnemyCount +", maxEnemy=" + maxEnemy);
    
    if (r > (0.75 * deltaTime)*(maxEnemy-activeEnemyCount)) return 0;

    

    double rightEdgeOfScreen = Library.leftEdgeOfWorld + Library.getWindowAspect();
    PathVertex vertex = myChunk.getVertexRightOf(rightEdgeOfScreen);

    if (vertex == null) return 0;

    double x = vertex.getX(); 
    double rangeY = (vertex.getBottomY() - vertex.getTopY()) - type.getHeight();
    if (rangeY < 0.01) return 0;
    
    double y = world.getPlayer().getY() + type.getHeight()*(Library.RANDOM.nextDouble() - Library.RANDOM.nextDouble());
    if ((y <= vertex.getTopY()) || y > vertex.getBottomY() - type.getHeight()) y = vertex.getCenter()-type.getHeight()/2;

    
    Enemy myEnemy = new Enemy(type, x, y, type.getWidth(), type.getHeight(), type.getName(), world);
   
    gameObjects.add(myEnemy);
    activeEnemyCount++;
    //System.out.println("   ===> activeEnemyCount=" + activeEnemyCount);

    return 1;

  }
  
  public static void initGame() 
  { activeEnemyCount = 0;
  }
  
  
  public static int getActiveEnemyCount() {return activeEnemyCount;}

}
