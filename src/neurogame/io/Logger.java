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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import neurogame.gameplay.Ammo;
import neurogame.gameplay.DirectionVector;
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

  public enum LogType
  {
    GAME, ODDBALL, BANDIT
  };

  private static final String LOG_EXTENSION = ".csv";
  private static final String PATH = "logs/";
  private static final String PARALLEL_CONNECTION_PROGRAM = "ParallelPortTrigger/timer.exe";
  private static Calendar calendar = GregorianCalendar.getInstance();

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
  private byte gameCount = 0;
  private boolean sentEndGameSignal;

  /**
   * Instantiate a new Logger with the default file path.
   * 
   * @param initTime
   *          String containing the time stamp of initialization for NeuroGame,
   *          as acquired by Library.timeStamp().
   */
  public Logger(InputController controller, User user, LogType type)
  {
    this.user = user;
    this.controller = controller;

    String fileName = generateFileName(type);

    logFile = new File(PATH, fileName);
    logFile.getParentFile().mkdir();

    // Enemy Type:
    // NONE = 0
    // ENEMY_STRAIGHT = 1;
    // ENEMY_FOLLOW = 2;
    // ENEMY_SINUSOIDAL = 3;
    // ZAPPER = 4;

    // Missile Target:
    // 0: no missile
    // -1 No target
    // 1 through Enemy.MAX_ENEMY_COUNT: Index of enemy that is within verticle
    // hit area.

    String out = "Seconds, PlayerX, PlayerY, Health, Ammo, JoystickX, JoystickY, JoystickButton, Trigger, WallAbove, WallBelow, ";
    for (int i = 0; i < Enemy.MAX_ENEMY_COUNT; i++)
    {
      int n = i + 1;
      out += "Enemy " + n + " Type,Enemy " + n + " Proximity,Enemy " + n + " Angle, ";
    }
    for (int i = 0; i < Star.MAX_STAR_COUNT; i++)
    {
      int n = i + 1;
      out += "Star " + n + " Proximity, Star " + n + " Angle, ";
    }

    // SimpleDateFormat dateFormat = new SimpleDateFormat
    // ("EEEE: MMMM d yyyy 'at' h:mm:ss a zzz");

    // Date curDate = new Date();

    long nanoTime = System.nanoTime();

    startSec = nanoTime * NeuroGame.NANO_TO_SEC;
    String milliSecOfDay = getCurrentTimStr();
    // out +=
    // "AmmoProximity, AmmoAngle, Missile Target, Missile Proximity to Target\nStart Date/Time: "
    // + dateFormat.format(curDate) + "\n";
    out += "AmmoProximity, AmmoAngle, Missile Target, Missile Proximity to Target\n" + milliSecOfDay + "\n";

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

  public static String getCurrentTimStr()
  {
    // StartTime: HHMMSSmmm

    // long millis = nanoSec / 1000000;
    // Calendar cal = Calendar.getInstance();
    // cal.setTimeInMillis(millis);

    Date curDate = new Date();
    calendar.setTime(curDate);
    int hours = calendar.get(Calendar.HOUR_OF_DAY);
    int min = calendar.get(Calendar.MINUTE);
    int sec = calendar.get(Calendar.SECOND);
    int milliSec = calendar.get(Calendar.MILLISECOND);
    // int milliSec = calendar.get(Calendar.MILLISECOND);
    // System.out.println("Hours="+hours);
    // long millisecondsAfterHour = ( nanoSec / 1000000) % MILLISEC_PER_HOUR;
    // System.out.println("millisecondsAfterHour="+millisecondsAfterHour);
    // millis -= TimeUnit.HOURS.toMillis(hours);
    // long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
    // millis -= TimeUnit.MINUTES.toMillis(minutes);
    // long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

    // System.out.println("hours="+hours + (hours*10000000L));
    // return Long.toString(hours * 10000000L + min * 100000L +
    // (long)sec*1000L);

    String outStr = String.format("%02d%02d%02d%03d", hours, min, sec, milliSec);
    System.out.println(curDate + ",  Time in HHMMSSmmm: " + outStr);
    return outStr;
  }

  private String generateFileName(LogType type)
  {
    return "Axon_" + type + "_Log_" + user.getName() + '_' + FILE_DATE_FORMAT.format(new Date()) + LOG_EXTENSION;
  }

  public void startGame()
  {
    System.out.println("Logger.startGame() gameCount=" + gameCount);
    sentEndGameSignal = false;
    socketByteSend = (byte) (SocketToParallelPort.TRIGGER_GAME_START_BASE + gameCount);
    gameCount++;
  }

  public void update(World world, double currentSec)
  {
    double gameSec = currentSec - startSec;

    String out = String.format("%.4f", gameSec);

    if (world == null)
    {
      if (!sentEndGameSignal)
      {
        socketByteSend = SocketToParallelPort.TRIGGER_GAME_OVER;
        sentEndGameSignal = true;
      }

      out += ",0,0,0,0,0,0,0";

      byte byteSent = updateSocket();

      out += String.format(",%d,0,0,", byteSent);
      for (int i = 0; i < Enemy.MAX_ENEMY_COUNT; i++)
      {
        out += "0,0,0, ";
      }
      for (int i = 0; i < Star.MAX_STAR_COUNT; i++)
      {
        out += "0,0, ";
      }
      out += "0,0,0,0\n";
    }

    else
    {

      Player player = world.getPlayer();
      Enemy[] enemyList = Enemy.getEnemyList();
      Star[] starList = Star.getStarList();
      Ammo ammo = Ammo.getCurrentAmmoBox();
      Missile missile = Missile.getCurrentMissile();
      double health = (double) (player.getHealth()) / Library.HEALTH_MAX;
      int joystickButton = 0;
      if (controller.isPlayerPressingButton()) joystickButton = 1;
      DirectionVector joystickVector = controller.getPlayerInputDirectionVector();

      // System.out.println(out);
      PathVertex vertex = world.getInterpolatedWallTopAndBottom(player.getX() + player.getWidth());

      // System.out.println("Logger(): vertex.getTop()="+vertex.getTop()+", vertex.getBottom()="+
      // vertex.getBottom());

      out += String.format(",%.3f,%.3f", player.getCenterX(), player.getCenterY());

      out += String.format(",%.2f,%d", health, player.getAmmoCount());

      out += String.format(",%.3f,%.3f,%d", joystickVector.x, joystickVector.y, joystickButton);

      int collisionBits = player.getCollisionLogBitsThisUpdate();

      double proximityTop = Math.min(1.0, (1.0 - (player.getY() - vertex.getTop())));
      double proximityBot = Math.min(1.0, (1.0 - (vertex.getBottom() - (player.getY() + player.getHeight()))));

      int missileTarget = 0;
      double missileProximity = 0;
      if ((missile != null) && (missile.isAlive()) && Library.isOnScreen(missile))
      {
        missileTarget = -1;
      }

      if (socketByteSend == SocketToParallelPort.TRIGGER_SIGNAL_GROUND)
      {
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
        else if ((collisionBits & Player.COLLISION_FLAG_ENEMY_LOST) > 0)
        {
          socketByteSend = SocketToParallelPort.TRIGGER_GAME_ENEMY_LOST;
        }
      }

      byte byteSent = updateSocket();

      out += String.format(",%d,%.3f,%.3f,", byteSent, proximityTop, proximityBot);

      for (int i = 0; i < Enemy.MAX_ENEMY_COUNT; i++)
      {
        if ((enemyList[i] != null) && (enemyList[i].isAlive()) && Library.isOnScreen(enemyList[i]))
        {
          // out += String.format("%.3f,%.3f,", enemyList[i].getCenterX(),
          // enemyList[i].getCenterY());

          double proximity = player.getProximity(enemyList[i]);
          double angle = 0.0;
          if (proximity > 0.0) angle = Math.toDegrees(player.getAngle(enemyList[i]));
          int enemyType = enemyList[i].getType().ordinal();
          out += String.format("%d,%.3f,%.3f,", enemyType, proximity, angle);

          if (missileTarget != 0)
          {
            if (missile.getX() < enemyList[i].getHitMaxX())
            {
              double y = enemyList[i].getY();
              if ((missile.getCenterY() > y) && (missile.getCenterY() < y + enemyList[i].getHeight()))
              {
                double dist = enemyList[i].getX() - missile.getHitMaxX();
                if (dist < 0) dist = 0;
                proximity = (Library.getWindowAspect() - dist) / Library.getWindowAspect();
                if (proximity > missileProximity)
                {
                  missileTarget = i + 1;
                  missileProximity = proximity;
                }
              }
            }
          }
        }
        else out += "0,0,0,";
      }
      for (int i = 0; i < Star.MAX_STAR_COUNT; i++)
      {
        if ((starList[i] != null) && (starList[i].isAlive()) && Library.isOnScreen(starList[i]))
        {
          // out += String.format("%.3f,%.3f,", starList[i].getCenterX(),
          // starList[i].getCenterY());
          double proximity = player.getProximity(starList[i]);
          double angle = 0.0;
          if (proximity > 0.0) angle = Math.toDegrees(player.getAngle(starList[i]));
          out += String.format("%.3f,%.3f,", proximity, angle);
        }
        else out += "0,0,";
      }
      if ((ammo != null) && (ammo.isAlive()) && Library.isOnScreen(ammo))
      {
        // out += String.format("%.3f,%.3f", ammo.getCenterX(),
        // ammo.getCenterY());
        double proximity = player.getProximity(ammo);
        double angle = 0.0;
        if (proximity > 0.0) angle = Math.toDegrees(player.getAngle(ammo));
        out += String.format("%.3f,%.3f,", proximity, angle);
      }
      else out += "0,0,";

      if (missileTarget != 0)
      {
        out += String.format("%d,%.3f\n", missileTarget, missileProximity);
      }
      else out += "0,0\n";
    }

    try
    {
      writer.write(out);
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
    }

  }

  private byte updateSocket()
  {
    if (socket == null) socketByteLast = 0;

    else if (socketByteLast != SocketToParallelPort.TRIGGER_SIGNAL_GROUND)
    {
      socket.sendByte(SocketToParallelPort.TRIGGER_SIGNAL_GROUND);
      socketByteLast = SocketToParallelPort.TRIGGER_SIGNAL_GROUND;
    }

    else if (socketByteSend != SocketToParallelPort.TRIGGER_SIGNAL_GROUND)
    {
      socket.sendByte(socketByteSend);
      socketByteLast = socketByteSend;
      socketByteSend = SocketToParallelPort.TRIGGER_SIGNAL_GROUND;
    }
    return socketByteLast;
  }

  public void sendByteBySocket(byte data)
  {
    socket.sendByte(data);
  }

  public void closeLog()
  {
    if (socket != null)
    {
      try
      {
        socket.close();
        writer.close();
      }
      catch (IOException e)
      {}
    }
  }
}
