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
//git push origin master //use --force when fracking git is being a git.


//git stash //kill all local changes

package neurogame.main;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.UnsupportedLookAndFeelException;

import neurogame.gameplay.Enemy;
import neurogame.gameplay.GameObject;
import neurogame.gameplay.GameObjectType;
import neurogame.gameplay.Player;
import neurogame.gameplay.PowerUp;
import neurogame.gameplay.Star;
import neurogame.level.World;
import neurogame.library.Library;
import neurogame.io.Logger;

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

  public enum GameState
  {
    INITIALIZING, //Skip all rendering
    TITLE,        //Title / option screen displayed
    PLAYING,      // Normal game play
    PAUSED,       // Normal game play paused
    DEAD,         // TODO : Player has died, continue showing game moving, no player controls, display "Game Over" overlay.
    GAMEOVER,     // show the high scores 
    ODDBALL,	  // Oddball visual test 
    HIGHSCORE;	  // Highscore screen after winning
  }
  
  private long startTime;
  private long elapsedTime;
  
  private GameState gameState = GameState.INITIALIZING;
  private TitleScreen title;
  private GameOverScreen gameOver;

  private World world;
  private Player player;
  private double timepassed;
  
  
  private Oddball oddball;
  private boolean loggingMode;
  private Logger log;
  
  private boolean soundEnabled;
  private boolean godMode;
  

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
    
    // Load user profiles
    Library.loadUsers();
    final NeuroGame game = this;
        
    frame = new NeuroFrame(game);
    Library.initSprites(frame);
    Library.loadFont();
    
  	title = new TitleScreen(frame);
    controller = new GameController(this, frame);
    
    frame.setGameController(controller);
    frame.render(null);
    showTitle();
    
    // Defaults
    loggingMode = true;
    soundEnabled = true;
    godMode = false;
    timepassed = 0.0;
  }
  
  public void mainGameLoop()
  {
    long timeMS_start = System.currentTimeMillis();
    long timeMS_curr = timeMS_start;
    long timeMS_last = timeMS_start;
    update(0);

    while (true)
    {

      long deltaMilliSec = System.currentTimeMillis() - timeMS_last;

      if (deltaMilliSec < Library.MIN_FRAME_MILLISEC)
      {
        try { Thread.sleep(Library.MIN_FRAME_MILLISEC - deltaMilliSec); }
        catch (Exception e) { }
      }
      timeMS_curr = System.currentTimeMillis();
      double deltaSec = (timeMS_curr - timeMS_last) / 1000.0;

      timeMS_last = timeMS_curr;
      update(deltaSec);
    }
  }



  /**
   * Perform framewise updates.
   */
  private void update(double deltaSec)
  {
    switch (gameState)
    {
    case PLAYING:
      playUpdate(deltaSec);
      
      if (player.getHealth() <= 0)
      {
        killPlayer();
        gameOver();
      }
      break;
    case PAUSED:
      pauseUpdate();
      break;
    case TITLE:
    	controller.titleUpdate();
      break;
    case GAMEOVER:
    	controller.gameOverUpdate(deltaSec);
    	break;
    case HIGHSCORE:
    	controller.highscoreUpdate();
    	break;
    case ODDBALL:
    	controller.oddballUpdate();
    	break;
    default:
      break;
    }

    ArrayList<GameObject> gameObjectList = null;
    if (world != null)
    { gameObjectList = world.getObjectList();
    }

    frame.render(gameObjectList);
  }

  public World getWorld(){
	  return world;
  }

  /**
   * Displays the title screen.
   */
  public void showTitle(){
	MainDrawPanel drawPanel = frame.getDrawPanel();
    
	gameState = GameState.TITLE;
    drawPanel.setVisible(false);
    drawPanel.setTitle(title);
    title.showTitleScreen(true);
    
  }
  
  private void showGameOver(){
	  MainDrawPanel drawPanel = frame.getDrawPanel();
	  
	  gameState = GameState.GAMEOVER;
	  drawPanel.setGameOver(null);
	  this.player.die(true);
  }
  
  public void showHighScores(){
	  MainDrawPanel drawPanel = frame.getDrawPanel();
	  
	  gameState = GameState.HIGHSCORE;
	  gameOver = new GameOverScreen(frame,frame.getCurrentUser());
	  drawPanel.setGameOver(gameOver);
	  drawPanel.setVisible(false);
  }

  /**
   * Handles keyboard events while paused.
   */
  private void pauseUpdate()
  {

    if (controller.getInputs().get("pause"))
    {
      controller.getInputs().put("pause", false);
      unpause();
    }
    if (controller.getInputs().get("sound"))
    {
      soundEnabled = !soundEnabled;
    }
  }

  /**
   * Send the passed String to the IOExecutor to be queued for logging. Does
   * nothing if logging is disabled.
   *
   * @param s
   *          String to send to the IOExecutor. The executor's Logger will add
   *          the time stamp automatically.
   */




  /**
   * Setter for loggingMode.
   *
   * @param loggingMode
   *          Boolean for enabling/disabling local logging.
   */
  public void setLoggingMode(boolean loggingMode)
  {
    this.loggingMode = loggingMode;
  }

  /**
   * Setter for enabling/disabling the sound.
   *
   * @param soundEnabled
   *          Enable/disable the sound if true/false, respectively.
   */
  public void setSound(boolean soundEnabled)
  {
    this.soundEnabled = soundEnabled;
  }

  /**
   * Setter for God mode. The global God mode is unaffected by the immunity
   * granted by the super PowerUp.
   *
   * @param godMode
   *          Boolean for enabling/disabling God mode.
   */
  public void setGodMode(boolean godMode)
  {
    this.godMode = godMode;
  }


  /**
   * Kill the player.
   */
  private void killPlayer()
  {
    controller.setControllable(false);
  }

  /**
   * Start a new game.
   */
  public void newGame()
  {
    System.out.println("GameController.newGame()  loggingMode="+loggingMode );
    gameState = GameState.PLAYING;

    
    world = new World();
    Star.initGame();
    // zappers = world.getZappers();
    frame.startGame(world);
    controller.setControllable(true);
    player = world.getPlayer();
    GameObject.resetGameObjectCount();
    PowerUp.initGame();
    Enemy.initGame();

    if (loggingMode) log = new Logger();
  }
  
  public void showOddBall(){  
	oddball = new Oddball(frame);
	gameState = GameState.ODDBALL;
  }
  /**
   * Pause the game.
   */
  public void togglePause()
  {
	  if(gameState == GameState.PLAYING){
		  gameState = GameState.PAUSED;
	  }
	  else if(gameState == GameState.PAUSED){
		  gameState = GameState.PLAYING;
	  }
  }

  /**
   * Unpause the game.
   */
  private void unpause()
  {
    gameState = GameState.PLAYING;
    //frame.unpause();
  }

  /**
   * End the current game.
   */
  private void gameOver()
  {
    if (loggingMode) 
    { log.closeLog();
      log = null;
    }
    
    
  	frame.getUser().saveHighscore(player.getScore());
   	Library.saveUser(frame.getUser());

    showGameOver();
  }

  public TitleScreen getTitleScreen(){
	  return title;
  }
  
  public GameOverScreen getGameOverScreen(){
	  return gameOver;
  }
  
  public Oddball getOddballScreen(){
	  return oddball;
  }
  
  private void playUpdate(double deltaTime)
  {
    
    // Player input.
    controller.keyHandler();
    double scrollDistance = world.update(deltaTime);

    updateObjectList(world.getObjectList(), deltaTime, scrollDistance);
    
    if (loggingMode) log.update(world);
  }

  public GameState getGameState(){
	  return this.gameState;
  }

  
  /**
   * Helper method for handling GameObject updates.
   *
   * @param list
   *          List<GameObject> to iterate over and update.
   * @param deltaTime
   */
  public void updateObjectList(List<GameObject> gameObjList, double deltaTime, double scrollDistance)
  {
    for (ListIterator<GameObject> iterator = gameObjList.listIterator(); iterator.hasNext();)
    {
      GameObject obj = iterator.next();

      if (obj.isAlive())
      {
        obj.update(deltaTime, scrollDistance);
      }
      if (obj.isAlive() == false)
      {
        iterator.remove();
      }
    }

    //Check for object / object collisions after all objects have updated
    for (int i = 0; i < gameObjList.size(); i++)
    {
      GameObject obj1 = gameObjList.get(i);
      GameObjectType type1 = obj1.getType();
      if (obj1.isAlive() == false) continue;
      if (type1.hasCollider() == false) continue;

      for (int k = i+1; k < gameObjList.size(); k++)
      {
        GameObject obj2 = gameObjList.get(k);
        GameObjectType type2 = obj2.getType();
        if (!obj2.isAlive()) continue;
        if (type2.hasCollider() == false) continue;

        if ((!type1.isDynamic()) && (!type2.isDynamic())) continue;

        if (obj1.collision(obj2))
        {
          //System.out.println("HIT: " + obj1 + " <--> " + obj2);
          obj1.hit(obj2);
          obj2.hit(obj1);
        }
      }
    }
  }
  
  
  /**
   * Activate the powerUp (if held).
   */
  public void usePowerUp(){
    player.shootMissile();
  }


  /**
   * Gets the time since initialization (in milliseconds).
   * 
   * @return Time since initialization (in milliseconds).
   */
  public long elapsedTime(){
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
	        if (s.toLowerCase().contains("d"))
	        {
	          Library.setDebug(true);
	        }

	        // Sound mode.
	        if (s.toLowerCase().contains("s")){
	          //setSound(true);
	        }
	        else{
	          //setSound(true);
	        }
	        

	        // God mode. If and only if perma-God mode is explicitly
	        // enabled from the command line, then also set suicideEnabled
	        // to true.
	        if (s.toLowerCase().contains("g"))
	        {
	          //setGodMode(true);
	          //setSuicideEnabled(true);
	        }
	        else
	        {
	          //setGodMode(false);
	          //setSuicideEnabled(false);
	        }
	      }
	      else
	      {
	        printInstructions();
	      }
	    }
	    else
	    {
	      //setSound(true);
	      //setGodMode(false);
	      //setSuicideEnabled(false);
	      //setGlobalDebug(false);
	      Library.setDebug(false);
	    }

	    // Hand off control to the GameController and start the timer. The
	    // timer must not be started until after setting the frame to full-
	    // screen-exclusive mode, as it can cause concurrency issues.
	    game.mainGameLoop();
  }
}
