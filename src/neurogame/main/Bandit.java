package neurogame.main;

import neurogame.io.InputController;
import neurogame.io.SocketToParallelPort;
import neurogame.io.User;
import neurogame.library.Library;
import neurogame.library.SpriteMap;

import javax.swing.*;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

/**
 *
 * Created by Marcos on 11/19/2014.
 */
@SuppressWarnings("serial")
public class Bandit extends JPanel
{

  public static final int ICON_SIZE = 128;
  public final int BUFFER_SIZE = ICON_SIZE + ICON_SIZE / 2;

  private enum BanditState
  {
    WIN, LOSE, IDLE, DONE, EXIT
  }

  private static final String introStr1 = "Outsmart the Artificial Intelligence";
  private static final String introStr2 = "by guessing which door hides the Green Star.";
  
  private boolean running;
  private int score;

  private NeuroGame game;
  private InputController controller;
  private User currentUser;

  private BanditState state = BanditState.IDLE;
  private BufferedImage winImage, loseImage, currentImage;

  private BufferedImage background;
  private BufferedImage foreground;
  private BufferedImage splash;
  private BufferedImage doneScreen;
  private BufferedImage doorTop;
  private BufferedImage doorBottom;

  private Graphics2D graphics;
  private Graphics2D splashGraphics;
  private Stroke selectedStroke;
  private Font fontArcade, fontArial;
  private FontMetrics fontMetricsArial;
  private int fontHeight;

  private static final Color CLEAR = new Color(0, 0, 0, 0);
  private static final Color FOG = new Color(0, 0, 0, 180);
  private static final Color BACKGROUND_COLOR = new Color(27, 66, 92, 255);
  private static final Color DOOR_COLOR = new Color(57, 99, 173, 255);
  private static final Color DOOR_BACKGROUND = new Color(19, 42, 60, 254);
  private static final Color COLOR_WIN = new Color(8, 188, 0, 254);
  private static final Color COLOR_LOSE = new Color(200, 0, 0, 254);
  private static final Color BORDER_COLOR = new Color(12, 21, 32, 254);
  private static final Color COLOR_DOOR_SELECT1 = new Color(170, 140, 255, 255);
  private static final Color COLOR_DOOR_SELECT2 = new Color(152, 125, 228, 255);

  private static final int BETWEEN_DOOR_SPACE = 64;
  private static final int SELECT_STROKE_WIDTH = 5;

  private Point TOP_LEFT_DOOR;
  private Point TOP_RIGHT_DOOR;
  private int DOOR_WIDTH;
  private int DOOR_HEIGHT;
  private int left_offset;
  private int right_offset;
  private final int DOOR_SPEED = 100;
  private int selected;
  

  private int panelWidth, panelHeight;

  private final int TOTAL_EVENTS = 80;
  private final double WIN_PROBILITY = 0.60;
  private final int TOTAL_WIN = (int)(TOTAL_EVENTS * WIN_PROBILITY);
  private final int TOTAL_LOSE = TOTAL_EVENTS - TOTAL_WIN;
  private final int LOW_SCORE_LIMIT = 100;
  private final int SCORE_FOR_WIN = 75;
  
  private double delayTime;
  private double animationDelay = 1;

  private int eventCount;
  private int winCount;
  private int loseCount;

  /**
   * A Bandit is a "gambling" game where the user chooses one of the doors and
   * either receives a winning event or losing event.
   * 
   * @param game
   * @param controller
   */
  public Bandit(NeuroGame game, InputController controller)
  {
    this.game = game;
    this.controller = controller;
    addKeyListener(controller);

    SpriteMap sprites = Library.getSprites();
    winImage = sprites.get("BanditStar");
    loseImage = sprites.get("BanditEnemySinusoidal");

    panelWidth = Library.getWindowPixelWidth();
    panelHeight = Library.getWindowPixelHeight();


    left_offset = 0;
    right_offset = 0;
    selected = 0;

    foreground = new BufferedImage(panelWidth, panelHeight, BufferedImage.TYPE_INT_ARGB);
    graphics = foreground.createGraphics();
    selectedStroke = new BasicStroke(SELECT_STROKE_WIDTH);
    fontArcade = new Font("Karmatic Arcade", Font.PLAIN, 42);
    fontArial = Library.FONT_ARIAL30;
    fontMetricsArial = graphics.getFontMetrics(fontArial);
    fontHeight = fontMetricsArial.getHeight();

    initializedImages();

    setSize(panelWidth, panelHeight);
    setBackground(Color.BLACK);
    setLayout(null);
    requestFocus();
  }

