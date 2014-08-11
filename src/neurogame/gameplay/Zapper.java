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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import neurogame.level.PathVertex;
import neurogame.level.World;
import neurogame.library.Library;

/**
 * A Zapper object for NeuroGame.
 * 
 * @author Ramon A. Lovato
 * @team Danny Gomez
 * @team Marcos Lemus
 */
public class Zapper extends Enemy
{
  public static final int spriteWidth = 64;
  public static final int spriteHeight = 64;
  public static final double width = Library.worldUnitToScreen(spriteWidth);
  public static final double height = Library.worldUnitToScreen(spriteHeight);
  private static final String name = "zapper";
  private static final int timeOn = Library.MIN_FRAME_MILLISEC*20;
  private static final BufferedImage masterImage = Library.getSprites().get(name);
  private BufferedImage zapAreaImage;
  private Graphics2D zapAreaCanvas;
  private int frameDelay;
  private int frameCounter;
  private boolean on;
  private Player player;
  private double threshold = 0.1;

  // private int zapX1, zapY1, zapX2, zapY2, zapPlayerX, zapPlayerY, zapMinX,
  // zapMinY, zapAreaWidth, zapAreaHeight;
  private int zapAreaWidth, zapAreaHeight, zapAreaHypotenuse;
  private double zapNodeWorldX1, zapNodeWorldY1, zapNodeWorldX2,
      zapNodeWorldY2, zapWorldMinX, zapWorldMinY;

  // private static final Color[] glowColor = {Color.WHITE, new Color(237, 182,
  // 240), new Color(148, 85, 202)};
  private static final int[] glowColor =
  { 0xFFFFFFFF, 0xFF64FFFF, 0xFF9455CA };

  /**
   * Instantiate a new Zapper at the specified coordinates. A beam will be drawn
   * automatically between the two sprites at points (x1, y1), (x2, y2).
   * 
   * @param x1
   *          X-coordinate of first node
   * @param y1
   *          Y-coordinate of first node.
   * @param x2
   *          X-coordinate of second node.
   * @param y2
   *          Y-coordinate of second node.
   * @param world
   *          World in which this Zapper will be placed.
   */
  public Zapper(PathVertex vertex, World world)
  {
    super(GameObjectType.ZAPPER, vertex, world);
    
    zapNodeWorldX1 = vertex.getX();
    zapNodeWorldX2 = vertex.getX();
    zapNodeWorldY1 = vertex.getTop();
    zapNodeWorldY2 = vertex.getBottom() - GameObjectType.ZAPPER.getHeight();
    
    setLocation(zapNodeWorldX1, zapNodeWorldY1);
    
    // image = new BufferedImage(spriteWidth, spriteHeight,
    // BufferedImage.TYPE_INT_ARGB);
    // graphics = image.createGraphics();
    // graphics.drawImage(masterImage, 0, 0, spriteWidth, spriteHeight, null);
    
    frameDelay = (Library.RANDOM.nextInt(30) + 1) * Library.MIN_FRAME_MILLISEC
        + timeOn;
    frameCounter = Library.RANDOM.nextInt(frameDelay);
    on = false;
    player = world.getPlayer();

    // image = new BufferedImage(spriteWidth, spriteHeight,
    // BufferedImage.TYPE_INT_ARGB);
    // graphics = image.createGraphics();

    double dx = Math.abs(zapNodeWorldX1 - zapNodeWorldX2);
    double dy = Math.abs(zapNodeWorldY1 - zapNodeWorldY2);

    zapAreaWidth = (int) (dx * Library.U_VALUE) + spriteWidth;
    zapAreaWidth = zapAreaWidth / 2;
    zapAreaHeight = (int) (dy * Library.U_VALUE) + spriteHeight;

    zapAreaHypotenuse = (int) Math.sqrt(zapAreaWidth * zapAreaWidth
        + zapAreaHeight * zapAreaHeight);

    zapWorldMinX = Math.min(zapNodeWorldX1, zapNodeWorldX2);
    zapWorldMinY = Math.min(zapNodeWorldY1, zapNodeWorldY2);

    // System.out.println("zapAreaWidth="+zapAreaWidth+", zapAreaHeight="+zapAreaHeight);
    zapAreaImage = new BufferedImage(zapAreaWidth, zapAreaHeight,
        BufferedImage.TYPE_INT_RGB);
    zapAreaCanvas = zapAreaImage.createGraphics();
  }

  /**
   * Getter for the x-coordinate of the second node.
   * 
   * @return X-coordinate of the second node in u-scale (double).
   */
  public double getX2()
  {
    return zapNodeWorldX2;
  }

  /**
   * Getter for the y-coordinate of the second node.
   * 
   * @return Y-coordinate of the second node in u-scale (double).
   */
  public double getY2()
  {
    return zapNodeWorldY2;
  }

  /**
   * Turn the Zapper on.
   */
  public void turnOn()
  {
    on = true;
    // hitBox = getHitBox();
  }

  /**
   * Turn the Zapper off.
   */
  public void turnOff()
  {
    on = false;
    // hitBox = getHitBox();
  }

  
  /**
   * Override of GameObject's update.
   */
  @Override
  public void update(double deltaTime, double scrollDistance)
  {
    if (getX() < Library.leftEdgeOfWorld) die(false);
    if (!isAlive()) return;
    
    if (++frameCounter >= frameDelay)
    {
      frameCounter = 0;
      turnOn();
    }

    if (frameCounter > timeOn)
    {
      turnOff();
    }

    if((on) && (Math.abs(player.getCenterX() - this.getX()) < threshold)){
      player.loseHealth(player.getCenterX(), player.getCenterY(),
          GameObjectType.ZAPPER.getHitDamage()*deltaTime);
    }

  }

