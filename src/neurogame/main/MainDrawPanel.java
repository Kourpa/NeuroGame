package neurogame.main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

import neurogame.gameplay.GameObject;
import neurogame.gameplay.PowerUp;
import neurogame.level.World;
import neurogame.library.Library;
import neurogame.library.SpriteMap;
import neurogame.main.GameController.GameState;

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
  private TitleScreen title;
  private GameOverScreen gameOver;
  private Graphics2D canvasObjectLayer;
  private BufferedImage imageObjectLayer;

  // private Graphics2D canvasBackgroundLayer;
  // private BufferedImage imageBackgroundLayer;

  private int windowWidth, windowHeight;

  private NeuroFrame frame;
  private World world;
  private PlayerHud HUD;
  private SpriteMap sprites;
  
  /**
   * Instantiates a new NeuroFrame.
   */
  public MainDrawPanel(final NeuroGame game, NeuroFrame frame)
  {

    System.out.println("MainGameDrawPanel(): Enter");
    this.frame = frame;
    HUD = new PlayerHud(frame);
  }

  public void setSprites(SpriteMap sprites)
  {
    this.sprites = sprites;
  }

  public void setTitle(TitleScreen title)
  {
    this.title = title;
  }
  
  public void setGameOver(GameOverScreen screen){
	  this.gameOver = screen;
  }

  public void setWorld(World world)
  {
    this.world = world;
  }

  public void resizeHelper(int width, int height)
  {

    windowWidth = width;
    windowHeight = height;

    this.setSize(windowWidth, windowHeight);

    if (canvasObjectLayer != null) canvasObjectLayer.dispose();

    imageObjectLayer = new BufferedImage(windowWidth, windowHeight,
        BufferedImage.TYPE_INT_RGB);
    canvasObjectLayer = imageObjectLayer.createGraphics();

    // imageBackgroundLayer = new BufferedImage(windowWidth, windowHeight,
    // BufferedImage.TYPE_INT_RGB);
    // canvasBackgroundLayer = imageBackgroundLayer.createGraphics();

    GameController.setGraphics(canvasObjectLayer);

    render(null);

  }

  // public void clearScreen()
  // {
  // canvasObjectLayer.setColor(Color.BLACK);
  // canvasObjectLayer.fillRect(0, 0, windowWidth, windowHeight);
  // }

  /**
   * Draw the heads-up display.
   */
private void drawHUD()
{
	HUD.updateHUD(canvasObjectLayer, frame);
}

<<<<<<< HEAD
public void paintComponent(Graphics g)
=======
  /**
   * Draw the health display.
   */
  private void drawHealth()
  {
    int health = frame.getHealth();
    Color outline = Color.GREEN;
    if (health >= 0.9 * Library.HEALTH_MAX)
    {
      canvasObjectLayer.setPaint(healthPaintFull);
    }
    else if (health > 0.2 * Library.HEALTH_MAX)
    {
      canvasObjectLayer.setPaint(healthPaintDamaged);
      outline = Color.ORANGE;
    }
    else
    {
      canvasObjectLayer.setPaint(healthPaintNearDeath);
      outline = Color.RED;
    }

    int width = (frame.getHealth() * 300) / Library.HEALTH_MAX;
    canvasObjectLayer.fillRect(5, 5, width, 32);
    canvasObjectLayer.setPaint(outline);
    canvasObjectLayer.drawRect(5, 5, 300, 32);

  }



  public void paintComponent(Graphics g)
>>>>>>> 5176bbe7b070fa34dfb8afb2f8b47b34782eeeb7
  {
    try
    {
      g.drawImage(imageObjectLayer, 0, 0, null);
    }
    catch (Exception e)
    {
    }
  }

  public void render(ArrayList<GameObject> gameObjList)
  {
    if (frame.getGameMode() == GameState.INITIALIZING) return;

    // System.out.println("NeuroFrame.render(): graphics=" + graphics);
    if (frame.getGameMode() == GameState.TITLE)
    {
      canvasObjectLayer.drawImage(title.getImage(), 0, 0, null);
    }
    else
    {
      world.render(canvasObjectLayer);
      // canvasObjectLayer.setColor(Color.BLUE);
      // canvasObjectLayer.fillRect(0, 0, windowWidth,windowHeight);
      // System.out.println("windowWidth="+windowWidth+", windowHeight="+windowHeight);
      world.getPlayer().render(canvasObjectLayer);

      if (gameObjList != null)
      {
        for (GameObject obj : gameObjList)
        {
          obj.render(canvasObjectLayer);
        }
      }

      drawHUD();
    }

    repaint();
  }
  // Render one frame.
  // do
  // {
  // do
  // {
  // if (!isVisible())
  // {
  // setVisible(true);
  // }
  // graphics = (Graphics2D) strategy.getDrawGraphics();

  // switch (mode)
  // {
  // case PLAYING:
  // graphics.drawImage(engine.image(), 0, 0, windowWidth, windowHeight, null);
  //
  // // if (debug)
  // // {
  // // debug();
  // // }
  // drawHUD();
  // break;
  // case PAUSED:
  // graphics.drawImage(engine.image(), 0, 0, windowWidth, windowHeight, null);
  // // if (debug)
  // // {
  // // debug();
  // // }
  // drawHUD();
  // break;
  // case TITLE:
  // if (title != null)
  // {
  // graphics.drawImage(title.image(), 0, 0, windowWidth, windowHeight, null);
  // }
  // break;
  // default:
  // break;
  // }
  // }
  // while (strategy.contentsRestored());
  // // Show the buffer.
  // strategy.show();
  // }
  // while (strategy.contentsLost());

  // this.repaint();
  // }

}