  private void initializedImages()
  {
    background = new BufferedImage(panelWidth, panelHeight, BufferedImage.TYPE_INT_ARGB);
    doneScreen = new BufferedImage(panelWidth, panelHeight, BufferedImage.TYPE_INT_ARGB);
    splash = new BufferedImage(panelWidth, panelHeight, BufferedImage.TYPE_INT_ARGB);
    splashGraphics = splash.createGraphics();
    splashGraphics.setFont(fontArcade);
    splashGraphics.setBackground(CLEAR);
    splashGraphics.setColor(Color.GREEN);
    splashGraphics.clearRect(0, 0, splash.getWidth(), splash.getHeight());
    
    graphics.setFont(fontArial);

    Graphics2D g;

    g = background.createGraphics();
    g.setColor(BACKGROUND_COLOR);
    g.fillRect(0, 0, background.getWidth(), background.getHeight());
    g.setColor(DOOR_BACKGROUND);

    // Draw the side dots
    int i;
    for (i = panelHeight / 16; i < panelHeight - panelHeight / 16; i += panelHeight / 16)
    {
      g.setColor(DOOR_BACKGROUND);
      g.fillOval(panelWidth / 32, i, panelHeight / 32, panelHeight / 32);
      g.fillOval(panelWidth - panelWidth / 32 - panelHeight / 32, i, panelHeight / 32, panelHeight / 32);

      g.setColor(BORDER_COLOR);
      g.drawOval(panelWidth - panelWidth / 32 - panelHeight / 32, i, panelHeight / 32, panelHeight / 32);
      g.drawOval(panelWidth / 32, i, panelHeight / 32, panelHeight / 32);
    }

    // Draw the top dots
    for (int j = panelWidth / 16; j < panelWidth - panelWidth / 16; j += panelWidth / 32)
    {
      g.setColor(DOOR_BACKGROUND);
      g.fillOval(j, panelHeight / 16, panelHeight / 32, panelHeight / 32);
      g.fillOval(j, i - panelHeight / 16, panelHeight / 32, panelHeight / 32);

      g.setColor(BORDER_COLOR);
      g.drawOval(j, panelHeight / 16, panelHeight / 32, panelHeight / 32);
      g.drawOval(j, i - panelHeight / 16, panelHeight / 32, panelHeight / 32);
    }

    g.setColor(DOOR_BACKGROUND);
    g.fillRect(panelWidth / 16, panelHeight / 8, (int) (panelWidth * .875), (int) (panelHeight * .775));

    g.setColor(BORDER_COLOR);
    g.drawRect(panelWidth / 16, panelHeight / 8, (int) (panelWidth * .875), (int) (panelHeight * .775));

    // Clear the slot for the doors.
    g.setBackground(CLEAR);
    DOOR_WIDTH = (int) (panelWidth * .275);
    DOOR_HEIGHT = (int) (panelHeight * .65);
    TOP_LEFT_DOOR = new Point(panelWidth / 10, (int) (panelHeight * .19));
    TOP_RIGHT_DOOR = new Point(TOP_LEFT_DOOR.x + DOOR_WIDTH + BETWEEN_DOOR_SPACE, TOP_LEFT_DOOR.y);

    g.clearRect(TOP_LEFT_DOOR.x, TOP_LEFT_DOOR.y, DOOR_WIDTH, DOOR_HEIGHT);
    g.clearRect(TOP_RIGHT_DOOR.x, TOP_RIGHT_DOOR.y, DOOR_WIDTH, DOOR_HEIGHT);

    doorTop = new BufferedImage(DOOR_WIDTH, DOOR_HEIGHT, BufferedImage.TYPE_INT_ARGB);
    doorBottom = new BufferedImage(DOOR_WIDTH, DOOR_HEIGHT, BufferedImage.TYPE_INT_ARGB);

    // lets do fancy stuff?
    Path2D top = new Path2D.Double();
    Stroke thicker = new BasicStroke(5);

    top.moveTo(0, 0);
    top.lineTo(0, DOOR_HEIGHT / 2);
    top.lineTo(DOOR_WIDTH / 32, DOOR_HEIGHT / 2);
    top.lineTo(DOOR_WIDTH / 16, DOOR_HEIGHT / 3);
    top.lineTo(DOOR_WIDTH / 8, DOOR_HEIGHT / 3);
    top.lineTo(DOOR_WIDTH / 4, DOOR_HEIGHT / 2 + DOOR_HEIGHT / 8);
    top.lineTo(DOOR_WIDTH / 2, DOOR_HEIGHT / 2 + DOOR_HEIGHT / 8);
    top.lineTo(DOOR_WIDTH / 2 + DOOR_WIDTH / 4, DOOR_HEIGHT / 2 + DOOR_HEIGHT / 8);
    top.lineTo(DOOR_WIDTH / 2 + DOOR_WIDTH / 4 + DOOR_WIDTH / 8, DOOR_HEIGHT / 3);
    top.lineTo(DOOR_WIDTH / 2 + DOOR_WIDTH / 4 + DOOR_WIDTH / 8 + DOOR_WIDTH / 16, DOOR_HEIGHT / 3);
    top.lineTo(DOOR_WIDTH / 2 + DOOR_WIDTH / 4 + DOOR_WIDTH / 8 + DOOR_WIDTH / 16 + DOOR_WIDTH / 32, DOOR_HEIGHT / 2);
    top.lineTo(DOOR_WIDTH, DOOR_HEIGHT / 2);
    top.lineTo(DOOR_WIDTH, 0);
    top.closePath();

    Path2D bottom = new Path2D.Double();
    bottom.moveTo(0, DOOR_HEIGHT);
    bottom.lineTo(0, DOOR_HEIGHT / 2);
    bottom.lineTo(DOOR_WIDTH / 32, DOOR_HEIGHT / 2);
    bottom.lineTo(DOOR_WIDTH / 16, DOOR_HEIGHT / 3);
    bottom.lineTo(DOOR_WIDTH / 8, DOOR_HEIGHT / 3);
    bottom.lineTo(DOOR_WIDTH / 4, DOOR_HEIGHT / 2 + DOOR_HEIGHT / 8);
    bottom.lineTo(DOOR_WIDTH / 2, DOOR_HEIGHT / 2 + DOOR_HEIGHT / 8);
    bottom.lineTo(DOOR_WIDTH / 2 + DOOR_WIDTH / 4, DOOR_HEIGHT / 2 + DOOR_HEIGHT / 8);
    bottom.lineTo(DOOR_WIDTH / 2 + DOOR_WIDTH / 4 + DOOR_WIDTH / 8, DOOR_HEIGHT / 3);
    bottom.lineTo(DOOR_WIDTH / 2 + DOOR_WIDTH / 4 + DOOR_WIDTH / 8 + DOOR_WIDTH / 16, DOOR_HEIGHT / 3);
    bottom
        .lineTo(DOOR_WIDTH / 2 + DOOR_WIDTH / 4 + DOOR_WIDTH / 8 + DOOR_WIDTH / 16 + DOOR_WIDTH / 32, DOOR_HEIGHT / 2);
    bottom.lineTo(DOOR_WIDTH, DOOR_HEIGHT / 2);
    bottom.lineTo(DOOR_WIDTH, DOOR_HEIGHT);
    bottom.closePath();

    g = doorTop.createGraphics();
    g.setStroke(thicker);

    g.setColor(DOOR_COLOR);
    g.fill(top);
    g.setColor(Color.LIGHT_GRAY);
    g.draw(top);

    g = doorBottom.createGraphics();
    g.setStroke(thicker);

    g.setColor(DOOR_COLOR);
    g.fill(bottom);
    g.setColor(Color.LIGHT_GRAY);
    g.draw(bottom);

    g = doneScreen.createGraphics();
    g.setBackground(FOG);
    g.setFont(Library.FONT36);
    g.setColor(COLOR_WIN);
    g.clearRect(0, 0, doneScreen.getWidth(), doneScreen.getHeight());
    g.drawString("Congradulations, you got enough credits to ", panelWidth / 16, panelHeight / 8);
    g.drawString("purchase your Delton starfighter. ", panelWidth / 16, panelHeight / 8 + 42);

  }

