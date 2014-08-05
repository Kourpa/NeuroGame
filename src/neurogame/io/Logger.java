package neurogame.io;

import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import neurogame.gameplay.Ammo;
import neurogame.gameplay.Star;
import neurogame.gameplay.Enemy;
import neurogame.gameplay.GameObject;
import neurogame.gameplay.GameObjectType;
import neurogame.gameplay.Player;
import neurogame.level.PathVertex;
import neurogame.level.World;
import neurogame.library.Library;
public class Logger
{
 
  private static final String LOG_PREFIX = "NGLog_";
  private static final String LOG_EXTENSION = ".csv";
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
    
    out += "AmmoBoxX, AmmoBoxY\n" + time0 + "\n"; 

    
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
    Enemy[] enemyList = Enemy.getEnemyList();
    Star[] starList = Star.getStarList();
    Ammo ammo = Ammo.getCurrentAmmoBox();
    
    int collisionBits = player.getCollisionLogBitsThisUpdate();
    
    String out = Long.toString(System.currentTimeMillis()-time0) +  
        String.format("," + FLOAT4 + "," + FLOAT4  + ","+ FLOAT4 +",%d,",
        player.getCenterX(), player.getCenterY(), (double)(player.getHealth())/Library.HEALTH_MAX, collisionBits );
    
    PathVertex vertex = world.getInterpolatedWallTopAndBottom(player.getX()+player.getWidth());
    
    //System.out.println("Logger(): vertex.getTop()="+vertex.getTop()+", vertex.getBottom()="+ vertex.getBottom());
    
    double proximityTop = Math.min(1.0,  (1.0 - (player.getY() - vertex.getTop())));
    double proximityBot = Math.min(1.0,  (1.0 - (vertex.getBottom() - (player.getY() + player.getHeight()))));
    
    if ((collisionBits & Player.COLLISION_BITS_WALL_ABOVE) > 0) proximityTop = 1.0;
    if ((collisionBits & Player.COLLISION_BITS_WALL_BELOW) > 0) proximityBot = 1.0;
    

    
    out += String.format(FLOAT4 + "," + FLOAT4  + ",", proximityTop, proximityBot);
    
    for (int i=0; i<Enemy.MAX_ENEMY_COUNT; i++)
    { 
      if ((enemyList[i] != null) && (enemyList[i].isAlive()))
      { 
        out += String.format("," + FLOAT4 + "," + FLOAT4  + ",", enemyList[i].getCenterX(), enemyList[i].getCenterY());
      }
      else out += "0,0,";
    }
    for (int i=0; i<Star.MAX_STAR_COUNT; i++)
    { if ((starList[i] != null) && (starList[i].isAlive())) 
      {
        out += String.format("," + FLOAT4 + "," + FLOAT4  + ",", starList[i].getCenterX(), starList[i].getCenterY());
      }
      else out += "0,0,";
    }
    if ((ammo != null) && (ammo.isAlive())) 
    { out += String.format("," + FLOAT4 + "," + FLOAT4  + "\n", ammo.getCenterX(), ammo.getCenterY());
    }
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
