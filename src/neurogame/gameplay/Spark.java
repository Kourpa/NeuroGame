package neurogame.gameplay;

import java.awt.Color;
import java.awt.Graphics2D;

import neurogame.level.World;
import neurogame.library.Library;

public class Spark
{
  private double sizeInPixels, deltaSize;

  private double speedX, speedY;
  private double posX, posY;

  private static final int YELLOW_R = 236;
  private static final int YELLOW_G = 240;
  private static final int YELLOW_B = 41;

  private static final int ORANGE_R = 253;
  private static final int ORANGE_G = 156;
  private static final int ORANGE_B = 33;

  private int age = 0;
  private int deathAge;
  private int r,g,b;

  public Spark(double x, double y, World world)
  {

    sizeInPixels = 5.0*Library.RANDOM.nextDouble() + 1.0;
    deltaSize = 3.0*Library.RANDOM.nextDouble() + 0.25;

    posX = x;
    posY = y;

    speedX = (Library.RANDOM.nextDouble() - 0.5) / 20.0;
    speedY = (Library.RANDOM.nextDouble() - 0.5) / 20.0;
    deathAge = Library.RANDOM.nextInt(15) + 5;
    
    double w1 = Library.RANDOM.nextDouble();
    double w2 = 1.0 - w1;
    
    r = (int) (w1 * ORANGE_R + w2 * YELLOW_R);
    g = (int) (w1 * ORANGE_G + w2 * YELLOW_G);
    b = (int) (w1 * ORANGE_B + w2 * YELLOW_B);
    
    
  }

  public boolean update()
  {
    //System.out.println("Spark.update()");
    age++;
    sizeInPixels += deltaSize;

    posX += speedX;
    posY += speedY;

    if (age > deathAge) return false;
    
    //canvas.fillRect(x0 - pixelSize / 2, y0 - pixelSize / 2, pixelSize,pixelSize
    int x0 = Library.worldPosXToScreen(posX);
    int y0 = Library.worldPosYToScreen(posY);
   
    int halfSize = (int)(sizeInPixels/2);
    
    
    if ((x0 + halfSize < 0) || (x0 - halfSize > Library.getWindowPixelWidth())) return false;
    if ((y0 + halfSize < 0) || (y0 - halfSize > Library.getWindowPixelHeight())) return false;
    return true;
  }

  public void render(Graphics2D canvas)
  {
   //System.out.println("   Spark.render()");
    
    int alpha = (int) (255.0 * (1.0 - age / (deathAge + 5.0)));

    int x0 = Library.worldPosXToScreen(posX);
    int y0 = Library.worldPosYToScreen(posY);
    //System.out.println("     spark: x0="+x0 + ", y0="+y0+", size="+sizeInPixels +",  pixelDeltaSize=" +deltaSize + ", age="+age+"/"+deathAge);


    canvas.setColor(new Color(r, g, b, alpha));
    int size = (int)sizeInPixels;
    canvas.fillRect(x0 - size/2, y0 - size/2, size, size);

    //int size2 = sizeInPixels +2;
    //canvas.setColor(new Color(YELLOW_R, YELLOW_G, YELLOW_B, alpha));
    //canvas.drawRect(x0-size2/2, y0-size2/2, size2, size2);
  }
}
