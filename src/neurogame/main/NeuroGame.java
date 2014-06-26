/**
 * NeuroGame.
 * CS 351, Project 3
 * 
 * Team members:
 * Ramon A. Lovato
 * Danny Gomez
 * Marcos Lemus
 */


//GIT Repository
//git clone https://github.com/Kourpa/NeuroGame  //initial set up
//git config core.autocrlf false


//git status
//git add -A //when you create new fines that need to be added
//git commit -am 'comment' //commit -a (all) -m (message)
//git pull origin master
//git push origin master


//git stash //kill all local changes

package neurogame.main;

import javax.swing.UnsupportedLookAndFeelException;

import neurogame.library.Library;
import neurogame.main.GameController.GameState;

/**
 * NeuroGame's main class.
 * 
 * @author Ramon A. Lovato
 * @team Danny Gomez
 * @team Marcos Lemus
 */
public class NeuroGame
{
  private NeuroFrame frame;
  private GameController controller;

  private long startTime;
  private long elapsedTime;
  
  private final int PORT = 0xF050;
  private Process cTimer;

  /**
   * Initializes a new NeuroGame session.
   */
  public NeuroGame()
  {
    init();
  }

  /**
   * Sets up gameFrame and controller.
   */
  private void init()
  {
    startTime = elapsedTime = System.currentTimeMillis();
    
//    try{
//        cTimer = Runtime.getRuntime().exec("Resources/Timer/timer.exe " + PORT);
//        System.out.println("Timer Started");
//    } catch(IOException ex){
//        System.out.println("Starting CTimer Failed.");
//    }

    System.out.println("Starting CTimer Failed: (Joel Turned it off because it keeps running - even after the java program exits!!!.)");
    
    
    // Load user profiles
    Library.loadUsers();
    
    final NeuroGame game = this;

    frame = new NeuroFrame(game);
    Library.initSprites(frame);

    controller = new GameController(this, frame);

    frame.setGameMode(GameState.TITLE);
    frame.render(null);
  }

  /**
   * Gets the time since initialization (in milliseconds).
   * 
   * @return Time since initialization (in milliseconds).
   */
  public long elapsedTime()
  {
    return System.currentTimeMillis() - startTime;
  }

  /**
   * Converts the time since initialization to a string.
   * 
   * @return String representation of the elapsed time.
   */
  public String elapsedTimeString()
  {
    StringBuilder time = new StringBuilder("");
    elapsedTime = elapsedTime();
    long elapsedSecs = elapsedTime / 1000;
    long elapsedMins = elapsedSecs / 60;
    long hours = elapsedMins / 60;
    long mins = elapsedMins % 60;
    long secs = elapsedSecs % 60;

    // Hours.
    if (hours < 10)
    {
      time.append("0");
    }
    time.append(hours);
    time.append(":");
    // Minutes.
    if (mins < 10)
    {
      time.append("0");
    }
    time.append(mins);
    time.append(":");
    // Seconds.
    if (secs < 10)
    {
      time.append("0");
    }
    time.append(secs);

    return time.toString();
  }

  /**
   * Print CLI instructions.
   */
  public static void printInstructions()
  {
    System.out.printf("Usage:%n" + "    NeuroGame [OPTIONS]%n");
    System.out.println();
    System.out.printf("Options: -hdfFw%n" + "    h - print this help message%n"
        + "    d - enable global debug mode%n"
        + "    D - disable global debug mode (default)%n"
        + "    l - enable local logging (default)%n"
        + "    L - disable local logging%n"
        + "    f - run in full-screen-exclusive mode (default)%n"
        + "    F - run in maximized window mode%n"
        + "    w - run in normal windowed mode%n"
        + "    s - run with sound enabled (default)%n"
        + "    S - run with sound disabled%n"
        + "    g - enable global God mode%n"
        + "    G - disable global God mode (default)");
    System.out.println();
    System.out.println("Arguments {'e', 'E'}, {'f', 'F', 'w'} "
        + "{'g', 'G'}, {'l', 'L'} and {'s', 'S'} are " + "mutually exclusive.");
  }


  /**
   * Finish any outstanding tasks and exit the game cleanly.
   */
  public void quit()
  {
    System.exit(0);
  }

  /**
   * NeuroGame's main method.
   * 
   * @param args
   *          Command-line arguments.
   */
  public static void main(String[] args)
  {
    // OS X-specific tweaks. Recent versions completely ignore these, but
    // they're provided for legacy purposes.
    System.setProperty("apple.laf.useScreenMenuBar", "true");
    System.setProperty("com.apple.mrj.application.apple.menu.about.name",
        Library.GAME_TITLE);
    System.setProperty("apple.awt.fullscreenhidecursor", "true");
    try
    {
      javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager
          .getCrossPlatformLookAndFeelClassName());
    }
    catch (ClassNotFoundException | InstantiationException
        | IllegalAccessException | UnsupportedLookAndFeelException ex)
    {
      ex.printStackTrace();
    }

    NeuroGame game = new NeuroGame();
    // Process CLI arguments.
    if (args.length == 1)
    {
      if (args[0].matches(Library.ARGS_REGEX))
      {
        String s = args[0].substring(1);
        // Help message.
        if (s.contains("h"))
        {
          printInstructions();
        }
        // Debug mode.
        if (s.contains("d"))
        {
          game.controller.setGlobalDebug(true);
          Library.setDebug(true);
        }
        else if (s.contains("D"))
        {
          game.controller.setGlobalDebug(false);
          Library.setDebug(false);
        }

        // Sound mode.
        if (s.contains("s"))
        {
          game.controller.setSound(true);
        }
        else if (s.contains("S"))
        {
          game.controller.setSound(false);
        }
        else
        {
          game.controller.setSound(true);
        }
        // God mode. If and only if perma-God mode is explicitly
        // enabled from the command line, then also set suicideEnabled
        // to true.
        if (s.contains("g"))
        {
          game.controller.setGodMode(true);
          game.controller.setSuicideEnabled(true);
        }
        else if (s.contains("G"))
        {
          game.controller.setGodMode(false);
          game.controller.setSuicideEnabled(false);
        }
        else
        {
          game.controller.setGodMode(false);
          game.controller.setSuicideEnabled(false);
        }
      }
      else
      {
        printInstructions();
      }
    }
    else
    {
      game.controller.setSound(true);
      //game.controller.setLoggingMode(false);
      game.controller.setGodMode(false);
      game.controller.setSuicideEnabled(false);
      game.controller.setGlobalDebug(false);
      Library.setDebug(false);
    }
    // Hand off control to the GameController and start the timer. The
    // timer must not be started until after setting the frame to full-
    // screen-exclusive mode, as it can cause concurrency issues.
    game.controller.mainGameLoop();
    game.cTimer.destroy();
    System.out.println("CTimer killed!");
  }
}
