package neurogame.gameplay;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;

import neurogame.level.EnumChunkType;
import neurogame.level.World;
import neurogame.library.Library;
import neurogame.library.QuickSet;
import neurogame.io.InputController;

/**
 * @author Daniel
 * 
 */
public class Player extends GameObject
{

  private static Image image = Library.getSprites().get(GameObjectType.PLAYER.getName());
  public static final int MAX_AMMO_COUNT = 20;
  private int ammoCount;
  private boolean triggerReleasedSinceLastMissile;
  public boolean triggerPressed = false;

  private boolean invulnerable = false;
  
  private int collisionLogBitsThisUpdate;
  public static final int COLLISION_BITS_WALL_ABOVE = 1;
  public static final int COLLISION_BITS_WALL_BELOW = 2;
  public static final int COLLISION_BITS_ENEMY = 4;
  public static final int COLLISION_BITS_STAR = 8;
  public static final int COLLISION_BITS_AMMO = 16;
  public static final int COLLISION_FLAG_MISSILE_HIT_ENEMY = 32;
 
  private double timeOfLastWallCollision;
  private double gameScore;
  private double gameTotalSeconds;
  
  private double skillEnemyStraight;
  private double skillEnemyFollow;
  private double skillEnemySinusoidal;
  private double skillEnemyZapper;

  private QuickSet<Spark> sparkList;

  private DirectionVector directionVector = new DirectionVector();
  private double lastVelocityX, lastVelocityY;
  
  private double health;

  public Player(double x, double y, World world)
  {
    super(GameObjectType.PLAYER, x, y, world);
    
    final int hitPixel_x1 = 8;
    final int hitPixel_x2 = 43;
    final int hitPixel_y1 = 0;
    final int hitPixel_y2 = 90;
    overrideDefaultHitBoxInPixels(75, 99, hitPixel_x1, hitPixel_y1, hitPixel_x2, hitPixel_y2);
    initGame();
  }

  public void initGame()
  {
    health = Library.HEALTH_MAX;
    gameScore = 0;
    gameTotalSeconds = 0;
    timeOfLastWallCollision = 0;
    triggerReleasedSinceLastMissile = true;
    
    ammoCount = 10;
    
    //missileCurrentCooldown = 0;
    
    //skillProbabilitySpawnCoinPerSec = (Star.MIN_PROBALITY_SPAWN_PER_SEC + Star.MAX_PROBALITY_SPAWN_PER_SEC)/2.0;

    lastVelocityX = 0;
    lastVelocityY = 0;
    
    skillEnemyStraight = 1;
    skillEnemyFollow = 1;
    skillEnemySinusoidal = 1;
    skillEnemyZapper = 1;
    
  }

  /**
   * Update method for the player. This will be called on every frame.
   * 
   * @param dir
   *          Direction to move the player obj, if any.
   */
  public void update(double deltaSec, double scrollDistance)
  {
    //System.out.println("Player.update("+deltaSec+")");
    gameTotalSeconds += deltaSec;
    //if (missileCurrentCooldown > 0) missileCurrentCooldown -= deltaSec;
    
    collisionLogBitsThisUpdate = 0;

    updatePlayerInputDirection();
    
    double inputSpeed = directionVector.getAcceleration();
    double maxSpeed = GameObjectType.PLAYER.getMaxSpeed();
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
      if (collisionLocation == EnumCollisionType.WALL_TOP) collisionLogBitsThisUpdate |= COLLISION_BITS_WALL_ABOVE;
      if (collisionLocation == EnumCollisionType.WALL_BOTTOM) collisionLogBitsThisUpdate |= COLLISION_BITS_WALL_BELOW;
      
      
      lastVelocityX = 0;
      lastVelocityY = -lastVelocityY;
      
      if (gameTotalSeconds - timeOfLastWallCollision > 0.25)
      {
        loseHealth(getX(), getY(), Library.DAMAGE_PER_WALL_HIT);
        timeOfLastWallCollision = gameTotalSeconds;
      }
      
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
      { //System.out.println("Spark move:" + i );
        Spark spark = sparkList.get(i);
        boolean alive = spark.update();
        if (!alive) 
        { sparkList.remove(i);
          //System.out.println("    Kill spark idx = " + i + ", new size="+sparkList.size());
        }
      }
    }
    