  public void init(User currentUser)
  {
    this.currentUser = currentUser;
    state = BanditState.IDLE;
    running = true;
    eventCount = 0;
    winCount = 0;
    loseCount = 0;
    score = 0;
    
    currentImage = winImage;
    

    // just in case?
    panelWidth = Library.getWindowPixelWidth();
    panelHeight = Library.getWindowPixelHeight();

    setSize(panelWidth, panelHeight);

    selected = 0;

    repaint();
    setVisible(true);
   
    if (currentUser.isLogging())
    {
      game.log.sendByteBySocket(SocketToParallelPort.TRIGGER_BANDIT_START);
    }

    animate(0);


  }

  /**
   * Update and redraw everything
   * 
   * @param deltaSec
   * @return running
   */
  public boolean banditUpdate(double deltaSec)
  {
    this.requestFocus();

    if (state != BanditState.DONE)
    {
      if (state == BanditState.IDLE)
      {
        if (controller.isPlayerPressingESC())
        {
          state = BanditState.EXIT;
          running = false;
        }
        else
        {
          if (controller.getPlayerInputDirectionVector().x != 0)
          {
            if (controller.getPlayerInputDirectionVector().x > 0)
            {
              selected = 1;
            }
            else
            {
              selected = 0;
            }
          }
          else if (controller.isPlayerPressingButton())
          {
            nextEvent();
          }
        }
      }
    }
    else
    {
      if (controller.isPlayerPressingESC() || controller.isPlayerPressingButton())
      {
        running = false;
      }
    }

    animate(deltaSec);

    return running;
  }

