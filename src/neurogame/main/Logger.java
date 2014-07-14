package neurogame.main;

import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import neurogame.gameplay.Coin;
import neurogame.gameplay.Enemy;
import neurogame.gameplay.GameObject;
import neurogame.gameplay.GameObjectType;
import neurogame.level.PathVertex;
import neurogame.level.World;
import neurogame.library.Library;
public class Logger
{
 
  private static final String LOG_PREFIX = "NGLog_";
  private static final String LOG_EXTENSION = ".txt";
  private static final String PATH = "logs/";
  
  private static final SimpleDateFormat FILE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd.HH-mm-ss.SSS");

  private File logFile;
  private BufferedWriter writer;
  
  private GameObject player, powerUp;
  private GameObject[] enemyList = new GameObject[Enemy.MAX_ENEMY_COUNT];
  private GameObject[] starList = new GameObject[Coin.MAX_STAR_COUNT];

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
    
    String out = "Milliseconds, PlayerX, PlayerY, Health, Collision, WallAbove, WallBelow, ";
    for (int i=0; i<Enemy.MAX_ENEMY_COUNT; i++)
    { out += "Enemy" + i + "X, Enemy" + i + "Y, ";
    }
    for (int i=0; i<Coin.MAX_STAR_COUNT; i++)
    { out += "Star" + i + "X, Star" + i + "Y, ";
    }
    out += "PowerUpX, PowerUpY\n"; 

    
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
    ArrayList<GameObject> objectList = world.getObjectList();
    int enemyCount = 0;
    int starCount = 0;
    powerUp = null;
    for (GameObject obj : objectList) 
    {
      GameObjectType type = obj.getType();
      if (type == GameObjectType.PLAYER) player = obj;
      else if (type.isEnemy())
      {
        enemyList[enemyCount] = obj;
        enemyCount++;
      }
      else if (type == GameObjectType.COIN)
      {
        starList[starCount] = obj;
        starCount++;
      }
      else if (type == GameObjectType.POWER_UP) powerUp = obj;
    }
    
    
    String out = Long.toString(System.currentTimeMillis()) + "," + 
        player.getCenterX() + "," + player.getCenterY() + ","+player.getHealth()+",0,";
    
    PathVertex vertex = world.getInterpolatedWallTopAndBottom(player.getCenterX());
    out += vertex.getTopY() + "," + vertex.getBottomY() + ",";
    
    for (int i=0; i<Enemy.MAX_ENEMY_COUNT; i++)
    { 
      if (i < enemyCount)
      { out += enemyList[i].getCenterX() + "," + enemyList[i].getCenterY() + ",";
      }
      else out += "0,0,";
    }
    for (int i=0; i<Coin.MAX_STAR_COUNT; i++)
    { if (i < starCount)
      { out += starList[i].getCenterX() + "," + starList[i].getCenterY() + ",";
      }
      else out += "0,0,";
    }
    if (powerUp != null) out += powerUp.getCenterX() + "," + powerUp.getCenterY() +"\n";
    else out += "0,0\n";

    
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
