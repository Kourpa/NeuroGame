package neurogame.gameplay;

import java.awt.Graphics2D;
import java.awt.Image;

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

  private int collisionTimer;

  private double score;

  private int coins;
  private int maxCoins;
  
  private QuickSet<Spark> sparkList;
  
  private DirectionVector directionVector = new DirectionVector();
  private double lastVelocityX, lastVelocityY;

  public Player(double x, double y, double width, double height, World world)
  {
    super(x, y, width, height, name, image, world);

    health = Library.HEALTH_MAX;
    collisionTimer= 0;
    maxSpeed = 0.0005f;

    score = 0;
    coins = 0;
    maxCoins = 0;
    lastVelocityX = 0;
    lastVelocityY = 0;
    
    setIsVisible(true);
  }

  /**
   * Update method for the player. This will be called on every frame.
   * 
   * @param dir
   *          Direction to move the player obj, if any.
   */
  public void update(long deltaTime)
  {
    System.out.println(this);
    updateColTimer();
    
    
    double speed = directionVector.getSpeed();
    if (speed > maxSpeed) speed = maxSpeed;
    
    double velocityX = lastVelocityX*0.9 + directionVector.x * speed;
    double velocityY = lastVelocityY*0.9 + directionVector.y * speed;
    
    lastVelocityY = directionVector.y * speed;
    //System.out.println(directionVector);
    
    speed = Math.sqrt(velocityX*velocityX + velocityY*velocityY);
    if (speed > maxSpeed) speed = maxSpeed;
    
    
    move(deltaTime, speed, velocityX, velocityY);
    
    lastVelocityX = velocityX;
    lastVelocityY = velocityY;
    
    
    if (wallCollision())
    {  System.out.println("wallCollision()=true");
       lastVelocityX = 0;
       lastVelocityY = -lastVelocityY;
      loseHealth(getX(), getY(), Library.DAMAGE_PER_WALL_HIT);
      
      if (Math.abs(lastVelocityY) < 0.002) 
      { if (lastVelocityY >= 0) lastVelocityY = 0.002;
      
        else lastVelocityY = -0.002;
      }
      
      
      while (wallCollision())
      { 
        //System.out.println("Hit: (" + getX()+", "+getY()+") velY = "+velY);
        
        setLocation(getX()+lastVelocityX, getY()+lastVelocityY);

        if (getY() < Library.VERTICAL_MIN) lastVelocityY = 0.002;
        else if (getY()+getHeight() > Library.VERTICAL_MAX) lastVelocityY = -0.002;
        
      }
    }

    // if the player got pushed off the screen
    if (getX()+getWidth() < world.getDeltaX())
    {
      System.out.println("Die (off screen Left): " + getX()+getWidth() + "<" + world.getDeltaX());
      health = 0;
    }
    
    if (sparkList != null)
    { for (int i=0; i < sparkList.size(); i++)
      { Spark spark = sparkList.get(i);
        boolean alive = spark.move();
        if (!alive) sparkList.remove(i);
      }
    }
    
    
    
  }

  public void setDirection(DirectionVector directionVector)
  {

    
    this.directionVector.x = lastVelocityX * 0.25 + directionVector.x;
    this.directionVector.y = lastVelocityY * 0.25 + directionVector.y;
  }


  
  
//  public void move(double dx, double dy)
//  {
//    double x = getX()+dx;
//    if (x < world.getDeltaX()) x = world.getDeltaX();
//    setLocation(x, getY()+dy);
//  }

  public void updateColTimer()
  {
    if (collisionTimer > 0) collisionTimer --;
  }

  public void loseHealth(double hitX, double hitY, int damage)
  {
    if (collisionTimer > 0 || invulnerable) return;
    
    health -= damage;
    if (health < 0) health = 0;
      
    collisionTimer = Library.INVULNERABLE_FRAMES;
      
    int sparkCount = Library.RANDOM.nextInt(20)+Library.RANDOM.nextInt(20)+Library.RANDOM.nextInt(20)+25;
    sparkList = new QuickSet<Spark>(sparkCount);
    for (int i=0; i<sparkCount; i++)
    { 
      sparkList.add(new Spark(hitX, hitY, world));
    }
  }

  public void collectCoin()
  {
    coins++;
    score += Library.COIN_POINTS;

    if (coins > maxCoins)
    {
      maxCoins = coins;
    }
    health += Library.HEALTH_PER_COIN;
    if (health > Library.HEALTH_MAX) health = Library.HEALTH_MAX;
  }

  public void collectPowerUp()
  {
    score += Library.POWERUP_POINTS;
  }

  public void enemyKilled()
  {
    score += Library.ENEMY_POINTS;
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

  public int getCoins()
  {
    return coins;
  }

  public int getMaxCoins()
  {
    return maxCoins;
  }

  public int getScore()
  {
    return (int)score;
  }
  
  public void zeroScore()
  {
    score = 0;
  }

  public void addScore(double score)
  {
    this.score += score;
  }


  public void render(Graphics2D g)
  {
    int xx = Library.worldToScreen(getX() - world.getDeltaX());
    int yy = Library.worldToScreen(getY());
    g.drawImage(image, xx, yy, null);

    if (health == 2) g.drawImage(Library.getSprites().get("pDmg1"), xx, yy,
        null);

    else if (health == 1) g.drawImage(Library.getSprites().get("pDmg2"), xx,
        yy, null);
    
    if (sparkList != null)
    { 
      for (int i=0; i < sparkList.size(); i++)
      { Spark spark = sparkList.get(i);
        spark.draw(g);
      }
    }
  }
  
  
  public String toString()
  {
    return "Player: ("+getX()+","+getY()+")";
  }

}