  /**
   * Generate the next event along with the timing intervals. The timing is not
   * based on actual time instead it is based on the a number of spins for each
   * counter.
   */
  private void nextEvent()
  {
    if (eventCount >= TOTAL_EVENTS)
    {
      state = BanditState.DONE;
      if (currentUser.isLogging())
      {
        game.log.sendByteBySocket(SocketToParallelPort.TRIGGER_BANDIT_DONE);
      }
    }
    else
    {
      eventCount++;

      if (winCount >= TOTAL_WIN)
      {
        state = BanditState.LOSE;
        loseCount++;
      }
      else if (loseCount >= TOTAL_LOSE)
      {
        state = BanditState.WIN;
        winCount++;
      }
      else if (score < LOW_SCORE_LIMIT)
      {
        state = BanditState.WIN;
        winCount++;
      }
      else if (Library.RANDOM.nextDouble() > winCount / (eventCount + 1.0))
      {
        state = BanditState.WIN;
        winCount++;
      }
      else
      {
        state = BanditState.LOSE;
        loseCount++;
      }

      if (state == BanditState.WIN)
      {
    	currentImage = winImage;
        if (currentUser.isLogging())
        {
          game.log.sendByteBySocket(SocketToParallelPort.TRIGGER_BANDIT_WIN);
        }
      }
      else
      {
    	currentImage = loseImage;
        if (currentUser.isLogging())
        {
          game.log.sendByteBySocket(SocketToParallelPort.TRIGGER_BANDIT_LOSE);
        }
      }
    }
  }

