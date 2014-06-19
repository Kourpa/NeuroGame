package neurogame.gameplay;

import java.awt.Graphics2D;
import java.awt.Image;
import java.util.List;

import neurogame.level.Chunk;
import neurogame.level.EnumChunkType;
import neurogame.level.PathVertex;
import neurogame.level.World;
import neurogame.library.Library;
import neurogame.library.Vector2;

public class Enemy extends GameObject
{

  public enum EnumEnemyType
  {
    STRAIGHT
    { public String getName() {return "EnemyStraight";}
      public double getWidth() {return 0.05;}
      public double getHeight() {return 0.05;}
      public int getDamageToPlayer() {return 10;}
    }, 
    
    FOLLOW
    { public String getName() {return "EnemyFollow";}
      public double getWidth() {return 0.05;}
      public double getHeight() {return 0.05;}
      public int getDamageToPlayer() {return 15;}
    }, 
    
    SINUSOIDAL
    { public String getName() {return "EnemySinusoidal";}
      public double getWidth() {return 0.05;}
      public double getHeight() {return 0.05;}
      public int getDamageToPlayer() {return 10;}
    };
    
    public abstract String getName();
    public abstract double getWidth();
    public abstract double getHeight();
    public abstract int getDamageToPlayer();
  }
  
  private static int activeEnemyCount;
  
  private Image image;
  private EnumEnemyType type;
  private double lastMovementX, lastMovementY;
  
  public Enemy(EnumEnemyType type, double x, double y, double width, double height, String name, World world)
  {
    super(x, y, width, height, name, world);
    
    lastMovementX = 0.0;
    lastMovementY = 0.0;
    
    this.type = type;
    if (type == EnumEnemyType.STRAIGHT)
    { 
      image = Library.getSprites().get(name);
      maxSpeed = 0.65f;
    }
    else if (type == EnumEnemyType.FOLLOW)
    { 
      image = Library.getSprites().get(name);
      maxSpeed = 0.30f;
    }
    
  }
  
  
  public void die()
  { 
    if (isAlive())
    { super.die();
      activeEnemyCount--; 
      //System.out.println("   die(): activeEnemyCount=" + activeEnemyCount);
      player.defeatedEnemy(type);
    }
  }
  
  

  
  public boolean update(double deltaSec, double scrollDistance)
  {
    if (getX()+getWidth() < Library.leftEdgeOfWorld) return false;
    
    if (checkCollisionWithPlayer()) return false;
    if (checkCollisionWithWall()) return false;
    
    double maxDistanceChange = maxSpeed * deltaSec;
    
    Vector2 deltaPos;
    
    if (type == EnumEnemyType.STRAIGHT)
    { deltaPos = strategyStraight(maxDistanceChange, scrollDistance);
    }
    else if (type == EnumEnemyType.FOLLOW)
    { deltaPos = strategyFollow(maxDistanceChange, scrollDistance);
    }
    else
    { deltaPos = new Vector2();
    }
    
    move(deltaPos.x, deltaPos.y);
    lastMovementX = deltaPos.x;
    lastMovementY = deltaPos.y;
    
    return true;
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
    { if (yy - type.getHeight() < vertex.getTopY())
      { changedSpeedToAvoidWall = true;
        deltaPos.y = maxDistanceChange/2.0;
      }
      else if (yy + type.getHeight()*2.0 > vertex.getBottomY())    
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
    
    double dx = scrollDistance + player.getCenterX() - (getX() + type.getWidth()/2);
    double dy = player.getCenterY() - (getY() + type.getHeight()/2);
    
    if (getX() < player.getX()) dx = -maxDistanceChange;
    
    Vector2 deltaPos = new Vector2(dx, dy);
    

    
    deltaPos.setMaxMagnitude(maxDistanceChange);
    

    return deltaPos;
  }


  public boolean checkCollisionWithPlayer()
  {
    if (!isAlive()) return false;

    if (collision(player))
    {
      // Library.log(getName() + " collided with player", world.getRisk());
      double hitX = (getCenterX() + player.getCenterX()) / 2.0;
      double hitY = (getCenterY() + player.getCenterY()) / 2.0;

      player.crashedIntoEnemy(type);
      player.loseHealth(hitX, hitY, type.getDamageToPlayer());
      die();
      return true;
    }
    return false;
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
  
  public void render(Graphics2D g)
  {
    int xx = Library.worldPosXToScreen(getX());
    int yy = Library.worldPosYToScreen(getY());
    g.drawImage(image, xx, yy, null);
  }
  
  
  public static int spawn(Chunk myChunk, World world, List<GameObject> gameObjects, double deltaTime)
  {
    EnumEnemyType type = myChunk.getChunkType().getEnemyType();
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

    double rangeY = (vertex.getBottomY() - vertex.getTopY()) - type.getHeight() * 2;
    if (rangeY < 0.01) return 0;
    
    
    double y = (Library.RANDOM.nextDouble() * rangeY) + vertex.getTopY() + type.getHeight(); 

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
