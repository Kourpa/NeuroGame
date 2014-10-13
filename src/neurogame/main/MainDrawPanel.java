package neurogame.main;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

import neurogame.gameplay.GameObject;
import neurogame.io.InputController;
import neurogame.level.World;
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
  
  /**
   * Instantiates a new NeuroFrame.
   */
  public MainDrawPanel(final NeuroGame game, InputController controller)
  {
	  System.out.println("MainGameDrawPanel(): Enter");
	  this.game = game;
	  this.addKeyListener(controller);
  }
  
  public void initGame()
  {
    this.setVisible(true);
    this.requestFocus();
  }
  
  public void setGameOver()
  {
	  HUD.drawGameOver(true);
  }

  public void setWorld(World world)
  {
    this.world = world;
    HUD = new PlayerHud(game, world, game.getUser());
    HUD.drawGameOver(false); // reset the HUD
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
private void drawHUD()
{
	HUD.updateHUD(canvasObjectLayer, game, world.getPlayer().getAmmoCount());
}

public void paintComponent(Graphics g)
{
      g.drawImage(imageObjectLayer, 0, 0, null);
}

public void render(ArrayList<GameObject> gameObjList)
{
    if (game.getGameState() == GameState.INITIALIZING) return;

    // System.out.println("NeuroFrame.render(): graphics=" + graphics);
    if((game.getGameState() == GameState.PLAYING) || (game.getGameState() == GameState.GAMEOVER))
    {
      world.render(canvasObjectLayer);

      if (gameObjList != null)
      {
        for (GameObject obj : gameObjList)
        { if (obj.isAlive()) obj.render(canvasObjectLayer);
        }
      }
      
      drawHUD();
    }
    repaint();
  }
}
