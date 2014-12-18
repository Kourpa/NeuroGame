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
public class Bandit extends JPanel {

  public static final int ICON_SIZE = 128;
  public final int BUFFER_SIZE = ICON_SIZE + ICON_SIZE/2;

  private enum BanditState {
    WIN, LOSE, IDLE, INTRO, DONE, EXIT
  }

  private boolean running;
  private int score;

  private NeuroGame game;
  private InputController controller;
  private User currentUser;

  private BanditState state = BanditState.INTRO;
  private BufferedImage[] images;
  private int selectedImage;

  private BufferedImage background;
  private BufferedImage foreground;
  private BufferedImage splash;
  private BufferedImage intro;
  private BufferedImage doneScreen;
  private BufferedImage doorTop;
  private BufferedImage doorBottom;

  private Graphics2D graphics;
  private Graphics2D splashGraphics;
  private Stroke selectedStroke;
  private Font font;

  private final Color CLEAR = new Color(0, 0, 0, 0);
  private final Color FOG = new Color(0, 0, 0, 180);
  private final Color BACKGROUND_COLOR = new Color(27, 66, 92, 255);
  private final Color DOOR_COLOR = new Color(57, 99, 173, 255);
  private final Color DOOR_BACKGROUND = new Color(19, 42, 60, 254);
  private final Color BORDER_COLOR = new Color(12, 21, 32, 254);
  private final Color FONT_COLOR = new Color(8, 188, 0, 254);
  private final Color FONT_BLINK_COLOR = new Color(0, 44, 40, 254);

  private int introXOffset;
  private int introYOffset;

  private Point TOP_LEFT_DOOR;
  private Point TOP_RIGHT_DOOR;
  private int DOOR_WIDTH;
  private int DOOR_HEIGHT;
  private int left_offset;
  private int right_offset;
  private final int DOOR_SPEED = 20;
  private int selected;

  private int panelWidth, panelHeight;

  private final int TOTAL_EVENTS = 10;
  private double elapsedTime;
  private double delayTime;
  private double animationDelay = 1;

  private int eventCount;
  private int winCount;
  private int loseCount;

  /**
   * A Bandit is a "gambling" game where the user chooses one of the doors and
   * either receives a winning event or losing event.
   * @param game
   * @param controller
   */
  public Bandit(NeuroGame game, InputController controller){
    this.game = game;
    this.controller = controller;
    addKeyListener(controller);

    images = new BufferedImage[4];

    SpriteMap sprites = Library.getSprites();
    intro = sprites.get("BanditIntro");
    images[0] = sprites.get("BanditStar");
    images[1] = sprites.get("BanditEnemyStraight");
    images[2] = sprites.get("BanditEnemySinusoidal");
    images[3] = sprites.get("BanditEnemyFollow");
    selectedImage = 0;

    panelWidth = Library.getWindowPixelWidth();
    panelHeight = Library.getWindowPixelHeight();

    introXOffset = panelWidth/2 - intro.getWidth()/2;
    introYOffset = panelHeight/2 - intro.getHeight()/2;

    left_offset = 0;
    right_offset = 0;
    selected = 0;

    foreground = new BufferedImage(panelWidth, panelHeight, BufferedImage.TYPE_INT_ARGB);
    graphics = foreground.createGraphics();
    selectedStroke = new BasicStroke(5);
    font = new Font("Karmatic Arcade", Font.PLAIN, 42);

    initializedImages();

    setSize(panelWidth, panelHeight);
    setBackground(Color.BLACK);
    setLayout(null);
    requestFocus();
  }