  /**
   * Animate the spinners and send signals to the parallel port when the
   * spinners have stopped.
   */
  private void animate(double deltaTime)
  {
    boolean done = false;
    if (state == BanditState.WIN || state == BanditState.LOSE) done = openDoor();

    graphics.setColor(DOOR_BACKGROUND);
    graphics.fillRect(0, 0, panelWidth, panelHeight);
    
    if (done)
    {
      done = drawResult(deltaTime, selected, state);
    }
   
    if (done)
    {
      left_offset = right_offset = 0;

      if (state == BanditState.WIN)
      {
        score += SCORE_FOR_WIN;
      }
      else if (state == BanditState.LOSE)
      {
        if (currentUser.isLogging())
        {
          game.log.sendByteBySocket(SocketToParallelPort.TRIGGER_BANDIT_LOSE);
        }
        score -= SCORE_FOR_WIN;
      }

      state = BanditState.IDLE;
    }
    // Attemp to clear the buffer if no signal has been sent this tick
    // else{
    // game.log.sendByteBySocket(SocketToParallelPort.TRIGGER_SIGNAL_GROUND);
    // }


    if (state != BanditState.IDLE)
    { 
      int x,y;
      if (selected == 0)
      {
        x = TOP_LEFT_DOOR.x + DOOR_WIDTH / 2 - currentImage.getWidth() / 2;
        y = TOP_LEFT_DOOR.y + DOOR_HEIGHT / 2 - currentImage.getHeight() / 2;
      }
      else
      {
        x = TOP_RIGHT_DOOR.x + DOOR_WIDTH / 2 - currentImage.getWidth() / 2;
        y = TOP_RIGHT_DOOR.y + DOOR_HEIGHT / 2 - currentImage.getHeight() / 2;  
      }
      graphics.drawImage(currentImage, x, y, null);
    }

    // Left door
    graphics.drawImage(doorTop, TOP_LEFT_DOOR.x, TOP_LEFT_DOOR.y - left_offset, null);
    graphics.drawImage(doorBottom, TOP_LEFT_DOOR.x, TOP_LEFT_DOOR.y + left_offset, null);

    // Right door
    graphics.drawImage(doorTop, TOP_RIGHT_DOOR.x, TOP_RIGHT_DOOR.y - right_offset, null);
    graphics.drawImage(doorBottom, TOP_RIGHT_DOOR.x, TOP_RIGHT_DOOR.y + right_offset, null);

    graphics.drawImage(background, 0, 0, null);

    int doorSelectLeft = TOP_LEFT_DOOR.x - 16;
    int doorSelectTop =  TOP_LEFT_DOOR.y - 16;
    
    

    if (selected == 1)
    {
      doorSelectLeft = TOP_RIGHT_DOOR.x - 16;
      doorSelectTop =  TOP_RIGHT_DOOR.y - 16;
    }

    graphics.setStroke(selectedStroke);
    graphics.setColor(COLOR_DOOR_SELECT2);
    graphics.drawRect(doorSelectLeft, doorSelectTop, DOOR_WIDTH + 32, DOOR_HEIGHT + 32);
    
    graphics.setColor(COLOR_DOOR_SELECT1);
    graphics.drawRect(doorSelectLeft+SELECT_STROKE_WIDTH, doorSelectTop+SELECT_STROKE_WIDTH, DOOR_WIDTH + 32-SELECT_STROKE_WIDTH*2, DOOR_HEIGHT + 32-SELECT_STROKE_WIDTH*2);
    
    drawScore();
    graphics.drawImage(splash, 0, 0, null);

    graphics.setFont(Library.FONT_ARIAL30);
    graphics.drawString(introStr1, 50, 70);
    graphics.drawString(introStr2, 50, 130);

    if (state == BanditState.DONE)
    {
      graphics.drawImage(doneScreen, 0, 0, null);
    }

    repaint();
  }

