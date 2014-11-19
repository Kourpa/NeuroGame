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
import neurogame.gameplay.GameObject;
import neurogame.gameplay.Missile;
import neurogame.gameplay.Star;
import neurogame.gameplay.Enemy;
import neurogame.gameplay.Player;
import neurogame.level.PathVertex;
import neurogame.level.World;
import neurogame.library.Library;
import neurogame.main.NeuroGame;
import neurogame.io.InputController;

public class Logger
{
  private static final String LOG_PREFIX = "AxonGameLog_";
  private static final String LOG_EXTENSION = ".csv";
  private static final String PATH = "logs/";
  private static final String PARALLEL_CONNECTION_PROGRAM = "ParallelPortTrigger/timer.exe";

  private static final SimpleDateFormat FILE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd.HH-mm");
  
  private User user;
  private InputController controller;

  private File logFile;
  private BufferedWriter writer;

  private SocketToParallelPort socket;
  private static final String HOST = "127.0.0.1";
  private static final int PORT = 55555;
  
  private byte socketByteLast;
  private byte socketByteSend;
  
  private double startSec;

  
  /**
   * Instantiate a new Logger with the default file path.
   * 
   * @param initTime
   *          String containing the time stamp of initialization for NeuroGame,
   *          as acquired by Library.timeStamp().
   */
  public Logger(InputController controller, User user)
  {
    this.user = user;
    this.controller =  controller;
    
    String fileName = generateFileName();

    logFile = new File(PATH, fileName);
    logFile.getParentFile().mkdir();

    //Enemy Type:
    // NONE = 0
    // ENEMY_STRAIGHT = 1;
    // ENEMY_FOLLOW   = 2;
    // ENEMY_SINUSOIDAL = 3;
    // ZAPPER = 4;
    
    String out = "Seconds, PlayerX, PlayerY, Health, Ammo, JoystickX, JoystickY, JoystickButton, Trigger, WallAbove, WallBelow, ";
    for (int i = 0; i < Enemy.MAX_ENEMY_COUNT; i++)
    {
      int n = i+1;
      out += "Enemy " + n +" Type,Enemy " + n +" Proximity,Enemy " + n +" Angle, ";
    }
    for (int i = 0; i < Star.MAX_STAR_COUNT; i++)
    {
      int n = i+1;
      out += "Star " + n +" Proximity, Star " + n +" Angle, ";
    }

    SimpleDateFormat dateFormat = new SimpleDateFormat ("EEEE: MMMM d yyyy 'at' h:mm:ss a zzz");

    Date curDate = new Date();
    startSec = System.nanoTime()*NeuroGame.NANO_TO_SEC;
    out += "AmmoProximity, AmmoAngle, MissileX, MissileY\nStart Date/Time: " + dateFormat.format(curDate) + "\n";

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
    socketByteLast = -1;
    socketByteSend = SocketToParallelPort.TRIGGER_SIGNAL_GROUND;
    updateSocket();

  }



  private String generateFileName()
  {
    return LOG_PREFIX + user.getName() + '_' + FILE_DATE_FORMAT.format(new Date()) + LOG_EXTENSION;
  }

  public void startGame()
  {
    socketByteSend = SocketToParallelPort.TRIGGER_GAME_START;
    updateSocket();

  }