  private void initializedImages(){
    background = new BufferedImage(panelWidth, panelHeight, BufferedImage.TYPE_INT_ARGB);
    doneScreen = new BufferedImage(panelWidth, panelHeight, BufferedImage.TYPE_INT_ARGB);
    splash = new BufferedImage(panelWidth, panelHeight, BufferedImage.TYPE_INT_ARGB);
    splashGraphics = splash.createGraphics();
    splashGraphics.setFont(font);
    splashGraphics.setBackground(CLEAR);
    splashGraphics.setColor(Color.GREEN);
    splashGraphics.clearRect(0, 0, splash.getWidth(), splash.getHeight());

    Graphics2D g;

    g = background.createGraphics();
    g.setColor(BACKGROUND_COLOR);
    g.fillRect(0, 0, background.getWidth(), background.getHeight());
    g.setColor(DOOR_BACKGROUND);

    // Draw the side dots
    int i;
    for(i = panelHeight/16; i < panelHeight - panelHeight/16; i += panelHeight/16){
      g.setColor(DOOR_BACKGROUND);
      g.fillOval(panelWidth/32, i, panelHeight/32, panelHeight/32);
      g.fillOval(panelWidth - panelWidth/32 - panelHeight/32, i, panelHeight/32, panelHeight/32);

      g.setColor(BORDER_COLOR);
      g.drawOval(panelWidth - panelWidth / 32 - panelHeight / 32, i, panelHeight / 32, panelHeight / 32);
      g.drawOval(panelWidth / 32, i, panelHeight / 32, panelHeight / 32);
    }

    // Draw the top dots
    for(int j = panelWidth/16; j < panelWidth - panelWidth/16; j += panelWidth/32){
      g.setColor(DOOR_BACKGROUND);
      g.fillOval(j, panelHeight/16, panelHeight/32, panelHeight/32);
      g.fillOval(j, i - panelHeight/16, panelHeight/32, panelHeight/32);

      g.setColor(BORDER_COLOR);
      g.drawOval(j, panelHeight / 16, panelHeight / 32, panelHeight / 32);
      g.drawOval(j, i - panelHeight / 16, panelHeight / 32, panelHeight / 32);
    }

    g.setColor(DOOR_BACKGROUND);
    g.fillRect(panelWidth/16, panelHeight/8, (int)(panelWidth * .875), (int)(panelHeight * .775));

    g.setColor(BORDER_COLOR);
    g.drawRect(panelWidth / 16, panelHeight / 8, (int) (panelWidth * .875), (int) (panelHeight * .775));

    //Clear the slot for the doors.
    g.setBackground(CLEAR);
    DOOR_WIDTH = (int)(panelWidth * .275);
    DOOR_HEIGHT = (int)(panelHeight*.65);
    TOP_LEFT_DOOR = new Point(panelWidth/10, (int)(panelHeight * .19));
    TOP_RIGHT_DOOR = new Point(TOP_LEFT_DOOR.x + DOOR_WIDTH + 32, TOP_LEFT_DOOR.y);

    g.clearRect(TOP_LEFT_DOOR.x, TOP_LEFT_DOOR.y, DOOR_WIDTH, DOOR_HEIGHT);
    g.clearRect(TOP_RIGHT_DOOR.x, TOP_RIGHT_DOOR.y, DOOR_WIDTH, DOOR_HEIGHT);

    doorTop = new BufferedImage(DOOR_WIDTH, DOOR_HEIGHT, BufferedImage.TYPE_INT_ARGB);
    doorBottom = new BufferedImage(DOOR_WIDTH, DOOR_HEIGHT, BufferedImage.TYPE_INT_ARGB);

    // lets do fancy stuff?
    Path2D top = new Path2D.Double();
    Stroke thicker = new BasicStroke(5);

    top.moveTo(0, 0);
    top.lineTo(0, DOOR_HEIGHT/2);
    top.lineTo(DOOR_WIDTH/32, DOOR_HEIGHT/2);
    top.lineTo(DOOR_WIDTH/16, DOOR_HEIGHT/3);
    top.lineTo(DOOR_WIDTH/8, DOOR_HEIGHT/3);
    top.lineTo(DOOR_WIDTH/4, DOOR_HEIGHT/2 + DOOR_HEIGHT/8);
    top.lineTo(DOOR_WIDTH/2, DOOR_HEIGHT/2 + DOOR_HEIGHT/8);
    top.lineTo(DOOR_WIDTH/2 + DOOR_WIDTH/4, DOOR_HEIGHT/2 + DOOR_HEIGHT/8);
    top.lineTo(DOOR_WIDTH/2 +
               DOOR_WIDTH/4 +
               DOOR_WIDTH/8, DOOR_HEIGHT/3);
    top.lineTo(DOOR_WIDTH/2 +
               DOOR_WIDTH/4 +
               DOOR_WIDTH/8 +
               DOOR_WIDTH/16, DOOR_HEIGHT/3);
    top.lineTo(DOOR_WIDTH/2 +
               DOOR_WIDTH/4 +
               DOOR_WIDTH/8 +
               DOOR_WIDTH/16 +
               DOOR_WIDTH/32, DOOR_HEIGHT/2);
    top.lineTo(DOOR_WIDTH, DOOR_HEIGHT/2);
    top.lineTo(DOOR_WIDTH, 0);
    top.closePath();

    Path2D bottom = new Path2D.Double();
    bottom.moveTo(0, DOOR_HEIGHT);
    bottom.lineTo(0, DOOR_HEIGHT/2);
    bottom.lineTo(DOOR_WIDTH/32, DOOR_HEIGHT/2);
    bottom.lineTo(DOOR_WIDTH/16, DOOR_HEIGHT/3);
    bottom.lineTo(DOOR_WIDTH/8, DOOR_HEIGHT/3);
    bottom.lineTo(DOOR_WIDTH/4, DOOR_HEIGHT/2 + DOOR_HEIGHT/8);
    bottom.lineTo(DOOR_WIDTH/2, DOOR_HEIGHT/2 + DOOR_HEIGHT/8);
    bottom.lineTo(DOOR_WIDTH/2 + DOOR_WIDTH/4, DOOR_HEIGHT/2 + DOOR_HEIGHT/8);
    bottom.lineTo(DOOR_WIDTH/2 +
        DOOR_WIDTH/4 +
        DOOR_WIDTH/8, DOOR_HEIGHT/3);
    bottom.lineTo(DOOR_WIDTH/2 +
        DOOR_WIDTH/4 +
        DOOR_WIDTH/8 +
        DOOR_WIDTH/16, DOOR_HEIGHT/3);
    bottom.lineTo(DOOR_WIDTH/2 +
        DOOR_WIDTH/4 +
        DOOR_WIDTH/8 +
        DOOR_WIDTH/16 +
        DOOR_WIDTH/32, DOOR_HEIGHT/2);
    bottom.lineTo(DOOR_WIDTH, DOOR_HEIGHT/2);
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
    g.setFont(GUI_util.FONT36);
    g.setColor(FONT_COLOR);
    g.clearRect(0, 0, doneScreen.getWidth(), doneScreen.getHeight());
    g.drawString("Congradulations, you got enough credits to ", panelWidth / 16, panelHeight / 8);
    g.drawString("purchase your Delton starfighter. ", panelWidth/16, panelHeight/8 + 42);

  }

