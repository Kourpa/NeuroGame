package neurogame.io;

//Triggers: Oddball must be different codes than game tgriggers
//*Triggers on oddball: only on change.
//Triggers: collision, game start, game over
// Auto run/connect to parallel port
//Trigger: fire trigger whenever butten is pressed. When ememy explodes
// Remove points from ememys that escape, more bullits.
//  Hide options in options memu.
//Trigger whenever hit

//Joystick x=3, y=2

import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import neurogame.gameplay.Ammo;
import neurogame.gameplay.DirectionVector;
import neurogame.gameplay.Missile;
import neurogame.gameplay.Star;
import neurogame.gameplay.Enemy;
import neurogame.gameplay.Player;
import neurogame.level.PathVertex;
import neurogame.level.World;
import neurogame.library.Library;
import neurogame.io.InputController;

public class Logger
{

  private static final String LOG_PREFIX = "NGLog_";
  private static final String LOG_EXTENSION = ".csv";
  private static final String PATH = "logs/";
  private static final String PARALLEL_CONNECTION_PROGRAM = "ParallelPortTrigger/timer.exe";

  private static final SimpleDateFormat FILE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd.HH-mm-ss.SSS");
  private static final String FLOAT4 = "%.4f";

  private File logFile;
  private BufferedWriter writer;

  private Long time0;

  private SocketToParallelPort socket;
  private static final String HOST = "127.0.0.1";
  private static final int PORT = 55555;
  
  private int senttrigger;

  /**
   * Instantiate a new Logger with the default file path.
   * 
   * @param initTime
   *          String containing the time stamp of initialization for NeuroGame,
   *          as acquired by Library.timeStamp().
   */
  public Logger()
  {
	  senttrigger = 0;
    String fileName = generateFileName();

    logFile = new File(PATH, fileName);
    logFile.getParentFile().mkdir();

    time0 = System.currentTimeMillis();

    String out = "Milliseconds, PlayerX, PlayerY, Health, Ammo, JoystickX, JoystickY, JoystickButton, Collision, WallAbove, WallBelow, ";
    for (int i = 0; i < Enemy.MAX_ENEMY_COUNT; i++)
    {
      out += "Enemy" + i + "X, Enemy" + i + "Y, ";
    }
    for (int i = 0; i < Star.MAX_STAR_COUNT; i++)
    {
      out += "Star" + i + "X, Star" + i + "Y, ";
    }

    out += "AmmoBoxX, AmmoBoxY, MissileX, MissileY\n" + time0 + "\n";

    try
    {
      writer = new BufferedWriter(new FileWriter(logFile));
      writer.write(out);
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
      System.exit(0);
    }

    try
    {
      Runtime.getRuntime().exec(PARALLEL_CONNECTION_PROGRAM);
    }
    catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    socket = new SocketToParallelPort(HOST, PORT);

  }

  public void sendByteBySocket(byte data)
  {
    socket.sendByte(data);
  }

  private String generateFileName()
  {
    return LOG_PREFIX + FILE_DATE_FORMAT.format(new Date()) + LOG_EXTENSION;
  }

  public void startGame()
  {
	  senttrigger = 5;
    if (socket != null)
    {
      socket.sendByte(SocketToParallelPort.TRIGGER_GAME_START);
    }
  }

