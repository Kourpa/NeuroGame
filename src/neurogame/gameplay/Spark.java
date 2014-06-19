package neurogame.gameplay;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;

import neurogame.level.World;
import neurogame.library.Library;

public class Spark
{
  private int pixelSize, pixelDeltaSize;

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
  private World world;

  public Spark(double x, double y, World world)
  {
    this.world = world;

    pixelSize = Library.worldUnitToScreen(0.001);
    pixelDeltaSize = Library
        .worldUnitToScreen((Library.RANDOM.nextDouble() + 0.5) / 1000.0);

    posX = x;
    posY = y;

    speedX = (Library.RANDOM.nextDouble() - 0.5) / 20.0;
    speedY = (Library.RANDOM.nextDouble() - 0.5) / 20.0;
    deathAge = Library.RANDOM.nextInt(15) + 10;
  }

  public boolean move()
  {
    age++;
    pixelSize += pixelDeltaSize;

    posX += speedX;
    posY += speedY;

    if (age > deathAge) return false;
    return true;
  }

  public void render(Graphics2D canvas)
  {
    int alpha = (int) (255.0 * (1.0 - age / (deathAge + 5.0)));

    int x0 = Library.worldPosXToScreen(posX);
    int y0 = Library.worldPosYToScreen(posY);

    double w1 = Library.RANDOM.nextDouble();
    double w2 = 1.0 - w1;

    int r = (int) (w1 * ORANGE_R + w2 * YELLOW_R);
    int g = (int) (w1 * ORANGE_G + w2 * YELLOW_G);
    int b = (int) (w1 * ORANGE_B + w2 * YELLOW_B);
    canvas.setColor(new Color(r, g, b, alpha));
    canvas.fillRect(x0 - pixelSize / 2, y0 - pixelSize / 2, pixelSize,
        pixelSize);

    int size2 = pixelSize +2;
    canvas.setColor(new Color(YELLOW_R, YELLOW_G, YELLOW_B, alpha));
    canvas.drawRect(x0-size2/2, y0-size2/2, size2, size2);
  }
}