  public void init(User currentUser){
    this.currentUser = currentUser;
    state = BanditState.INTRO;
    running = true;

    //just in case?
    panelWidth = Library.getWindowPixelWidth();
    panelHeight = Library.getWindowPixelHeight();

    setSize(panelWidth, panelHeight);

    elapsedTime = 0;
    selected = 0;

    animate(0);

    graphics.drawImage(intro, introXOffset, introYOffset, null);
    running = true;

    repaint();
    setVisible(true);
  }

  /**
   * Helper method for resetting things to 0
   */
  private void startBandit(){
    if(currentUser.isLogging()){
      game.log.sendByteBySocket(SocketToParallelPort.TRIGGER_BANDIT_START);
    }

    animate(0);

    running = true;
    eventCount = 0;
    winCount = 0;
    loseCount = 0;
    state = BanditState.IDLE;
    score = 0;
  }

  /**
   * Update and redraw everything
   * @param deltaSec
   * @return running
   */
  public boolean banditUpdate(double deltaSec){
    this.requestFocus();

    elapsedTime += deltaSec;

    if(state != BanditState.INTRO && state != BanditState.DONE) {
      if (state == BanditState.IDLE) {
        if(controller.isPlayerPressingESC()) {
          state = BanditState.EXIT;
          running = false;
        }
        else {
          if(controller.getPlayerInputDirectionVector().x != 0) {
            if (controller.getPlayerInputDirectionVector().x > 0) {
              selected = 1;
            } else {
              selected = 0;
            }
          }
          else if (controller.isPlayerPressingButton()) {
            nextEvent();
          }
        }
      }
    }
    else if(state == BanditState.INTRO){
      if(controller.isPlayerPressingESC()) {
        state = BanditState.EXIT;
        running = false;
      }
      else if(controller.isPlayerPressingButton()) startBandit();
    }
    else{
      if(controller.isPlayerPressingESC() || controller.isPlayerPressingButton()){
        running = false;
      }
    }

    animate(deltaSec);

    return running;
  }

