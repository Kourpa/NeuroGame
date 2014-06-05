/**
 * NeuroGame.
 * CS 351, Project 3
 * 
 * Team members:
 * Ramon A. Lovato
 * Danny Gomez
 * Marcos Lemus
 */

package neurogame.gameplay;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import neurogame.level.World;
import neurogame.library.Library;

/**
 * A Coin object for NeuroGame.
 * 
 * @author Ramon A. Lovato
 * @team Danny Gomez
 * @team Marcos Lemus
 */
public class Coin extends GameObject
{
  public static final double WIDTH = 0.05;
  public static final double HEIGHT = 0.05;
  private static final String name = "coin";
  private static final BufferedImage image = Library.getSprites().get(name);
  private static final int spriteWidth = 64;
  private static final int spriteHeight = 64;
  private static final int spriteSheetHeight = 384;
  private static final int animationFrames = 6;
  private int frameCounter;
  private int spriteX;
  private int spriteY;

  /**
   * Instantiate a new Coin at the specified coordinates.
   * 
   * @param x
   *          X-coordinate at which to place the Coin.
   * @param y
   *          Y-coordinate at which to place the coin.
   * @param world
   *          World in which to place the coin.
   */
  public Coin(double x, double y, World world)
  {
    super(x, y, WIDTH, HEIGHT, name, image, world);
    frameCounter = Library.RANDOM.nextInt(animationFrames);
    spriteX = 0;
    spriteY = 0;
  }

  /**
   * Override of GameObject's update - advances the frameCounter and which
   * sprite is being clipped from the sheet.
   */

  public void update(long deltaTime)
  {
    // check collision with player
    if (collision(world.getPlayer()) && isActive())
    {
      world.getPlayer().collectCoin();
      setActive(false);
    }

    // Animation.
    frameCounter++;

    if (frameCounter % animationFrames == 0)
    {
      if (spriteY + spriteHeight >= spriteSheetHeight)
      {
        spriteY = 0;
      }
      else
      {
        spriteY += spriteHeight;
      }
    }
  }


  public void render(Graphics2D g)
  {
    int dx = Library.worldToScreen(getX() - world.getDeltaX());
    int dy = Library.worldToScreen(getY());
    Image im = image.getSubimage(spriteX, spriteY, spriteWidth, spriteHeight);
    g.drawImage(im, dx, dy, Library.worldToScreen(WIDTH),
        Library.worldToScreen(HEIGHT), null);
  }

}