  public void update(World world, double currentSec)
  {
    double gameSec = currentSec - startSec;
    Player player = world.getPlayer();
    Enemy[] enemyList = Enemy.getEnemyList();
    Star[] starList = Star.getStarList();
    Ammo ammo = Ammo.getCurrentAmmoBox();
    Missile missile = Missile.getCurrentMissile();
    double health = (double) (player.getHealth()) / Library.HEALTH_MAX;
    int joystickButton = 0;
    if (controller.isPlayerPressingButton()) joystickButton = 1;
    DirectionVector joystickVector = controller.getPlayerInputDirectionVector();
    
    //System.out.println(out);
    PathVertex vertex = world.getInterpolatedWallTopAndBottom(player.getX() + player.getWidth());

    // System.out.println("Logger(): vertex.getTop()="+vertex.getTop()+", vertex.getBottom()="+
    // vertex.getBottom());

    String out = String.format("%.4f,%.3f,%.3f", gameSec, player.getCenterX(), player.getCenterY());
    
    out += String.format(",%.2f,%d", health, player.getAmmoCount()); 
    
    out += String.format(",%.3f,%.3f,%d",joystickVector.x,joystickVector.y, joystickButton);
    
    int collisionBits = player.getCollisionLogBitsThisUpdate();
    

    double proximityTop = Math.min(1.0, (1.0 - (player.getY() - vertex.getTop())));
    double proximityBot = Math.min(1.0, (1.0 - (vertex.getBottom() - (player.getY() + player.getHeight()))));
   

    if ((collisionBits & Player.COLLISION_BITS_ENEMY) > 0)
    {
      socketByteSend = SocketToParallelPort.TRIGGER_GAME_PLAYER_CRASH_ENEMY;
    }
    
    else if ((collisionBits & Player.COLLISION_BITS_STAR) > 0)
    {
      socketByteSend = SocketToParallelPort.TRIGGER_GAME_COLLECT_STAR;
    }
      
    else if ((collisionBits & Player.COLLISION_BITS_WALL_ABOVE) > 0)
    {
      proximityTop = 1.0;
      socketByteSend = SocketToParallelPort.TRIGGER_GAME_PLAYER_CRASH_WALL;
    }
    
    else if ((collisionBits & Player.COLLISION_BITS_WALL_BELOW) > 0)
    {
      proximityBot = 1.0;
      socketByteSend = SocketToParallelPort.TRIGGER_GAME_PLAYER_CRASH_WALL;
    }
      
    else if ((collisionBits & Player.COLLISION_BITS_AMMO) > 0)
    {
      socketByteSend = SocketToParallelPort.TRIGGER_GAME_COLLECT_AMMO;
    }
      
    else if ((collisionBits & Player.COLLISION_FLAG_MISSILE_HIT_ENEMY) > 0)
    {
      socketByteSend = SocketToParallelPort.TRIGGER_GAME_MISSILE_HIT_ENEMY;
    }
    
    else if (joystickButton == 1) socketByteSend = SocketToParallelPort.TRIGGER_GAME_SHOOT_BUTTON;
    
    out += String.format(",%d,%.3f,%.3f,", socketByteSend, proximityTop, proximityBot);
    
    
    updateSocket();
    
    
    

    for (int i = 0; i < Enemy.MAX_ENEMY_COUNT; i++)
    {
      if ((enemyList[i] != null) && (enemyList[i].isAlive()) && Library.isOnScreen(enemyList[i]))
      {
        //out += String.format("%.3f,%.3f,", enemyList[i].getCenterX(), enemyList[i].getCenterY());
        
        double proximity = player.getProximity(enemyList[i]);
        double angle = 0.0;
        if (proximity > 0.0) angle = Math.toDegrees(player.getAngle(enemyList[i]));
        int enemyType = enemyList[i].getType().ordinal();
        out += String.format("%d,%.3f,%.3f,",enemyType, proximity, angle);
      }
      else out += "0,0,0,";
    }
    for (int i = 0; i < Star.MAX_STAR_COUNT; i++)
    {
      if ((starList[i] != null) && (starList[i].isAlive())  && Library.isOnScreen(starList[i]))
      {
        //out += String.format("%.3f,%.3f,", starList[i].getCenterX(), starList[i].getCenterY());
        double proximity = player.getProximity(starList[i]);
        double angle = 0.0;
        if (proximity > 0.0) angle = Math.toDegrees(player.getAngle(starList[i]));
        out += String.format("%.3f,%.3f,",proximity, angle);
      }
      else out += "0,0,";
    }
    if ((ammo != null) && (ammo.isAlive())  && Library.isOnScreen(ammo))
    {
      //out += String.format("%.3f,%.3f", ammo.getCenterX(), ammo.getCenterY());
      double proximity = player.getProximity(ammo);
      double angle = 0.0;
      if (proximity > 0.0) angle = Math.toDegrees(player.getAngle(ammo));
      out += String.format("%.3f,%.3f,",proximity, angle);
    }
    else out += "0,0,";

    if ((missile != null) && (missile.isAlive()) && Library.isOnScreen(missile))
    {
      out += String.format("%.3f,%.3f\n", missile.getCenterX(), missile.getCenterY());
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
	
	
  private void updateSocket()
  {
    if (socket == null) return;
    
    if (socketByteLast != SocketToParallelPort.TRIGGER_SIGNAL_GROUND)
    { 
      socket.sendByte(SocketToParallelPort.TRIGGER_SIGNAL_GROUND);
      socketByteLast = SocketToParallelPort.TRIGGER_SIGNAL_GROUND;
      return;
    }
    
    if (socketByteSend != SocketToParallelPort.TRIGGER_SIGNAL_GROUND)
    {
      socket.sendByte(socketByteSend);
      socketByteLast = socketByteSend;
      socketByteSend = SocketToParallelPort.TRIGGER_SIGNAL_GROUND;
    }
  }
  
  public void sendByteBySocket(byte data)
  {
    socket.sendByte(data);
  }
  
//  public double getProximity(double playerX, double playerY, double x, double y)
//  {
//    double dx = 
//  }

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
      catch (IOException e) {}
    }
  }
}
