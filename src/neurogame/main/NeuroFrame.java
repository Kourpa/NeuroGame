/**
 * NeuroGame.
 * CS 351, Project 3
 * 
 * Team members:
 * Ramon A. Lovato
 * Danny Gomez
 * Marcos Lemus
 */

package neurogame.main;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JFrame;

import neurogame.gameplay.GameObject;
import neurogame.level.World;
import neurogame.library.Library;
import neurogame.library.User;
import neurogame.main.NeuroGame.GameState;

/**
 * The main game frame for NeuroGame.
 * 
 * @author Marcos Lemus
 * @author Ramon A. Lovato
 * @team Danny Gomez
 */
@SuppressWarnings("serial")
public class NeuroFrame extends JFrame
{
  private static final int MIN_WINDOW_WIDTH = 200;
  private static final int MIN_WINDOW_HEIGHT = 200;

  // private BufferStrategy strategy;
  // private DrawingEngine engine;

  private MainDrawPanel drawPanel;
  private NeuroGame game;
  private int windowPixelWidth, windowPixelHeight;
  //private GameState mode = GameState.INITIALIZING;

  private Container contentPane;

  private User currentUser;
  private GameController controller;

  /**
   * Instantiates a new NeuroFrame.
   */
  public NeuroFrame(final NeuroGame game)
  {
    System.out.println("NeuroFrame(): Enter");


    this.setResizable(true);
    this.setTitle(Library.GAME_TITLE);

    Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    int screenWidth = screenSize.width;
    int screenHeight = screenSize.height;

    this.setBounds(0, 0, screenWidth, screenHeight);
    this.setVisible(true);
    contentPane = getContentPane();
    contentPane.setLayout(null);

    drawPanel = new MainDrawPanel(game, this);
    contentPane.add(drawPanel);
    drawPanel.setVisible(false);
    //contentPane.setBackground(Color.black);


    resizeHelper();

    this.addComponentListener(new ComponentAdapter()
    {
      public void componentResized(ComponentEvent e)
      {
        resizeHelper();
      }
    });

    // Listener to check for the frame closing and cleanly exit.
    addWindowListener(new WindowAdapter()
    {

      public void windowClosing(WindowEvent e)
      {
        game.quit();
      }
    });

  }
  
  public void setGameController(GameController cont){
	  this.controller = cont;
  }

  /**
   * Show the game using the provided DrawingEngine.
   * 
   * @param world
   *          World for the current game.
   * @return DrawingEngine being used to draw the world.
   */
  public void startGame(World world)
  {
    // engine = new DrawingEngine(this, world);
    //setContentPane(drawPanel);
    contentPane.add(drawPanel);
    drawPanel.setVisible(true);
    drawPanel.setWorld(world);
    //mode = GameState.PLAYING;
    // title = null;
  }

  public MainDrawPanel getDrawPanel(){
	  return drawPanel;
  }
  
  public GameState getGameMode()
  {
	  System.out.println("NeuroFrame:  cont: "+controller);
    return game.getGameState();
  }
 
  
  /**
 * Sets the user for the session
 */
public void setUser(User newUser){
	  this.currentUser = newUser;
  }
public User getUser(){
	return this.currentUser;
}
  
  /**
 * Returns the current user 
 */
public User getCurrentUser(){
	  return this.currentUser;
  }

  /**
   * A helper method that gets called when the window is resized, which releases
   * the old buffered image and generates a new one with the new window
   * dimensions.
   */
  private void resizeHelper()
  {

    boolean sizeIsOkay = true;
    int outsideFrameWidth = this.getWidth();
    int outsideFrameHeight = this.getHeight();

    if (outsideFrameWidth < MIN_WINDOW_WIDTH)
    {
      outsideFrameWidth = MIN_WINDOW_WIDTH;
      sizeIsOkay = false;
    }

    if (outsideFrameHeight < MIN_WINDOW_HEIGHT)
    {
      outsideFrameHeight = MIN_WINDOW_HEIGHT;
      sizeIsOkay = false;
    }

    if (outsideFrameHeight > outsideFrameWidth)
    {
      outsideFrameHeight = outsideFrameWidth;
      sizeIsOkay = false;
    }
    if (outsideFrameWidth > outsideFrameHeight * 2)
    {
      outsideFrameWidth = outsideFrameHeight * 2;
      sizeIsOkay = false;
    }

    Insets inset = this.getInsets();
    windowPixelWidth = outsideFrameWidth - inset.left - inset.right;
    windowPixelHeight = outsideFrameHeight - inset.top - inset.bottom;

    System.out.println("NeuroFrame.resizeHelper(): Enter (" + windowPixelWidth
        + ", " + windowPixelHeight + ")");

    if (sizeIsOkay)
    {
      Library.setWindowPixelWidth(windowPixelWidth);
      Library.setWindowPixelHeight(windowPixelHeight);
      Library.U_VALUE = windowPixelHeight;

      drawPanel.resizeHelper(windowPixelWidth, windowPixelHeight);
      
    }
    else
    {
      this.setSize(outsideFrameWidth, outsideFrameHeight);
    }

  }

  public void render(ArrayList<GameObject> gameObjList)
  {
    drawPanel.render(gameObjList);
  }
}
