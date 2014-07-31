package neurogame.gameplay;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;

import neurogame.level.Chunk;
import neurogame.level.ParticleEffect;
import neurogame.level.PathVertex;
import neurogame.level.World;
import neurogame.library.Library;
import neurogame.library.Vector2;

public class Enemy extends GameObject
{
  private static int activeEnemyCount;
  
  private Image image;
  
  private double maxSpeed;
  public static final int MAX_ENEMY_COUNT = 6; 
  
  private int enemyIdx;
  private boolean enemyFollowStoppedFollowing = false;
  private static double playerHeightAtLastSpawn = -77;
  
  private Vector2 velocity = new Vector2();
  
  public Enemy(GameObjectType type, double x, double y, double width, double height, String name, World world)
  {
    super(type, x, y, world);
    
    enemyIdx = activeEnemyCount;

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
  
  
  public void die(boolean showDeathEffect)
  { 
    if (showDeathEffect) 
    { world.addGameObject(new ParticleEffect(this, getCenterX(), getCenterY(), world));
    }
    if (isAlive())
    { isAlive = false;
      activeEnemyCount--; 
      //System.out.println("   die(): activeEnemyCount=" + activeEnemyCount);
      //player.defeatedEnemy(getType());
    }
  }
  
  

  
  public void update(double deltaSec, double scrollDistance)
  {
    if (getX()+getWidth() < Library.leftEdgeOfWorld)
    { world.getPlayer().killedOrAvoidedEnemy(this);
      die(false);
    }

    else if (checkCollisionWithWall()) die(true);

    if (!isAlive()) return;
   
    
    
    double maxDistanceChange = maxSpeed * deltaSec;
    

    
    if (getType() == GameObjectType.ENEMY_STRAIGHT)
    { strategyStraight(maxDistanceChange, scrollDistance);
    }
    else if (getType() == GameObjectType.ENEMY_FOLLOW)
    { strategyFollow(maxDistanceChange, scrollDistance);
    }
    else if (getType() == GameObjectType.ENEMY_SINUSOIDAL)
    { strategySinusoidal(maxDistanceChange, scrollDistance);
    }
    
    move(velocity.x, velocity.y);
    
  }
  
  
  public void hit(GameObject obj)
  { 
    GameObjectType type = obj.getType();
    if (type == GameObjectType.STAR) return;
    if (type == GameObjectType.POWER_UP) return;
    die(true);
  }
    
  public void strategyStraight(double maxDistanceChange, double scrollDistance)
  {
    velocity.x = scrollDistance - maxDistanceChange;
    velocity.y = velocity.y * 0.75;
    
    velocity.setMaxMagnitude(maxDistanceChange);
    
   
    double xx = getX()+velocity.x;
    double yy = getY()+velocity.y;
    
    boolean changedSpeedToAvoidWall = false;
    PathVertex vertex = world.getInterpolatedWallTopAndBottom(xx);
    if (vertex != null)
    { if (yy - getType().getHeight() < vertex.getTopY())
      { changedSpeedToAvoidWall = true;
        velocity.y = maxDistanceChange/2.0;
      }
      else if (yy + getType().getHeight()*2.0 > vertex.getBottomY())    
      { changedSpeedToAvoidWall = true;
        velocity.y = -maxDistanceChange/2.0;
      }
    
      if (changedSpeedToAvoidWall)
      { velocity.setMaxMagnitude(maxDistanceChange);
      }
    }
  }
  
  
  
  public void strategyFollow(double maxDistanceChange, double scrollDistance)
  {
    if (enemyFollowStoppedFollowing) strategyStraight(maxDistanceChange, scrollDistance);
    else
    {
      Player player = world.getPlayer();
      velocity.x = scrollDistance + player.getCenterX() - (getX() + getType().getWidth()/2);
      velocity.y = player.getCenterY() - (getY() + getType().getHeight()/2);
      
      velocity.setMaxMagnitude(maxDistanceChange);
    
      if (getX() + getWidth()*(2 + 8*Library.RANDOM.nextDouble()) < player.getX())
      { enemyFollowStoppedFollowing = true;
      }
    }
  }
  
  
  
  public void strategySinusoidal(double maxDistanceChange, double scrollDistance)
  {
    velocity.x = -maxDistanceChange/2.0;
    double dy =  velocity.y;
    if (dy == 0.0)
    { dy = maxDistanceChange;
      if (Library.RANDOM.nextBoolean())  dy = - maxDistanceChange;
    }

    double xx = getX() + velocity.x;
    PathVertex vertex = world.getInterpolatedWallTopAndBottom(xx);
    if (vertex == null)
    { velocity.y = 0;
      return;
    }
   
    

    if (getY()+getHeight()/2 > vertex.getCenter())
    { 
      velocity.y = velocity.y - maxDistanceChange/10;
    }
    else
    { 
      velocity.y = velocity.y + maxDistanceChange/10;
    }
 
    if (getY() + velocity.y > vertex.getBottomY() - getHeight()) velocity.y = - maxDistanceChange;
    if (getY() + velocity.y < vertex.getTopY()) velocity.y = maxDistanceChange; 
    
  }


  
 

  public boolean checkCollisionWithWall()
  {
    if (!isAlive()) return false;

    if (wallCollision() != EnumCollisionType.NONE)
    {
      die(true);
      return true;
    }
    return false;
  }
  
  
  
  public void render(Graphics2D canvas)
  {
    int xx = Library.worldPosXToScreen(getX());
    int yy = Library.worldPosYToScreen(getY());
    
    double angle = Math.atan2(velocity.y, velocity.x);
    double locationX = image.getWidth(null) / 2;
    double locationY = image.getHeight(null) / 2;
    AffineTransform transform = new AffineTransform();
    transform.translate(xx+locationX, yy+locationY);
    transform.rotate(angle);
    transform.translate(-locationX, -locationY);
    canvas.drawImage(image, transform, null);
    
    if (Library.DEBUG_SHOW_HITBOXES)
    { int x1 = Library.worldPosXToScreen(getHitMinX());
      int y1 = Library.worldPosYToScreen(getHitMinY());
      int x2 = Library.worldPosXToScreen(getHitMaxX());
      int y2 = Library.worldPosYToScreen(getHitMaxY());
      canvas.setColor(Color.RED);
      canvas.drawRect(x1,y1, x2-x1, y2-y1);
    }
    
  }
  
  
  public static int spawn(Chunk myChunk, World world, double deltaTime)
  {
    GameObjectType type = myChunk.getChunkType().getEnemyType();
    Player player =  world.getPlayer();
    if (type == null) return 0;
    int maxEnemy = player.getMaxEnemy(type);
    
    if (activeEnemyCount >= maxEnemy) return 0;
    
    if (type == GameObjectType.ENEMY_STRAIGHT) 
    { if ((activeEnemyCount > 1) && (Math.abs(player.getY() - playerHeightAtLastSpawn) < player.getHeight())) 
      { return 0;
      } 
    }
    
    
    double r = Library.RANDOM.nextDouble();
    
    if (r > (0.75 * deltaTime)*(maxEnemy-activeEnemyCount)) return 0;

    

    double rightEdgeOfScreen = Library.leftEdgeOfWorld + Library.getWindowAspect();
    PathVertex vertex = myChunk.getVertexRightOf(rightEdgeOfScreen);

    if (vertex == null) return 0;

    double x = vertex.getX(); 
    double rangeY = (vertex.getBottomY() - vertex.getTopY()) - type.getHeight();
    if (rangeY < 0.01) return 0;
    
    double y = player.getY() + type.getHeight()*(Library.RANDOM.nextDouble() - Library.RANDOM.nextDouble())*3;
    if ((y <= vertex.getTopY()) || y > vertex.getBottomY() - type.getHeight()) y = vertex.getCenter()-type.getHeight()/2;

    
    Enemy myEnemy = new Enemy(type, x, y, type.getWidth(), type.getHeight(), type.getName(), world);
   
    world.addGameObject(myEnemy);
    playerHeightAtLastSpawn = player.getY();
    activeEnemyCount++;
    //System.out.println("   ===> activeEnemyCount=" + activeEnemyCount);

    return 1;

  }


  public static void initGame() 
  { 
    activeEnemyCount = 0;
  }
  
  public int getEnemyIdx(){return enemyIdx;}
  public static int getActiveEnemyCount() {return activeEnemyCount;}

}
