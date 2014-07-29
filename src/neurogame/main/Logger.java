package neurogame.main;

import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import neurogame.gameplay.Star;
import neurogame.gameplay.Enemy;
import neurogame.gameplay.GameObject;
import neurogame.gameplay.GameObjectType;
import neurogame.gameplay.Player;
import neurogame.level.PathVertex;
import neurogame.level.World;
public class Logger
{
 
  private static final String LOG_PREFIX = "NGLog_";
  private static final String LOG_EXTENSION = ".txt";
  private static final String PATH = "logs/";
  
  private static final SimpleDateFormat FILE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd.HH-mm-ss.SSS");
  private static final String FLOAT4 = "%.4f";

  private File logFile;
  private BufferedWriter writer;
  
  private Long time0;
  

  /**
   * Instantiate a new Logger with the default file path.
   * 
   * @param initTime
   *          String containing the time stamp of initialization for NeuroGame,
   *          as acquired by Library.timeStamp().
   */
  public Logger()
  {
    String fileName = generateFileName();
    
    logFile = new File(PATH, fileName);
    logFile.getParentFile().mkdir();
    
    time0 = System.currentTimeMillis();
    
    String out = "Milliseconds, PlayerX, PlayerY, Health, Collision, WallAbove, WallBelow, ";
    for (int i=0; i<Enemy.MAX_ENEMY_COUNT; i++)
    { out += "Enemy" + i + "X, Enemy" + i + "Y, ";
    }
    for (int i=0; i<Star.MAX_STAR_COUNT; i++)
    { out += "Star" + i + "X, Star" + i + "Y, ";
    }
    
    out += "PowerUpX, PowerUpY\n" + time0 + "\n"; 

    
    try
    {
      writer = new BufferedWriter(new FileWriter(logFile));
      writer.write(out);
    }
    catch (IOException ex)
    {  ex.printStackTrace();
       System.exit(0);
    }
  }


  private String generateFileName()
  {
    return LOG_PREFIX + FILE_DATE_FORMAT.format(new Date()) + LOG_EXTENSION;
  }
  
  public void update(World world)
  {
    
    Player player = world.getPlayer();
    GameObject powerUp = null;
    Enemy[] enemyList = new Enemy[Enemy.MAX_ENEMY_COUNT];
    Star[] starList = new Star[Star.MAX_STAR_COUNT];
    
    ArrayList<GameObject> objectList = world.getObjectList();
    
    for (GameObject obj : objectList) 
    {
      GameObjectType type = obj.getType();
      if (type.isEnemy())
      {
        Enemy enemy = (Enemy)obj;
        enemyList[enemy.getEnemyIdx()] = enemy;
      }
      if (type == GameObjectType.STAR)
      {
        Star star =  (Star)obj;
        starList[star.getStarIdx()] = star;
      }
      
      else if (type == GameObjectType.POWER_UP) powerUp = obj;
    }
    
    
    String out = Long.toString(System.currentTimeMillis()-time0) +  
        String.format("," + FLOAT4 + "," + FLOAT4  + ","+player.getHealth()+",0,",
        player.getCenterX(), player.getCenterY(), player.getHealth() );
    
    PathVertex vertex = world.getInterpolatedWallTopAndBottom(player.getCenterX());
    
    out += String.format("," + FLOAT4 + "," + FLOAT4  + ",", vertex.getTopY(), vertex.getBottomY());
    
    for (int i=0; i<Enemy.MAX_ENEMY_COUNT; i++)
    { 
      if (enemyList[i] == null) out += "0,0,";
      else
      { 
        out += String.format("," + FLOAT4 + "," + FLOAT4  + ",", enemyList[i].getCenterX(), enemyList[i].getCenterY());
      }
    }
    for (int i=0; i<Star.MAX_STAR_COUNT; i++)
    { if (starList[i] == null) out += "0,0,";
      else out += String.format("," + FLOAT4 + "," + FLOAT4  + ",", starList[i].getCenterX(), starList[i].getCenterY());
    }
    if (powerUp == null) out += "0,0\n";
    else out += String.format("," + FLOAT4 + "," + FLOAT4  + "\n", powerUp.getCenterX(), powerUp.getCenterY());
    try
    {
      writer.write(out);
    }
    catch (IOException ex)
    {  ex.printStackTrace();
    }
  }
  
  public void closeLog()
  {
    try
    {
      writer.close();
    }
    catch (IOException e) { }
  }

}
