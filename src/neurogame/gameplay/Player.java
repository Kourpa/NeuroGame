package neurogame.gameplay;

import java.awt.Graphics2D;
import java.awt.Image;

import neurogame.gameplay.Enemy.EnumEnemyType;
import neurogame.level.World;
import neurogame.library.Library;
import neurogame.library.QuickSet;

/**
 * @author Daniel
 * 
 */
public class Player extends GameObject
{

  private static String name = "player";
  private static Image image = Library.getSprites().get(name);
  private PowerUp powerUp;

  private boolean invulnerable = false;

  private int wallCollisionCountInCurrentChunk;
 
  private double timeOfLastWallCollision;
  

  private int gameWallCollisionCount;
  private double gameScore;
  private int gameCoinsEarned;
  private double gameTotalSeconds;
  
  public double skillProbabilitySpawnCoinPerSec;
  private double skillEnemyStraight;
  private double skillEnemyFollow;

  private QuickSet<Spark> sparkList;

  private DirectionVector directionVector = new DirectionVector();
  private double lastVelocityX, lastVelocityY;

  public Player(double x, double y, double width, double height, World world)
  {
    super(x, y, width, height, name, world);
    setIsVisible(true);
    initGame();
  }

  public void initGame()
  {
    health = Library.HEALTH_MAX;
    wallCollisionCountInCurrentChunk = 0;
    gameWallCollisionCount = 0;
    gameScore = 0;
    gameCoinsEarned = 0;
    gameTotalSeconds = 0;
    timeOfLastWallCollision = 0;
    
    skillProbabilitySpawnCoinPerSec = (Coin.MIN_PROBALITY_SPAWN_PER_SEC + Coin.MAX_PROBALITY_SPAWN_PER_SEC)/2.0;

    maxSpeed = 0.5f;

    lastVelocityX = 0;
    lastVelocityY = 0;
    
    skillEnemyStraight = 1;
    skillEnemyFollow = 1;
    Enemy.initGame();
  }

  /**
   * Update method for the player. This will be called on every frame.
   * 
   * @param dir
   *          Direction to move the player obj, if any.
   */
  public boolean update(double deltaSec, double scrollDistance)
  {
    gameTotalSeconds += deltaSec;
    // System.out.println(this);

    double inputSpeed = directionVector.getAcceleration();
    if (inputSpeed > maxSpeed) inputSpeed = maxSpeed;

    double velocityX = lastVelocityX * 0.75 + directionVector.x * inputSpeed;
    double velocityY = lastVelocityY * 0.75 + directionVector.y * inputSpeed;

    // System.out.println(directionVector);

    double speed = Math.sqrt(velocityX * velocityX + velocityY * velocityY);
    if (speed > maxSpeed)
    {
      velocityX = (velocityX / speed) * maxSpeed;
      velocityY = (velocityY / speed) * maxSpeed;
    }
    lastVelocityX = velocityX;
    lastVelocityY = velocityY;

    double dx = velocityX * deltaSec + scrollDistance;
    double dy = velocityY * deltaSec;

    double nextX = getX() + dx;
    double nextY = getY() + dy;
    if (nextY < 0) nextY = 0;
    if (nextY + getHeight() > 1.0) nextY = 1.0 - getHeight();
    if (nextX < Library.leftEdgeOfWorld) nextX = Library.leftEdgeOfWorld;
    if (nextX + getWidth() > world.getVisibleWorldRight())
    {
      nextX = (world.getVisibleWorldRight()) - getWidth();
    }

    setLocation(nextX, nextY);
    EnumCollisionType collisionLocation = wallCollision();
    if (collisionLocation != EnumCollisionType.NONE)
    {
      
      lastVelocityX = 0;
      lastVelocityY = -lastVelocityY;
      
      loseHealth(getX(), getY(), Library.DAMAGE_PER_WALL_HIT);
      
      double stepSize = getHeight()/10.0;
      if (collisionLocation == EnumCollisionType.WALL_BOTTOM) stepSize = -stepSize;
      
      setLocation(getX(), getY() + stepSize);
      
      while (wallCollision() != EnumCollisionType.NONE)
      {
        setLocation(getX(), getY() + stepSize);
        
        if (getY() < 0 || getY() > 1) 
        { setLocation(getX(), 0.5);
          break;
        }
      }
    }

    if (sparkList != null)
    {
      for (int i = 0; i < sparkList.size(); i++)
      {
        Spark spark = sparkList.get(i);
        boolean alive = spark.move();
        if (!alive) sparkList.remove(i);
      }
    }

    return true;
  }

  public void setDirection(DirectionVector directionVector)
  {

    this.directionVector.x = lastVelocityX * 0.25 + directionVector.x;
    this.directionVector.y = lastVelocityY * 0.25 + directionVector.y;
  }

