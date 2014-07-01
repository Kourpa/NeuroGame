package neurogame.main;

import neurogame.library.Library;
import org.lwjgl.Sys;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author kourpa
 */
public class Oddball
{
  private long time;
  private final int numberOfGoodScreens = 50;
  private final int numberOfBadScreens = 200;
  private final ArrayList<Screen> options;
  private Screen currentScreen;
  private BufferedImage badImage, goodImage, waitImage;

  private long startTime, screenTime, waitTime;

  private boolean wait, instructions;

  public Oddball(BufferedImage badImage, BufferedImage goodImage, BufferedImage waitImage){
    wait = instructions = false;
    startTime = System.currentTimeMillis();
    screenTime = 400 + Library.RANDOM.nextInt(400);
    waitTime = 400 + Library.RANDOM.nextInt(400);
    time = startTime;
    options = new ArrayList<>();

    this.badImage = badImage;
    this.goodImage = goodImage;
    this.waitImage = waitImage;

    for(int i = 0; i < numberOfGoodScreens; i++){
      options.add(Screen.GOOD);
    }

    for(int i = 0; i < numberOfBadScreens; i++){
      options.add(Screen.BAD);
    }

    Collections.shuffle(options);
    instructions = true;
  }

  public void update(long deltaTime){
    time += deltaTime;

    if(instructions)
    {

    }
    else if(wait)
    {
      if(startTime - time > waitTime){
        wait = false;
        currentScreen = options.remove(0);
        startTime = System.currentTimeMillis();
        screenTime = 400 + Library.RANDOM.nextInt(400);
        waitTime = 400 + Library.RANDOM.nextInt(400);
      }
    }
    else if(startTime - time > screenTime) wait = true;

  }

  public void render(Graphics2D g)
  {
    switch(currentScreen)
    {
      case GOOD:
        g.drawImage(goodImage, 0, 0, null);
        break;
      case BAD:
        g.drawImage(badImage, 0, 0, null);
        break;
      case WAIT:
        g.drawImage(waitImage, 0, 0, null);
        break;
    }
  }

  private enum Screen{
    GOOD, BAD, WAIT;
  }
}