  public void update(World world)
  {
	  
	if (socket != null)
	{
	  if (senttrigger > 0)
	  {
		if (senttrigger == 1) {
		  socket.sendByte(SocketToParallelPort.TRIGGER_SIGNAL_GROUND);
		senttrigger = 0;
		}
		else senttrigger--;
		
	  }
	}
	
    Player player = world.getPlayer();
    Enemy[] enemyList = Enemy.getEnemyList();
    Star[] starList = Star.getStarList();
    Ammo ammo = Ammo.getCurrentAmmoBox();
    Missile missile = Missile.getCurrentMissile();
    double health = (double) (player.getHealth()) / Library.HEALTH_MAX;
    int joystickButton = 0;
    if (InputController.isPlayerPressingButton()) joystickButton = 1;

    int collisionBits = player.getCollisionLogBitsThisUpdate();
    //System.out.println(socket);
    if (socket != null)
    {
      if (joystickButton == 1)
       { socket.sendByte(SocketToParallelPort.TRIGGER_GAME_SHOOT_BUTTON);
       senttrigger =1;
       }
      else if ((collisionBits & (Player.COLLISION_BITS_WALL_ABOVE | Player.COLLISION_BITS_WALL_BELOW)) > 0)
      {
        socket.sendByte(SocketToParallelPort.TRIGGER_GAME_PLAYER_CRASH_WALL);
        senttrigger =5;
      }
      else if ((collisionBits & Player.COLLISION_BITS_ENEMY) > 0)
      {
        socket.sendByte(SocketToParallelPort.TRIGGER_GAME_PLAYER_CRASH_ENEMY);
        senttrigger =5;
      }
      else if ((collisionBits & Player.COLLISION_BITS_STAR) > 0)
      {
        socket.sendByte(SocketToParallelPort.TRIGGER_GAME_COLLECT_STAR);
        senttrigger =5;
      }
      else if ((collisionBits & Player.COLLISION_BITS_AMMO) > 0)
      {
        socket.sendByte(SocketToParallelPort.TRIGGER_GAME_COLLECT_AMMO);
        senttrigger =5;
      }
      else if ((collisionBits & Player.COLLISION_FLAG_MISSILE_HIT_ENEMY) > 0)
      {
        socket.sendByte(SocketToParallelPort.TRIGGER_GAME_MISSILE_HIT_ENEMY);
        senttrigger =5;
      }
    }

    DirectionVector joystickVector = InputController.getPlayerInputDirectionVector();

    String out = Long.toString(System.currentTimeMillis() - time0)
        + String.format("," + FLOAT4 + "," + FLOAT4 + "," + FLOAT4 + ",%d," + FLOAT4 + "," + FLOAT4 + ",%d,%d,",
            player.getCenterX(), player.getCenterY(), health, player.getAmmoCount(), joystickVector.x,
            joystickVector.y, joystickButton, collisionBits);

    //System.out.println(out);
    PathVertex vertex = world.getInterpolatedWallTopAndBottom(player.getX() + player.getWidth());

    // System.out.println("Logger(): vertex.getTop()="+vertex.getTop()+", vertex.getBottom()="+
    // vertex.getBottom());

    double proximityTop = Math.min(1.0, (1.0 - (player.getY() - vertex.getTop())));
    double proximityBot = Math.min(1.0, (1.0 - (vertex.getBottom() - (player.getY() + player.getHeight()))));

    if ((collisionBits & Player.COLLISION_BITS_WALL_ABOVE) > 0) proximityTop = 1.0;
    if ((collisionBits & Player.COLLISION_BITS_WALL_BELOW) > 0) proximityBot = 1.0;

    out += String.format(FLOAT4 + "," + FLOAT4 + ",", proximityTop, proximityBot);

    for (int i = 0; i < Enemy.MAX_ENEMY_COUNT; i++)
    {
      if ((enemyList[i] != null) && (enemyList[i].isAlive()))
      {
        out += String.format(FLOAT4 + "," + FLOAT4 + ",", enemyList[i].getCenterX(), enemyList[i].getCenterY());
      }
      else out += "0,0,";
    }
    for (int i = 0; i < Star.MAX_STAR_COUNT; i++)
    {
      if ((starList[i] != null) && (starList[i].isAlive()))
      {
        out += String.format(FLOAT4 + "," + FLOAT4 + ",", starList[i].getCenterX(), starList[i].getCenterY());
      }
      else out += "0,0,";
    }
    if ((ammo != null) && (ammo.isAlive()))
    {
      out += String.format(FLOAT4 + "," + FLOAT4, ammo.getCenterX(), ammo.getCenterY());
    }
    else out += "0,0,";

    if ((missile != null) && (missile.isAlive()))
    {
      out += String.format(FLOAT4 + "," + FLOAT4 + "\n", missile.getCenterX(), missile.getCenterY());
    }
    else out += "0,0\n";

    try
    {
      writer.write(out);
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
    }

  }

  public void closeLog()
  {
    if (socket != null)
    {
    try
    {
      socket.sendByte(SocketToParallelPort.TRIGGER_GAME_OVER);
      socket.close();
      writer.close();
    }
    catch (IOException e)
    {}
    }
  }

}