  /**
   * Generate the next event along with the timing intervals.
   * The timing is not based on actual time instead it is based on the
   * a number of spins for each counter.
   */
  private void nextEvent(){
    if(eventCount >= TOTAL_EVENTS){
      state = BanditState.DONE;
      if(currentUser.isLogging()){
        game.log.sendByteBySocket(SocketToParallelPort.TRIGGER_BANDIT_DONE);
      }
    }
    else{
      eventCount++;

      if(winCount == TOTAL_EVENTS/2) {
        state = BanditState.LOSE;
        loseCount++;
      }
      else if(loseCount == TOTAL_EVENTS/2) {
        state = BanditState.WIN;
        winCount++;
      }
      else if(score < 50) {
        state = BanditState.WIN;
        winCount++;
      }
      else if(Library.RANDOM.nextDouble() > winCount/(eventCount+1.0)){
        state = BanditState.WIN;
        winCount++;
      }
      else{
        state = BanditState.LOSE;
        loseCount++;
      }

      if(state == BanditState.WIN) {
        selectedImage = 0;
        if(currentUser.isLogging()){
          game.log.sendByteBySocket(SocketToParallelPort.TRIGGER_BANDIT_WIN);
        }
      }
      else{
        selectedImage = Library.RANDOM.nextInt(images.length - 1) + 1;
        if(currentUser.isLogging()) {
          game.log.sendByteBySocket(SocketToParallelPort.TRIGGER_BANDIT_LOSE);
        }
      }
    }
  }

