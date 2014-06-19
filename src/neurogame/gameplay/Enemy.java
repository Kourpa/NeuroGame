package neurogame.gameplay;

import java.awt.Graphics2D;
import java.awt.Image;
import java.util.List;

import neurogame.level.Chunk;
import neurogame.level.EnumChunkType;
import neurogame.level.PathVertex;
import neurogame.level.World;
import neurogame.library.Library;

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
  private double startY, startDistanceFromTop;
  
  public Enemy(EnumEnemyType type, double x, double y, double width, double height, String name, World world)
  {
    super(x, y, width, height, name, world);
    
    this.type = type;
    if (type == EnumEnemyType.STRAIGHT)
    { 
      image = Library.getSprites().get(name);
      startY = y;
      maxSpeed = 0.65f;
    }
    
  }
  
  
  public void die()
  { 
    super.die();
    activeEnemyCount--; 
    player.defeatedEnemy(type);
  }

  
  public boolean update(double deltaSec, double scrollDistance)
  {
    if (getX()+getWidth() < Library.leftEdgeOfWorld) return false;
    
    if (checkCollisionWithPlayer()) return false;
    if (checkCollisionWithWall()) return false;
    
    double maxDistanceChange = maxSpeed * deltaSec;

    double dx = scrollDistance - maxDistanceChange;
    double dy = 0;//getY() - startY;
    
    double speed = Math.sqrt(dx*dx + dy*dy);
    if (speed > maxDistanceChange)
    {
      dx = (dx / speed) * maxDistanceChange;
      dy = (dy / speed) * maxDistanceChange;
    }
    
    
    double xx = getX()+dx;
    double yy = getY()+dy;
    
    boolean changedSpeedToAvoidWall = false;
    PathVertex vertex = world.getInterpolatedWallTopAndBottom(xx);
    if (vertex != null)
    { if (yy - type.getHeight() < vertex.getTopY())
      { changedSpeedToAvoidWall = true;
        dy = maxDistanceChange/2.0;
      }
      else if (yy + type.getHeight()*2.0 > vertex.getBottomY())    
      { changedSpeedToAvoidWall = true;
        dy = -maxDistanceChange/2.0;
      }
    
      if (changedSpeedToAvoidWall)
      { speed = Math.sqrt(dx*dx + dy*dy);
        if (speed > maxDistanceChange)
        {
          dx = (dx / speed) * maxDistanceChange;
          dy = (dy / speed) * maxDistanceChange;
        }
      }
    }
    move(dx, dy);

    return true;
  }

  public boolean checkCollisionWithPlayer()
  {
    if (!isAlive()) return false;

    if (collision(player))
    {
      die();
      // Library.log(getName() + " collided with player", world.getRisk());
      double hitX = (getCenterX() + player.getCenterX()) / 2.0;
      double hitY = (getCenterY() + player.getCenterY()) / 2.0;

      player.crashedIntoEnemy(type);
      player.loseHealth(hitX, hitY, type.getDamageToPlayer());
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
    EnumEnemyType type = myChunk.getpathType().getEnemyType();
    if (type == null) return 0;
    int maxEnemy = world.getPlayer().getMaxEnemy(type);
    
    if (activeEnemyCount >= maxEnemy) return 0;
     
    double r = Library.RANDOM.nextDouble();
    
    //System.out.println("Enemy Spawn: r=" + r + ", activeEnemyCount=" + activeEnemyCount +", maxEnemy=" + maxEnemy);
    
    if (r > (0.5 * deltaTime)/(maxEnemy-activeEnemyCount)) return 0;

    

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

    return 1;

  }
  
  public static void initGame() 
  { activeEnemyCount = 0;;
  }
  
  
  public static int getActiveEnemyCount() {return activeEnemyCount;}

}