  /**
   * animate the doors opening
   * 
   * @return true if the doors are fully open
   */
  private boolean openDoor()
  {
    boolean done = true;

    if (selected == 0)
    {
      left_offset += DOOR_SPEED;
      if (left_offset < DOOR_HEIGHT / 2) done = false;
    }
    else
    {
      right_offset += DOOR_SPEED;
      if (right_offset < DOOR_HEIGHT / 2) done = false;
    }

    return done;
  }

  /**
   * Draw whatever the current score is to the screen.
   */
  private void drawScore()
  {
    //graphics.setColor(DOOR_COLOR);
    //graphics.fillRect(panelWidth - (int) (panelWidth * .29), panelHeight / 4, panelWidth / 5, panelHeight / 10);
    graphics.setColor(COLOR_WIN);
    graphics.setFont(fontArcade);
    graphics.drawString("Score", panelWidth - (int) (panelWidth * .26), (int) (panelHeight * .23));
    graphics.drawString(Integer.toString(score), panelWidth - (int) (panelWidth * .24), (int) (panelHeight * .32));
  }
  
  
  private boolean drawResult(double deltaTime, int door, BanditState result)
  {
    boolean done = false;
   
    delayTime += deltaTime;
    if (delayTime > animationDelay)
    {
      done = true;
      delayTime = 0;
    }
    else
    {
      int centerOfDoor, x1, x2, y;
      String msg1 = "YOU WIN!!";
      String msg2 = "+" + String.valueOf(SCORE_FOR_WIN) + " Credits";
      if (result == BanditState.WIN)
      {
        graphics.setColor(COLOR_WIN);
      }
      else
      {
        graphics.setColor(COLOR_LOSE);
        msg1 = "YOU LOSE";
        msg2 = "-" + String.valueOf(SCORE_FOR_WIN) + " Credits";
      }
      
      if (door == 0) centerOfDoor = TOP_LEFT_DOOR.x + DOOR_WIDTH / 2;
      else centerOfDoor = TOP_RIGHT_DOOR.x + DOOR_WIDTH / 2;
     
      x1 = centerOfDoor - fontMetricsArial.stringWidth(msg1) / 2;
      x2 = centerOfDoor - fontMetricsArial.stringWidth(msg2) / 2;

      y = TOP_LEFT_DOOR.y + DOOR_HEIGHT / 2 + loseImage.getHeight() + fontHeight;
      graphics.drawString(msg1, x1, y);
      graphics.drawString(msg2, x2, y+fontHeight+10);
      

    }
    return done;
  }

//  private boolean drawWin(double deltaTime)
//  {
//    boolean done = false;
//    int x, y;
//    delayTime += deltaTime;
//    if (delayTime > animationDelay)
//    {
//      done = true;
//      delayTime = 0;
//      splashGraphics.clearRect(0, 0, splash.getWidth(), splash.getHeight());
//    }
//    else
//    {
//      if (Library.RANDOM.nextDouble() > .6)
//      {
//        x = Library.RANDOM.nextInt(splash.getWidth() - 100);
//        y = Library.RANDOM.nextInt(splash.getHeight() - 100);
//        splashGraphics.setColor(Color.GREEN);
//        splashGraphics.drawString("YOU WIN", x, y);
//      }
//    }
//    return done;
//  }
//
//  private boolean drawLose(double deltaTime)
//  {
//    boolean done = false;
//    delayTime += deltaTime;
//    if (delayTime > animationDelay)
//    {
//      done = true;
//      delayTime = 0;
//      splashGraphics.clearRect(0, 0, splash.getWidth(), splash.getHeight());
//    }
//    else
//    {
//      splashGraphics.setColor(Color.RED);
//      splashGraphics.drawString("YOU LOSE", splash.getWidth() / 2, splash.getHeight() / 2);
//    }
//
//    return done;
//  }

  @Override
  public void paint(Graphics g)
  {
    g.drawImage(foreground, 0, 0, null);
  }
}