  /**
   * Animate the spinners and send signals to the parallel port when the spinners have stopped.
   */
  private void animate(double deltaTime){
    boolean done = false;
    elapsedTime += deltaTime;
    if(state == BanditState.WIN || state == BanditState.LOSE) done = openDoor();

    if(done){
      if(state == BanditState.WIN){
        done = drawWin(deltaTime);
      }
      else{
        done = drawLose(deltaTime);
      }
    }
    else elapsedTime += deltaTime;

    if(done && state != BanditState. INTRO){
      left_offset = right_offset = 0;

      if(currentUser.isLogging()){
        if(state == BanditState.WIN) {

          score += 55;
        }
        else if(state == BanditState.LOSE){
          game.log.sendByteBySocket(SocketToParallelPort.TRIGGER_BANDIT_LOSE);
          score -= 50;
        }
      }

      state = BanditState.IDLE;
    }
    //Attemp to clear the buffer if no signal has been sent this tick
    else{
      game.log.sendByteBySocket(SocketToParallelPort.TRIGGER_SIGNAL_GROUND);
    }

    graphics.setColor(DOOR_BACKGROUND);
    graphics.fillRect(0,0,panelWidth, panelHeight);

    if(selected == 0){
      int x = TOP_LEFT_DOOR.x + DOOR_WIDTH/2 - images[selectedImage].getWidth()/2;
      int y = TOP_LEFT_DOOR.y + DOOR_HEIGHT/2 - images[selectedImage].getHeight()/2;
      graphics.drawImage(images[selectedImage], x, y, null);
    }
    else{
      int x = TOP_RIGHT_DOOR.x + DOOR_WIDTH/2 - images[selectedImage].getWidth()/2;
      int y = TOP_RIGHT_DOOR.y + DOOR_HEIGHT/2 - images[selectedImage].getHeight()/2;
      graphics.drawImage(images[selectedImage], x, y, null);
    }


    //Left door
    graphics.drawImage(doorTop, TOP_LEFT_DOOR.x, TOP_LEFT_DOOR.y - left_offset, null);
    graphics.drawImage(doorBottom, TOP_LEFT_DOOR.x, TOP_LEFT_DOOR.y + left_offset, null);

    //Right door
    graphics.drawImage(doorTop, TOP_RIGHT_DOOR.x, TOP_RIGHT_DOOR.y - right_offset, null);
    graphics.drawImage(doorBottom, TOP_RIGHT_DOOR.x, TOP_RIGHT_DOOR.y + right_offset, null);


    graphics.drawImage(background, 0, 0, null);

    graphics.setColor(Color.CYAN);
    graphics.setStroke(selectedStroke);
    if(selected == 0){
      graphics.drawRect(TOP_LEFT_DOOR.x - 16, TOP_LEFT_DOOR.y - 16, DOOR_WIDTH + 32, DOOR_HEIGHT + 32);
    }
    else{
      graphics.drawRect(TOP_RIGHT_DOOR.x - 16, TOP_RIGHT_DOOR.y - 16, DOOR_WIDTH + 32, DOOR_HEIGHT + 32);
    }


    drawScore();
    graphics.drawImage(splash, 0, 0, null);

    if(state == BanditState.INTRO){
      graphics.drawImage(intro, introXOffset, introYOffset, null);
    }
    else if(state == BanditState.DONE){
      graphics.drawImage(doneScreen, 0, 0, null);
    }

    repaint();
  }

  /**
   * animate the doors opening
   * @return true if the doors are fully open
   */
  private boolean openDoor(){
    boolean done = true;

    if(selected == 0){
      left_offset += DOOR_SPEED;
      if(left_offset < DOOR_HEIGHT/2) done = false;
    }
    else{
      right_offset += DOOR_SPEED;
      if(right_offset < DOOR_HEIGHT/2) done = false;
    }

    return done;
  }

  /**
   * Draw whatever the current score is to the screen.
   */
  private void drawScore(){
    graphics.setColor(DOOR_COLOR);
    graphics.fillRect(panelWidth - (int) (panelWidth * .29), panelHeight / 4, panelWidth / 5, panelHeight / 10);
    graphics.setColor(FONT_COLOR);
    graphics.setFont(font);
    graphics.drawString("Score", panelWidth - (int)(panelWidth *.26), (int)(panelHeight * .23));
    graphics.drawString(Integer.toString(score), panelWidth - (int)(panelWidth *.24), (int)(panelHeight * .32));
  }

  private boolean drawWin(double deltaTime){
    boolean done = false;
    int x,y;
    delayTime += deltaTime;
    if(delayTime > animationDelay) {
      done = true;
      delayTime = 0;
      splashGraphics.clearRect(0, 0, splash.getWidth(), splash.getHeight());
    }
    else{
      if(Library.RANDOM.nextDouble() > .6){
          x = Library.RANDOM.nextInt(splash.getWidth() - 100);
          y = Library.RANDOM.nextInt(splash.getHeight() - 100);
          splashGraphics.setColor(Color.GREEN);
          splashGraphics.drawString("YOU WIN", x, y);
      }
    }
    return done;
  }

  private boolean drawLose(double deltaTime){
    boolean done = false;
    delayTime += deltaTime;
    if(delayTime > animationDelay) {
      done = true;
      delayTime = 0;
      splashGraphics.clearRect(0, 0, splash.getWidth(), splash.getHeight());
    }
    else{
      splashGraphics.setColor(Color.RED);
      splashGraphics.drawString("YOU LOSE", splash.getWidth()/2, splash.getHeight()/2);
    }

    return done;
  }

  @Override
  public void paint(Graphics g){
    g.drawImage(foreground, 0, 0, null);
  }
}
