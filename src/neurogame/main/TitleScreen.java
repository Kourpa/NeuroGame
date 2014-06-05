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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.Map;

import neurogame.library.Library;

/**
 * The title screen for NeuroGame.
 * 
 * @author Ramon A. Lovato
 * @team Danny Gomez
 * @team Marcos Lemus
 */
public class TitleScreen
{
  private Image titleBackground, startButtonPlain, startButtonSelected;
  private Image exitButtonPlain, exitButtonSelected;

  private String selected;

  private BufferedImage masterImage;
  private Graphics masterGraphics;
  //private BufferedImage image;
  //private Graphics graphics;
  private Map<String, BufferedImage> sprites;

  private int width;
  private int height;

  /**
   * Instantiate a new TitleScreen.
   * 
   * @param frame
   *          NeuroFrame to contain this TitleScreen.
   */
  public TitleScreen(final NeuroFrame frame)
  {
    masterImage = new BufferedImage(Library.getWindowWidth(),
        Library.getWindowHeight(), BufferedImage.TYPE_INT_ARGB);
    masterGraphics = masterImage.createGraphics();
    //image = masterImage;
    //graphics = image.createGraphics();
    width = frame.getWidth();
    height = frame.getHeight();
    sprites = Library.getSprites();

    // Get the images.

    titleBackground = sprites.get("titleBackground");
    startButtonPlain = sprites.get("startButtonPlain");
    startButtonSelected = sprites.get("startButtonSelected");
    exitButtonPlain = sprites.get("exitButtonPlain");
    exitButtonSelected = sprites.get("exitButtonSelected");

    frame.getRootPane().addComponentListener(new ComponentAdapter()
    {
      public void componentResized(ComponentEvent e)
      {
        width = frame.getWidth();
        height = frame.getHeight();
        //image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        //graphics = image.createGraphics();
        draw();
      }
    });

    selected = "start";
  }

  /**
   * Toggles which button is selected.
   */
  public void switchButton()
  {
    selected = (selected == "start" ? "exit" : "start");
    draw();
  }

  /**
   * Getter for selected.
   * 
   * @return selected String representation of selected button.
   */
  public String getSelected()
  {
    return selected;
  }

  /**
   * Clear the screen.
   */
  private void clearBuffers()
  {
    masterGraphics.setColor(Color.BLACK);
    masterGraphics.fillRect(0, 0, Library.getWindowWidth(), Library.getWindowHeight());
    //graphics.setColor(Color.BLACK);
    //graphics.fillRect(0, 0, width, height);
  }

  /**
   * Draws to the buffered image.
   */
  public void draw()
  {
    int sWidth = Library.getWindowWidth();
    int sHeight = Library.getWindowHeight();

    // Update master image.
    masterGraphics.drawImage(titleBackground, 0, 0, null);

    if (selected == "start")
    {
      masterGraphics.drawImage(startButtonSelected, sWidth / 5, sHeight / 2, null);
      masterGraphics.drawImage(exitButtonPlain, (sWidth * 2) / 3, sHeight / 2, null);
    }
    else
    {
      masterGraphics.drawImage(startButtonPlain, sWidth / 5, sHeight / 2, null);
      masterGraphics.drawImage(exitButtonSelected, (sWidth * 2) / 3,  sHeight / 2, null);
    }



    // Draw master image to image at proper scale.
    //graphics.drawImage(masterImage, 0, 0, width, height, null);
  }

//  /**
//   * Redraws the buffered image.
//   */
//  public void redraw()
//  {
//    //clearBuffers();
//    draw();
//  }

  /**
   * Getter for the buffered image.
   * 
   * @return BufferedImage of the title screen.
   */
  public BufferedImage getImage()
  {
    draw();
    //System.out.println("TitleScreen.getImage(): image.size = ("+image.getWidth()+", "+image.getHeight()+")");
    return masterImage;
  }

}