  public void hit(GameObject obj)
  { die(false);
  }
  
  public boolean checkCollisionWithWall()
  {
    return false;
  }
   
  public void render(Graphics2D g)
  {

    int zapMinX = Library.worldPosXToScreen(zapWorldMinX);
    int zapMinY = Library.worldPosYToScreen(zapWorldMinY);

    int zapX1 = Library.worldPosXToScreen(zapNodeWorldX1);
    int zapY1 = Library.worldPosYToScreen(zapNodeWorldY1);
    int zapX2 = Library.worldPosXToScreen(zapNodeWorldX2);
    int zapY2 = Library.worldPosYToScreen(zapNodeWorldY2);

    if (on)
    {
      drawLightning(collision(player));
      drawLightning(false);

      g.drawImage(zapAreaImage, zapMinX+20, zapMinY, null); // Offset the X so the lightning is center
    }
    
    // Rotate 180 to face down
    AffineTransform tx = AffineTransform.getRotateInstance(Math.toRadians(180), spriteWidth/2, spriteHeight/2);
    tx.scale(0.5, 0.5);
    AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
    
    //g.drawImage(masterImage, zapX1, zapY1, spriteWidth, spriteHeight, null);
    g.drawImage(op.filter(masterImage, null), zapX1, zapY1, null);
    
    g.drawImage(masterImage, zapX2, zapY2, spriteWidth, spriteHeight, null);
  }

  public void drawLightning(boolean hitPlayer)
  {
    // graphics = g;
    zapAreaCanvas.setColor(Color.BLACK);
    zapAreaCanvas.fillRect(0, 0, zapAreaWidth, zapAreaHeight);

    // if (hitPlayer)
    // {
    //
    // int playerX = Library.worldToScreen(player.getX() / player.getWidth());
    // int playerY = Library.worldToScreen(player.getY() / player.getHeight());
    // if (playerX < 1) playerX = 1;
    // if (playerY < 1) playerY = 1;
    // if (playerX >= zapAreaWidth - 1) playerX = zapAreaWidth - 2;
    // if (playerY >= zapAreaHeight - 1) playerY = zapAreaHeight - 2;
    //
    // int displace = zapAreaHypotenuse / 2;
    // drawBolt(1, 1, playerX, playerY, displace);
    // drawBolt(playerX, playerY, zapAreaWidth - 2, zapAreaHeight - 2,
    // displace);
    // }
    // else
    // {
    int displace = zapAreaHypotenuse / 2;
    drawBolt(zapAreaWidth/2, 1, zapAreaWidth/2, zapAreaHeight - 2, displace);
    // }
    // myPic.repaint();
  }

  public void drawBolt(int x1, int y1, int x2, int y2, int displace)
  {	    
    int dx = Math.abs(x1 - x2);
    int dy = Math.abs(y1 - y2);

    if (dx <= 1 && dy <= 1)
    {

      mergeRGB(x1, y1 - 1, glowColor[0]);
      mergeRGB(x2, y2 + 1, glowColor[0]);
      mergeRGB(x1 - 1, y1, glowColor[0]);
      mergeRGB(x2 + 1, y2, glowColor[0]);

      mergeRGB(x1, y1, glowColor[0]);
      mergeRGB(x2, y2, glowColor[0]);

      mergeRGB(x1, y1 + 1, glowColor[0]);
      mergeRGB(x2, y2 - 1, glowColor[0]);
      mergeRGB(x1 + 1, y1, glowColor[0]);
      mergeRGB(x2 - 1, y2, glowColor[0]);

      mergeRGB(x1 + 1, y1 + 1, glowColor[0]);
      mergeRGB(x2 + 1, y2 + 1, glowColor[0]);
      mergeRGB(x1 - 1, y1 - 1, glowColor[0]);
      mergeRGB(x2 - 1, y2 - 1, glowColor[0]);

      mergeRGB(x1 - 1, y1 + 1, glowColor[2]);
      mergeRGB(x2 + 1, y2 - 1, glowColor[2]);
      mergeRGB(x1 + 1, y1 - 1, glowColor[2]);
      mergeRGB(x2 - 1, y2 + 1, glowColor[2]);

    }
    else
    {
      int x = (x2 + x1) / 2;
      int y = (y2 + y1) / 2;
            
      y += (Library.RANDOM.nextDouble() - .5) * displace;
      x += (Library.RANDOM.nextDouble() - .5) * displace;

      if (x < 1) x = 1;
      if (y < 1) y = 1;
      if (x >= zapAreaWidth - 1) x = zapAreaWidth - 2;
      if (y >= zapAreaHeight - 1) y = zapAreaHeight - 2;

      drawBolt(x1, y1, x, y, displace / 2);
      drawBolt(x2, y2, x, y, displace / 2);
    }
  }

  private void mergeRGB(int x, int y, int rgb2)
  {

    int rgb = zapAreaImage.getRGB(x, y);

    int r1 = (rgb & 0x00FF0000) >> 16;
    int g1 = (rgb & 0x0000FF00) >> 8;
    int b1 = rgb & 0x000000FF;

    int r2 = (rgb2 & 0x00FF0000) >> 16;
    int g2 = (rgb2 & 0x0000FF00) >> 8;
    int b2 = rgb2 & 0x000000FF;

    r2 = (r1 + r2) / 2;
    g2 = (g1 + g2) / 2;
    b2 = (b1 + b2) / 2;

    rgb2 = (r2 << 16) | (g2 << 8) | b2 | 0xFF000000;

    zapAreaImage.setRGB(x, y, rgb2);

  }
}