  public void loseHealth(double hitX, double hitY, int damage)
  {
    if (invulnerable) return;
    if (gameTotalSeconds - timeOfLastWallCollision < 0.25) return;
    
    timeOfLastWallCollision = gameTotalSeconds;
    
    health -= damage;
    if (health < 0) health = 0;

    wallCollisionCountInCurrentChunk++;
    
    skillProbabilitySpawnCoinPerSec += 0.05;
    if (skillProbabilitySpawnCoinPerSec > Coin.MAX_PROBALITY_SPAWN_PER_SEC)
    { skillProbabilitySpawnCoinPerSec = Coin.MAX_PROBALITY_SPAWN_PER_SEC;
    }
    

    int sparkCount = Library.RANDOM.nextInt(20) + Library.RANDOM.nextInt(20)
        + Library.RANDOM.nextInt(20) + 25;
    sparkList = new QuickSet<Spark>(sparkCount);
    for (int i = 0; i < sparkCount; i++)
    {
      sparkList.add(new Spark(hitX, hitY, world));
    }
  }

  public void collectCoin(Coin myCoin)
  {
    gameCoinsEarned++;
    gameScore += Library.COIN_POINTS;

    health += Library.HEALTH_PER_COIN;
    
    skillProbabilitySpawnCoinPerSec -= 0.005;
    if (health > Library.HEALTH_MAX) 
    { health = Library.HEALTH_MAX;
      skillProbabilitySpawnCoinPerSec -= 0.025;
    }
    if (skillProbabilitySpawnCoinPerSec < Coin.MIN_PROBALITY_SPAWN_PER_SEC)
    { skillProbabilitySpawnCoinPerSec = Coin.MIN_PROBALITY_SPAWN_PER_SEC;
    }
    
    
  }

  public void collectPowerUp()
  {
    gameScore += Library.POWERUP_POINTS;
  }

  public void defeatedEnemy(EnumEnemyType type)
  {
    if (type == EnumEnemyType.STRAIGHT)
    { skillEnemyStraight += 0.2;
      if (skillEnemyStraight > 6) skillEnemyStraight = 6;
    }
    else if (type == EnumEnemyType.FOLLOW)
    { skillEnemyFollow += 0.2;
      if (skillEnemyFollow > 6) skillEnemyFollow = 6;
    }
  }
  
  
  public void crashedIntoEnemy(EnumEnemyType type)
  {
    if (type == EnumEnemyType.STRAIGHT)
    { skillEnemyStraight -= 1.2;
      if (skillEnemyStraight < 1) skillEnemyStraight = 1;
      //System.out.println("crashedIntoEnemy(): skillEnemyStraight="+skillEnemyStraight);
    }
    else if (type == EnumEnemyType.FOLLOW)
    { skillEnemyFollow -= 1.2;
      if (skillEnemyFollow < 1) skillEnemyFollow = 1;
      //System.out.println("crashedIntoEnemy(): skillEnemyStraight="+skillEnemyStraight);
    }
  }
  
  
  public void enemyKilled()
  {
    gameScore += Library.ENEMY_POINTS;
  }

  public PowerUp getPowerUp()
  {
    return powerUp;
  }

  public void setPowerUp(PowerUp powerUp)
  {
    this.powerUp = powerUp;
  }

  public boolean getInvulnerable()
  {
    return invulnerable;
  }

  public void setInvulnerable(boolean invulnerable)
  {
    this.invulnerable = invulnerable;
  }

  public int getTotalCoinsEarnedThisGame()
  {
    return gameCoinsEarned;
  }

  public int getScore()
  {
    return (int) gameScore;
  }

  public void resetCollisionCountInCurrentChunk()
  {
    wallCollisionCountInCurrentChunk = 0;
  }

  public int getCollisionCountInCurrentChunk()
  {
    return wallCollisionCountInCurrentChunk;
  }

  public void addScore(double score)
  {
    gameScore += score;
  }
  
  public int getMaxEnemy(EnumEnemyType type)
  {
    if (type == EnumEnemyType.STRAIGHT) return (int)skillEnemyStraight;
    else if (type == EnumEnemyType.FOLLOW) return (int)skillEnemyFollow;
    return 1;
  }
  

  public void render(Graphics2D canvas)
  {
    int xx = Library.worldPosXToScreen(getX());
    int yy = Library.worldPosYToScreen(getY());
    canvas.drawImage(image, xx, yy, null);

    if (health < Library.HEALTH_MAX/10) canvas.drawImage(Library.getSprites().get("pDmg2"), xx, yy, null);

    else if (health < Library.HEALTH_MAX/2) canvas.drawImage(Library.getSprites().get("pDmg1"), xx,  yy, null);

    if (sparkList != null)
    {
      for (int i = 0; i < sparkList.size(); i++)
      {
        Spark spark = sparkList.get(i);
        spark.render(canvas);
      }
    }
  }

  public String toString()
  {
    return "Player: (" + getX() + "," + getY() + ")";
  }

}
