package neurogame.main;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JPanel;

import neurogame.gameplay.GameObject;
import neurogame.io.InputController;
import neurogame.io.User;
import neurogame.level.World;
import neurogame.library.Library;
import neurogame.main.NeuroGame.GameState;


/**
 * The main game frame for NeuroGame.
 * 
 * @author Marcos Lemus
 * @author Ramon A. Lovato
 * @team Danny Gomez
 */
@SuppressWarnings("serial")
public class MainDrawPanel extends JPanel
{	
  private Graphics2D canvasObjectLayer;
  private BufferedImage imageObjectLayer;

  private int windowWidth, windowHeight;

  private World world;
  private NeuroGame game;
  private PlayerHud HUD;
  
  
  FontMetrics fontMetrix30,fontMetrix20;
  
  /**
   * Instantiates a new NeuroFrame.
   */
  public MainDrawPanel(final NeuroGame game, InputController controller)
  {
	  System.out.println("MainGameDrawPanel(): Enter");
	  this.game = game;
	  this.addKeyListener(controller);
	  
	  fontMetrix30 = this.getFontMetrics(Library.FONT_ARIAL30);
  }
  
  public void initGame()
  {
    this.setVisible(true);
    this.requestFocus();
  }
  

  public void setWorld(World world)
  {
    this.world = world;
    HUD = new PlayerHud(game, world, game.getUser());
  }

  public void resizeHelper(int width, int height)
  {

    windowWidth = width;
    windowHeight = height;

    this.setSize(windowWidth, windowHeight);

    if (canvasObjectLayer != null) canvasObjectLayer.dispose();

    imageObjectLayer = new BufferedImage(windowWidth, windowHeight,
        BufferedImage.TYPE_INT_ARGB);
    canvasObjectLayer = imageObjectLayer.createGraphics();

    render(null);

  }

  /**
   * Draw the heads-up display.
   */


public void paintComponent(Graphics g)
{
      g.drawImage(imageObjectLayer, 0, 0, null);
}


  private void drawGameOver()
  {
    canvasObjectLayer.setFont(Library.FONT70);
    canvasObjectLayer.setColor(Color.blue);
    canvasObjectLayer.drawString("GAME OVER", (int) (windowWidth * 0.47 - 225), (int) (windowHeight * 0.25));

    // canvasObjectLayer.setFont(FONT30);
    // canvasObjectLayer.setColor(Color.blue);
    // canvasObjectLayer.drawString(highscoreMessage, (int) (windowWidth * 0.475
    // - 225), (int) (windowHeight * 0.35));

    canvasObjectLayer.setFont(Library.FONT30);
    canvasObjectLayer.setColor(Color.white);
    canvasObjectLayer.drawString("High Scores: ", (int) (windowWidth * 0.475 - 75), (int) (windowHeight * 0.375));

    // ADD HIGH SCORES IN HERE
    canvasObjectLayer.setFont(Library.FONT30);
    canvasObjectLayer.setColor(Color.white);
    ArrayList<User> userList = new ArrayList<User>(User.getUserList());
    Collections.sort(userList);
    double yValue = 0.45;
    User currentUser = game.getCurrentUser();
    int n = Math.min(userList.size(), 10);
    for (int i = 0; i < n; i++)
    {
      User user = userList.get(i);
      if (user == currentUser)
      {
        canvasObjectLayer.setColor(Library.HIGHLIGHT_TEXT_COLOR);
      }
      else
      {
        canvasObjectLayer.setColor(Color.white);
      }
      int score = user.getHighScore();
      if (score > 0)
      {
        String str = user.getName() + "   " + user.getHighScoreDate() + "   " + score;
        canvasObjectLayer.drawString(str, (int) (windowWidth * 0.475 - 225), (int) (windowHeight * yValue));
        yValue += .05;
      }
    }

    canvasObjectLayer.setFont(Library.FONT_ARIAL30);
    canvasObjectLayer.setColor(Library.UNSELECTED_TEXT_COLOR);
    
    canvasObjectLayer.drawString("Press Joystick Button (or Enter Key) for New Game", 50, windowHeight - 150);
    canvasObjectLayer.setFont(Library.FONT_ARIAL20);
    
    canvasObjectLayer.drawString("Press Esc to return to main menu", 50, windowHeight - 35);

    
  }

  public void render(ArrayList<GameObject> gameObjList)
  {
    if (game.getGameState() == GameState.INITIALIZING) return;

    // System.out.println("NeuroFrame.render(): graphics=" + graphics);
    if ((game.getGameState() == GameState.PLAYING) || (game.getGameState() == GameState.GAMEOVER))
    {
      world.render(canvasObjectLayer);

      if (gameObjList != null)
      {
        for (GameObject obj : gameObjList)
        {
          if (obj.isAlive()) obj.render(canvasObjectLayer);
        }
      }
      HUD.updateHUD(canvasObjectLayer, game, world.getPlayer().getAmmoCount());
      if (game.getGameState() == GameState.GAMEOVER) drawGameOver();

    }
    repaint();
  }
}
