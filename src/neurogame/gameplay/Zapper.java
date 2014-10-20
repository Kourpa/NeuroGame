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

  private final String name = "zapper";
  private final int timeOn = Library.MIN_FRAME_MILLISEC*5;
  private BufferedImage topImage;
  private BufferedImage bottomImage;

  private final Color BLACK = new Color(0,0,0,0);

  private int spriteWidth;
  private int spriteHeight;

  private BufferedImage zapAreaImage;
  private Graphics2D zapAreaCanvas;
  private int frameDelay;
  private int frameCounter;
  private boolean on;
  private Player player;
  private double threshold = 0.1;
  private boolean hitPlayer;

  private int zapAreaWidth, zapAreaHeight, zapAreaHypotenuse;

  private double zapNodeWorldX, zapNodeWorldTopY, zapNodeWorldBottomY;

  private static final int[] glowColor =
  { 0xFFFFFFFF, 0xFF64FFFF, 0xFF9455CA };

  /**
   * Instantiate a new Zapper at the specified coordinates. A beam will be drawn
   * automatically between the two sprites at points (x1, y1), (x2, y2).
   * @param world
   *          World in which this Zapper will be placed.
   */
  public Zapper(PathVertex vertex, World world)
  {
    super(GameObjectType.ZAPPER, vertex, world);

    //Init globals
    zapNodeWorldX = vertex.getX();
    zapNodeWorldTopY = vertex.getTop();
    zapNodeWorldBottomY = vertex.getBottom() - getHeight();
    on = false;
    hitPlayer = false;
    player = world.getPlayer();

    setLocation(zapNodeWorldX, zapNodeWorldTopY);

    frameDelay = (Library.RANDOM.nextInt(10) + 1) * Library.MIN_FRAME_MILLISEC + timeOn;
    frameCounter = Library.RANDOM.nextInt(frameDelay);


    //Init images once rather then in render
    BufferedImage tempImage = Library.getSprites().get(name);

    AffineTransform transform = AffineTransform.getScaleInstance(.5, .5);
    AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
    bottomImage = op.filter(tempImage, null);

    spriteWidth = bottomImage.getWidth();
    spriteHeight = bottomImage.getHeight();

    AffineTransform rotate = AffineTransform.getRotateInstance(Math.toRadians(180),spriteWidth/2, spriteHeight/2);
    op = new AffineTransformOp(rotate, AffineTransformOp.TYPE_BILINEAR);
    topImage = op.filter(bottomImage, null);

    // Init zap variables
    zapAreaWidth = spriteWidth / 2;
    zapAreaHeight = Library.worldUnitToScreen(zapNodeWorldBottomY - zapNodeWorldTopY);
    zapAreaHeight -= spriteHeight;

    zapAreaHypotenuse = (int) Math.sqrt(zapAreaWidth * zapAreaWidth
      + zapAreaHeight * zapAreaHeight);

    zapAreaImage = new BufferedImage(zapAreaWidth, zapAreaHeight,
      BufferedImage.TYPE_INT_ARGB);
    zapAreaCanvas = zapAreaImage.createGraphics();
    zapAreaCanvas.setBackground(BLACK);
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
    if (getX() + getWidth() < Library.leftEdgeOfWorld) die(false);
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

    if((on) && (Math.abs(player.getCenterX() - (getCenterX())) < threshold)){
      player.loseHealth(player.getCenterX(), player.getCenterY(),
          GameObjectType.ZAPPER.getHitDamage()*deltaTime);
      hitPlayer = true;
    }
    else
    {
      hitPlayer = false;
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
    int zapX = Library.worldPosXToScreen(zapNodeWorldX);
    int zapY1 = Library.worldPosYToScreen(zapNodeWorldTopY);
    int zapY2 = Library.worldPosYToScreen(zapNodeWorldBottomY);

    if (on)
    {
      drawLightning(hitPlayer);
//      drawLightning(false);

      g.drawImage(zapAreaImage, zapX+20, zapY1 + spriteHeight, null); // Offset the X so the lightning is center
    }
    
    g.drawImage(topImage, zapX, zapY1, null);
    g.drawImage(bottomImage, zapX, zapY2, null);
  }

  public void drawLightning(boolean hitPlayer)
  {
    zapAreaCanvas.setBackground(BLACK);
    zapAreaCanvas.clearRect(0, 0, zapAreaWidth, zapAreaHeight);

//    if (hitPlayer)
//    {
//      System.out.println("hitting player");
//      int playerX = Library.worldUnitToScreen(player.getCenterX());
//      int playerY = Library.worldUnitToScreen(player.getCenterY());
//      if (playerX < 1) playerX = 1;
//      if (playerY < 1) playerY = 1;
//      if (playerX >= zapAreaWidth - 1) playerX = zapAreaWidth - 2;
//      if (playerY >= zapAreaHeight - 1) playerY = zapAreaHeight - 2;
//
//      int displace = 1000;
//      drawBolt(1, 1, playerX, playerY, displace);
//      drawBolt(playerX, playerY, zapAreaWidth - 2, zapAreaHeight - 2,
//      displace);
//    }
//    else
//    {
      int displace = zapAreaHypotenuse / 2;
      drawBolt(zapAreaWidth/2, 1, zapAreaWidth/2, zapAreaHeight - 2, displace);
//    }
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
