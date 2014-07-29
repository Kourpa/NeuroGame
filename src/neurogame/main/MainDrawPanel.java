package neurogame.main;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

import neurogame.gameplay.GameObject;
import neurogame.level.World;
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
  private Graphics2D canvasObjectLayer;
  private BufferedImage imageObjectLayer;

  // private Graphics2D canvasBackgroundLayer;
  // private BufferedImage imageBackgroundLayer;

  private int windowWidth, windowHeight;

  private NeuroFrame frame;
  private World world;
  private PlayerHud HUD;
  
  /**
   * Instantiates a new NeuroFrame.
   */
  public MainDrawPanel(final NeuroGame game, NeuroFrame frame)
  {
	  System.out.println("MainGameDrawPanel(): Enter");
	  this.frame = frame;
	  
  }


  public void setTitle(TitleScreen title)
  {
    this.title = title;
  }
  
  public void setGameOver(GameOverScreen screen){
	  //this.gameOver = screen;
	  HUD.drawGameOver(true);
  }

  public void setWorld(World world)
  {
    this.world = world;
    HUD = new PlayerHud(frame);
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

    // imageBackgroundLayer = new BufferedImage(windowWidth, windowHeight,
    // BufferedImage.TYPE_INT_RGB);
    // canvasBackgroundLayer = imageBackgroundLayer.createGraphics();


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
	HUD.updateHUD(canvasObjectLayer, frame, world.getPlayer().getMissileCount());
}

public void paintComponent(Graphics g)
{
      g.drawImage(imageObjectLayer, 0, 0, null);
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
