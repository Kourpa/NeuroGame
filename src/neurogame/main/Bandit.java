package neurogame.main;

import neurogame.io.InputController;
import neurogame.io.SocketToParallelPort;
import neurogame.io.User;
import neurogame.library.Library;
import neurogame.library.SpriteMap;

import javax.swing.*;
import java.awt.*;
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
  private BufferedImage[][] spinners;
  private BufferedImage[] lastImages;
  private int[] numbSpin;
  private int[] spinCount;
  private int[] spinnerOffset;
  private int[] spinnerSpeed;
  private final int MAX_SPINS = 30;
  private final int MIN_SPINS = 10;

  // The maximum speed the spinners can spin at.
  private final int MAX_SPEED = 30;
  private final int MIN_SPEED = 10;

  private BufferedImage background;
  private BufferedImage intro;
  private int bgWidthOffset;
  private int bgHeightOffset;

  private BufferedImage foreground;
  private Graphics2D graphics;
  private Font font;

  private final Color CLEAR = new Color(0,0,0,0);
  private final Color SPINNER_BACKGROUND = new Color(1, 39, 79, 254);

  private final Color BUTTON_BACKGROUND = new Color(0, 30, 27, 254);
  private final Color FONT_COLOR = new Color(8, 188, 0, 254);
  private final Color FONT_BLINK_COLOR = new Color(0, 44, 40, 254);

  private int panelWidth, panelHeight;
  private Point[] boxPos;

  private final int TOTAL_EVENTS = 80;
  private double elapsedTime;

  private int eventCount;
  private int winCount;
  private int loseCount;


  private final String INTRO_STR = "The resistance has rigged one of the Glion gambling\n" +
    "parlors to pay out an excess of credits.  You need to collect these\n" +
    "credits so we can purchase your Delton starfighter from our black market\n" +
    "dealers";

  /**
   * Create the new OneArmedBandit minigame.
   * @param game
   * @param controller
   */
  public Bandit(NeuroGame game, InputController controller){
    this.game = game;
    this.controller = controller;
    addKeyListener(controller);

    spinners = new BufferedImage[3][3];
    lastImages = new BufferedImage[3];
    numbSpin = new int[3];
    spinCount = new int[3];
    spinnerOffset = new int[3];
    spinnerSpeed = new int[3];

    images = new BufferedImage[4];

    SpriteMap sprites = Library.getSprites();
    background = sprites.get("slotMachine");
    intro = sprites.get("BanditIntro");
    images[0] = sprites.get("BanditStar");
    images[1] = sprites.get("BanditEnemyStraight");
    images[2] = sprites.get("BanditEnemySinusoidal");
    images[3] = sprites.get("BanditEnemyFollow");

    panelWidth = Library.getWindowPixelWidth();
    panelHeight = Library.getWindowPixelHeight();

    foreground = new BufferedImage(panelWidth, panelHeight, BufferedImage.TYPE_INT_ARGB);
    graphics = foreground.createGraphics();
    font = new Font("Karmatic Arcade", Font.PLAIN, 42);

    setSize(panelWidth, panelHeight);
    setBackground(Color.BLACK);
    setLayout(null);
    requestFocus();
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

    bgWidthOffset = panelWidth/2 - background.getWidth()/2;
    bgHeightOffset = panelHeight/2 - background.getHeight()/2;

    boxPos = new Point[3];
    boxPos[0] = new Point(bgWidthOffset + 225, bgHeightOffset + 235);
    boxPos[1] = new Point(bgWidthOffset + 164 + 225, bgHeightOffset + 235);
    boxPos[2] = new Point(bgWidthOffset + 2*164 + 225, bgHeightOffset + 235);

    spinners = new BufferedImage[3][3];
    for(int i = 0; i < spinners.length; i++){
      for(int j = 0; j < spinners[i].length; j++)
      spinners[i][j] = images[(int)(Library.RANDOM.nextDouble() * images.length)];
    }

    for(int i = 0; i < spinCount.length; i++) spinCount[i] = 0;
    for(int i = 0; i < spinnerOffset.length; i++) spinnerOffset[i] = 0;
    for(int i = 0; i < numbSpin.length; i++) numbSpin[i] = 0;
    for(int i = 0; i < spinnerSpeed.length; i++) spinnerSpeed[i] = 0;

    animate(0);
    graphics.drawImage(intro, bgWidthOffset, bgHeightOffset, null);

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

    if(state != BanditState.INTRO) {
      if (state == BanditState.IDLE) {
        if (controller.isPlayerPressingButton()) {
          nextEvent();
        }
      }
    }
    else{
      if(controller.isPlayerPressingButton()) startBandit();
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
    if(eventCount > TOTAL_EVENTS){
      state = BanditState.DONE;
      running = false;
    }
    else{
      for(int i = 0; i < numbSpin.length; i++)
        numbSpin[i] = (int)(MIN_SPINS + Library.RANDOM.nextDouble() * (MAX_SPINS - MIN_SPINS));

      for(int i = 0; i < spinnerSpeed.length; i++)
        spinnerSpeed[i] = (int)(MIN_SPEED + Library.RANDOM.nextDouble() * (MAX_SPEED - MIN_SPEED));

      spinCount[0] = 0;
      spinCount[1] = 0;
      spinCount[2] = 0;

      //think of a way to make the probability work with just the wins and the loses...
      //40 wins and 40 loses
      //if there are more wins than loses there should be a better chance to lose.

      if(winCount == TOTAL_EVENTS/2) state = BanditState.LOSE;
      else if(loseCount == TOTAL_EVENTS/2) state = BanditState.WIN;
      else if(score < 50) state = BanditState.WIN;
      else if(Library.RANDOM.nextDouble() > winCount/(eventCount+1.0)){
        state = BanditState.WIN;
        winCount++;
        eventCount++;
      }
      else{
        state = BanditState.LOSE;
        loseCount++;
        eventCount++;
      }

      chooseLastImage();
    }
  }

  /**
   * Animate the spinners and send signals to the parallel port when the spinners have stopped.
   */
  private void animate(double deltaTime){
    boolean done = true;

    graphics.setColor(Color.BLACK);
    graphics.fillRect(0, 0, panelWidth, panelHeight);

    graphics.setColor(SPINNER_BACKGROUND);
    graphics.fillRect(boxPos[0].x - 25, boxPos[0].y - 100, 500, 300);
    for(int i = 0; i < spinnerOffset.length; i++){
      if(spinCount[i] != numbSpin[i]) {
        done = false;
        spinnerOffset[i] += spinnerSpeed[i];
      }
    }

    //done will only be true of the spinners have stoped moving
    if(done && state != BanditState. INTRO){

      if(currentUser.isLogging()){
        if(state == BanditState.WIN) {
          game.log.sendByteBySocket(SocketToParallelPort.TRIGGER_BANDIT_WIN);
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

    //Draw the spinners at their current state
    for(int i = 0; i < spinners.length; i++) {
      for (int j = -1; j < spinners[i].length - 2; j++) {
        graphics.drawImage(spinners[i][j + 1], boxPos[i].x,
          boxPos[i].y + BUFFER_SIZE * j + spinnerOffset[i], null);
      }
    }

    //if the spinner has gone over the buffer size generate the next image for
    //that spinner, reset the offset, and move the images down appropriately.
    for(int i = 0; i < spinners.length; i++) {
      for (int j = -1; j < spinners[i].length - 1; j++) {

        if(BUFFER_SIZE - spinnerOffset[i] < 0){

          BufferedImage nextImage;

          if(spinCount[i] + 2 == numbSpin[i]){
            if(state == BanditState.WIN)
              nextImage = images[0];
            else
              nextImage = lastImages[i];
          }
          else{
            nextImage = images[Library.RANDOM.nextInt(images.length - 1)];
          }

          for(int z = 0; z < spinners.length - 1; z++){
            spinners[i][z + 1] = spinners[i][z];
          }
          spinners[i][0] = nextImage;

          spinnerOffset[i] -= BUFFER_SIZE;
          spinCount[i]++;

        }
      }
    }

    //Draw it all the the screen
    drawButtons(deltaTime);
    graphics.drawImage(background, bgWidthOffset, bgHeightOffset, null);

    if(state == BanditState.INTRO){
      graphics.drawImage(intro, bgWidthOffset, bgHeightOffset, null);
    }
    repaint();
  }

  /**
   * Helper method for drawing/animating buttons.
   */
  private void drawButtons(double deltaTime){
    graphics.setColor(BUTTON_BACKGROUND);
    graphics.fillRect(425, 525, 525, 100);

    if(state == BanditState.WIN || state == BanditState.LOSE){
      graphics.setColor(FONT_BLINK_COLOR);
    }
    else if(state == BanditState.IDLE || state == BanditState.INTRO){
      if((int)elapsedTime % 2 == 0){
        graphics.setColor(FONT_BLINK_COLOR);
      }
      else{
        graphics.setColor(FONT_COLOR);
      }
    }
    graphics.setFont(font);
    graphics.drawString("Ready", 442, 585);
    graphics.drawString(Integer.toString(score), 722, 585);
  }

  /**
   * Helper method for choosing what the final image will be in a sequence to insure
   * a win or a loss.
   */
  private void chooseLastImage(){
    boolean loss = false;
    for(int i = 0; i < lastImages.length; i++){
      lastImages[i] = images[Library.RANDOM.nextInt(images.length - 1)];
      if(lastImages[i] != images[0]) loss = true;
    }

    if(!loss){
      lastImages[Library.RANDOM.nextInt(lastImages.length - 1)] =
        images[1 + Library.RANDOM.nextInt(images.length - 2)];
    }
  }

  @Override
  public void paint(Graphics g){
    g.drawImage(foreground, 0, 0, null);
  }
}