    if (InputController.isPlayerPressingButton()) shootMissile();
    else { triggerReleasedSinceLastMissile = true;}
    
  }
  
  
  public void hit(GameObject obj)
  {
    GameObjectType type = obj.getType();
    if (type == GameObjectType.STAR) collectStar(obj);
    else if (type.isEnemy()) crashedIntoEnemy(obj);
    else if (type == GameObjectType.AMMO)
    { addMissileCount(obj, 10);
    }
  }
  
  public void addMissileCount(GameObject ammoBox, int count)
  {
    collisionLogBitsThisUpdate |= COLLISION_BITS_AMMO;
    
    gameScore += Library.SCORE_AMMOBOX;
    InfoMessage scoreInfo = new InfoMessage(ammoBox.getCenterX(), ammoBox.getCenterY(), world, String.valueOf(Library.SCORE_AMMOBOX));
    world.addGameObject(scoreInfo);
    
    ammoCount+=count;
    if (ammoCount > MAX_AMMO_COUNT) ammoCount = MAX_AMMO_COUNT;
  }
  
  public int getAmmoCount() {return ammoCount;}
  

  private void updatePlayerInputDirection()
  {
    DirectionVector inputDir = InputController.getPlayerInputDirectionVector();
    directionVector.x = lastVelocityX * 0.25 + inputDir.x;
    directionVector.y = lastVelocityY * 0.25 + inputDir.y;
    
  }
  
  
  
  

  public void loseHealth(double hitX, double hitY, double damage)
  {
    if (invulnerable) return;
    
    health -= damage;
    if (health < 0) health = 0;

    
//    skillProbabilitySpawnCoinPerSec += 0.05;
//    if (skillProbabilitySpawnCoinPerSec > Star.MAX_PROBALITY_SPAWN_PER_SEC)
//    { skillProbabilitySpawnCoinPerSec = Star.MAX_PROBALITY_SPAWN_PER_SEC;
//    }
    

    int sparkCount = Library.RANDOM.nextInt(20) + Library.RANDOM.nextInt(20)
        + Library.RANDOM.nextInt(20) + 25;
    sparkList = new QuickSet<Spark>(sparkCount);
    for (int i = 0; i < sparkCount; i++)
    {
      sparkList.add(new Spark(hitX, hitY, world));
    }
  }

  public void collectStar(GameObject star)
  {
    collisionLogBitsThisUpdate |= COLLISION_BITS_STAR;
    
    health += Library.HEALTH_PER_STAR;
    if (health > Library.HEALTH_MAX) health = Library.HEALTH_MAX;
    gameScore += Library.SCORE_STAR;
    InfoMessage scoreInfo = new InfoMessage(star.getCenterX(), star.getCenterY(), world, String.valueOf(Library.SCORE_STAR));
    world.addGameObject(scoreInfo);
    
    
//    skillProbabilitySpawnCoinPerSec -= 0.005;
//    if (health > Library.HEALTH_MAX) 
//    { health = Library.HEALTH_MAX;
//      skillProbabilitySpawnCoinPerSec -= 0.025;
//    }
//    if (skillProbabilitySpawnCoinPerSec < Star.MIN_PROBALITY_SPAWN_PER_SEC)
//    { skillProbabilitySpawnCoinPerSec = Star.MIN_PROBALITY_SPAWN_PER_SEC;
//    }
  }


  public void killedOrAvoidedEnemy(GameObject obj, boolean shotWithMissle)
  {
    if (!isAlive()) return;
    GameObjectType type = obj.getType();
    
    EnumChunkType pathType = world.getRightChunkType();
   
    
    double pathHeightBonus = 1.0 + 5*Math.max(0, pathType.getDefaultOpeningHeight() - world.getSkillBasedChunkGapHeight());
    //System.out.println("Player.killedOrAvoidedEnemy() pathHeightBonus = " + pathHeightBonus);
    
    
    if (shotWithMissle) 
    { int score = (int)(Library.ENEMY_POINTS *pathHeightBonus);
    
      gameScore += score;
    
   
      InfoMessage scoreInfo = new InfoMessage(obj.getCenterX(), obj.getCenterY(), world, String.valueOf(score));
      world.addGameObject(scoreInfo);
    }
    //System.out.println("    obj ("+ obj.getCenterX() +", " + obj.getCenterY() +")  worldLeft="+Library.leftEdgeOfWorld);
    
    
    if (type == GameObjectType.ENEMY_STRAIGHT)
    { skillEnemyStraight += 0.2;
      if (skillEnemyStraight > Enemy.MAX_ENEMY_COUNT) skillEnemyStraight = Enemy.MAX_ENEMY_COUNT;
    }
    else if (type == GameObjectType.ENEMY_FOLLOW)
    { skillEnemyFollow += 0.2;
      if (skillEnemyFollow > Enemy.MAX_ENEMY_COUNT) skillEnemyFollow = Enemy.MAX_ENEMY_COUNT;
    }
    else if (type == GameObjectType.ENEMY_SINUSOIDAL)
    { skillEnemySinusoidal += 0.2;
      if (skillEnemySinusoidal > Enemy.MAX_ENEMY_COUNT) skillEnemySinusoidal = Enemy.MAX_ENEMY_COUNT;
    }
    else if (type == GameObjectType.ZAPPER){
    	skillEnemyZapper += 0.5;
    	if (skillEnemyZapper > Enemy.MAX_ENEMY_COUNT) skillEnemyZapper = Enemy.MAX_ENEMY_COUNT;
    }
  }
  
  
  public void crashedIntoEnemy(GameObject obj)
  {
    collisionLogBitsThisUpdate |= COLLISION_BITS_ENEMY;
    
    double hitX = (getCenterX() + obj.getCenterX()) / 2.0;
    double hitY = (getCenterY() + obj.getCenterY()) / 2.0;

    GameObjectType type = obj.getType();
    loseHealth(hitX, hitY, type.getHitDamage());
    
    if (type == GameObjectType.ENEMY_STRAIGHT)
    { skillEnemyStraight -= 1.2;
      if (skillEnemyStraight < 1) skillEnemyStraight = 1;
    }
    else if (type == GameObjectType.ENEMY_FOLLOW)
    { skillEnemyFollow -= 1.2;
      if (skillEnemyFollow < 1) skillEnemyFollow = 1;
    }
    else if (type == GameObjectType.ENEMY_SINUSOIDAL)
    { skillEnemySinusoidal -= 1.2;
      if (skillEnemySinusoidal < 1) skillEnemySinusoidal = 1;
    }
    else if (type == GameObjectType.ZAPPER){
    	skillEnemyZapper -= 0.5;
    	if (skillEnemyZapper < 1) skillEnemyZapper= 1;
    }
  }
  


  public void die(boolean showDeathEffect)
  { 
    isAlive = false;
  }
  
  private void shootMissile()
  {
    if (!triggerReleasedSinceLastMissile) return;
    triggerPressed = true;
    if ((Missile.getCurrentMissile() != null) && (Missile.getCurrentMissile().isAlive())) return;
    
    triggerReleasedSinceLastMissile = false;
   
    //if (missileCurrentCooldown > 0) return;
    //System.out.println("Player.shootMissile()   missileCount=" + missileCount);
    if (ammoCount < 1) return;
    
    ammoCount--;
    //missileCurrentCooldown = MISSILE_COOLDOWN_SECONDS;
    world.addGameObject(new Missile(getX()+getWidth(), getCenterY(), world));
  }
  

  public boolean getInvulnerable()
  {
    return invulnerable;
  }

  public void setInvulnerable(boolean invulnerable)
  {
    this.invulnerable = invulnerable;
  }


  public int getScore()
  {
    return (int) gameScore;
  }


  public void addScore(double score)
  {
    gameScore += score;
  }
  
  public int getMaxEnemy(GameObjectType enemytype)
  {
    if (enemytype == GameObjectType.ENEMY_STRAIGHT) return (int)skillEnemyStraight;
    else if (enemytype == GameObjectType.ENEMY_FOLLOW) return (int)skillEnemyFollow;
    else if (enemytype == GameObjectType.ENEMY_SINUSOIDAL) return (int)skillEnemySinusoidal;
    else if (enemytype == GameObjectType.ZAPPER) return (int)skillEnemyZapper;
    return 1;
  }
  
  public int getMaxEnemy(EnumChunkType chunkType)
  {
    if (chunkType.getEnemyType() == GameObjectType.ENEMY_STRAIGHT) return (int)skillEnemyStraight;
    else if (chunkType.getEnemyType() == GameObjectType.ENEMY_FOLLOW) return (int)skillEnemyFollow;
    else if (chunkType.getEnemyType() == GameObjectType.ENEMY_SINUSOIDAL) return (int)skillEnemySinusoidal;
    else if (chunkType.getEnemyType() == GameObjectType.ZAPPER) return (int)skillEnemyZapper;
    return 1;
  }
  

  public int getHealth() {return (int)(health);}
  
  public int getCollisionLogBitsThisUpdate() {return collisionLogBitsThisUpdate;}
  
  public void render(Graphics2D canvas)
  {
    //System.out.println("Player.render()");
    int xx = Library.worldPosXToScreen(getX());
    int yy = Library.worldPosYToScreen(getY());
    canvas.drawImage(image, xx, yy, null);

    if (health < Library.HEALTH_MAX/8) canvas.drawImage(Library.getSprites().get("pDmg2"), xx, yy, null);

    else if (health < Library.HEALTH_MAX/2) canvas.drawImage(Library.getSprites().get("pDmg1"), xx,  yy, null);
    
    if (Library.DEBUG_SHOW_HITBOXES)
    { int x1 = Library.worldPosXToScreen(getHitMinX());
      int y1 = Library.worldPosYToScreen(getHitMinY());
      int x2 = Library.worldPosXToScreen(getHitMaxX());
      int y2 = Library.worldPosYToScreen(getHitMaxY());
      canvas.setColor(Color.RED);
      canvas.drawRect(x1,y1, x2-x1, y2-y1);
    }
    
   

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
