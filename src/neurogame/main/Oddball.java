package neurogame.main;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import neurogame.io.InputController;
import neurogame.io.SocketToParallelPort;
import neurogame.io.User;
import neurogame.library.Library;
import neurogame.library.SpriteMap;

/**
 * @author kourpa
 */
@SuppressWarnings("serial")
public class Oddball extends JPanel
{

  private enum OddballMode
  {
    SETUP, INTRO, START, PLAY_STANDARD, PLAY_ODDBALL, PLAY_WAIT, DONE, EXIT
  };

  public static final int ICON_SIZE = 204;
  private OddballMode mode = OddballMode.SETUP;

  private static final double EVENT_SEC = 0.5;

  private static final double WAIT_MIN_SEC = 1.0;
  private static final double WAIT_MAX_SEC = 2.0;

  private static final int TOTAL_EVENTS = 200;

  private NeuroGame game;

  private JLabel msg1, msg3;
  private JLabel label_enemyImg, label_friendImg, label_waitImg;
  private JTextArea msg2;
  private JLabel label_friendTxt1, label_enemyTxt1, label_friendTxt2, label_enemyTxt2;

  private BufferedImage enemyImage, friendlyImage, waitImage;
  private int panelWidth, panelHeight;

  private User currentUser;

  private int standardCount, oddballCount, totalEventCount, lastOddBallIdx;
  private double timeTotalElapsed;
  private double timeCurrentSymbolEnd;

  private static final String INTRO_STR = "In this exercise, we’ll determine if your neuro-perceptual processing "
      + "is adequate for piloting a starfighter\n\n"
      + "We’ll show you images of our Delton-class starfighter interspersed with "
      + "images of Glion battlecruisers.\n\n"
      + "You must keep a mental count of the number of Glion battlecrusiers you see, "
      + "and report this count when this 7 minute exercise is over.";

  public Oddball(NeuroGame game, InputController gameController)
  {
    this.game = game;

    panelWidth = Library.getWindowPixelWidth();
    panelHeight = Library.getWindowPixelHeight();

    this.setSize(panelWidth, panelHeight);
    this.setLayout(null);
    this.setBackground(Color.BLACK);

    SpriteMap sprites = Library.getSprites();

    enemyImage = sprites.get("OddballEnemyShip");
    friendlyImage = sprites.get("OddballPlayerShip");
    waitImage = sprites.get("WaitOddball");

    msg1 = GUI_util.makeLabelArial30("Welcome to cadet training!", this);
    this.add(msg1);

    msg2 = GUI_util.makeTextAreaArial20(INTRO_STR, this);

    msg3 = GUI_util.makeLabelArial20("[Press game controller button, or spacebar begin]", this);
    msg3.setForeground(Library.HIGHLIGHT_TEXT_COLOR);
    msg3.setHorizontalAlignment(SwingConstants.CENTER);

    label_friendImg = new JLabel(new ImageIcon(friendlyImage));

    label_enemyImg = new JLabel(new ImageIcon(enemyImage));

    label_waitImg = new JLabel(new ImageIcon(waitImage));

    label_friendTxt1 = GUI_util.makeLabelArial20("Our", this);
    label_friendTxt2 = GUI_util.makeLabelArial20("Delton-class Starfighter", this);
    label_enemyTxt1 = GUI_util.makeLabelArial20("Enemy", this);
    label_enemyTxt2 = GUI_util.makeLabelArial20("Glion Battlecrusiers", this);

    label_friendTxt1.setHorizontalAlignment(SwingConstants.CENTER);
    label_friendTxt2.setHorizontalAlignment(SwingConstants.CENTER);
    label_enemyTxt1.setHorizontalAlignment(SwingConstants.CENTER);
    label_enemyTxt2.setHorizontalAlignment(SwingConstants.CENTER);

    this.add(label_enemyImg);
    this.add(label_waitImg);
    this.add(label_friendImg);
    
    this.addKeyListener(gameController);
  }

  public void init(User currentUser)
  {
    mode = OddballMode.INTRO;

    this.currentUser = currentUser;

    panelWidth = Library.getWindowPixelWidth();
    panelHeight = Library.getWindowPixelHeight();

    this.setSize(panelWidth, panelHeight);

    FontMetrics fm30 = this.getFontMetrics(Library.FONT_ARIAL30);
    FontMetrics fm20 = this.getFontMetrics(Library.FONT_ARIAL20);

    int fontW20 = fm20.stringWidth("X");
    int fontW30 = fm30.stringWidth("X");

    int fontH30 = fm30.getLeading() + fm30.getMaxAscent() + fm30.getMaxDescent();
    int boxH30 = fontH30 + 8;
    int rowH30 = boxH30 + 3;

    int fontH20 = fm20.getLeading() + fm20.getMaxAscent() + fm20.getMaxDescent();
    int boxH20 = fontH20 + 8;
    int rowH20 = boxH20 + 3;

    int titleLeft = fontW30 * 2;
    int introLeft = titleLeft + fontW30 * 3;

    int titleWidth = panelWidth - titleLeft * 2;
    int introWidth = panelWidth - introLeft * 2;
    int introHeight = fontH20 * 10;

    int titleTop = fontH20;
    int introTop = titleTop + fontH30 + rowH20;

    msg1.setBounds(titleLeft, titleTop, titleWidth, boxH30);
    msg2.setBounds(introLeft, introTop, introWidth, introHeight);

    int iconTop = introTop + introHeight + 5;
    int iconLeft = introLeft + fontW20 * 5;
    int col2 = panelWidth - 2 * ICON_SIZE - iconLeft;
    label_friendImg.setBounds(iconLeft, iconTop, ICON_SIZE, ICON_SIZE);
    label_enemyImg.setBounds(col2, iconTop, ICON_SIZE, ICON_SIZE);

    int imageLabelTop = iconTop + ICON_SIZE + 5;
    int imageLabelWidth = ICON_SIZE * 2;
    int text1L = (iconLeft + ICON_SIZE / 2) - imageLabelWidth / 2;
    int text2L = (col2 + ICON_SIZE / 2) - imageLabelWidth / 2;
    label_friendTxt1.setBounds(text1L, imageLabelTop, imageLabelWidth, fontH20);
    label_friendTxt2.setBounds(text1L, imageLabelTop + fontH20, imageLabelWidth, boxH20);
    label_enemyTxt1.setBounds(text2L, imageLabelTop, imageLabelWidth, fontH20);
    label_enemyTxt2.setBounds(text2L, imageLabelTop + fontH20, imageLabelWidth, boxH20);

    int startMsgTop = (panelHeight - rowH30) - rowH20;
    msg3.setBounds(0, startMsgTop, panelWidth, boxH20);

    this.repaint();

    setIntroGUI(true);
    this.setVisible(true);
    this.requestFocus();
  }

  private void startOddball()
  {
    if (mode != OddballMode.INTRO) return;

    if (currentUser.isLogging())
    {
      game.log.sendByteBySocket(SocketToParallelPort.TRIGGER_ODDBALL_START);
    }
    setIntroGUI(false);
    totalEventCount = 0;
    standardCount = 0;
    oddballCount = 0;
    lastOddBallIdx = 0;
    timeTotalElapsed = 0;
    timeCurrentSymbolEnd = WAIT_MIN_SEC;

    int imageLeft = (panelWidth - ICON_SIZE) / 2;
    int imageTop = (panelHeight - ICON_SIZE) / 2;

    label_friendImg.setBounds(imageLeft, imageTop, ICON_SIZE, ICON_SIZE);
    label_enemyImg.setBounds(imageLeft, imageTop, ICON_SIZE, ICON_SIZE);
    label_waitImg.setBounds(imageLeft, imageTop, ICON_SIZE, ICON_SIZE);

    mode = OddballMode.START;
    showWait();
    mode = OddballMode.PLAY_WAIT;

  }

  private void setIntroGUI(boolean state)
  {
    msg1.setVisible(state);
    msg2.setVisible(state);
    msg3.setVisible(state);
    label_enemyTxt1.setVisible(state);
    label_enemyTxt2.setVisible(state);
    label_friendTxt1.setVisible(state);
    label_friendTxt2.setVisible(state);
    label_enemyImg.setVisible(state);
    label_friendImg.setVisible(state);
    label_waitImg.setVisible(false);
  }

  public boolean oddballUpdate(double deltaSec, int keycode)
  {

    //System.out.println("Oddball.oddballUpdate(): keycode="+keycode);
    
    
    if (mode == OddballMode.INTRO)
    {
      if ((keycode == KeyEvent.VK_ENTER) || (keycode == KeyEvent.VK_SPACE)) startOddball();
    }

    if (keycode == KeyEvent.VK_ESCAPE) mode = OddballMode.EXIT;

    if (mode == OddballMode.EXIT) return false;

    if (mode == OddballMode.INTRO) return true;
    if (mode == OddballMode.START) return true;
    if (mode == OddballMode.DONE) return false;
    if (mode == OddballMode.SETUP) return true;

    timeTotalElapsed += deltaSec;
    if (timeTotalElapsed < timeCurrentSymbolEnd) return true;

    if (totalEventCount >= TOTAL_EVENTS)
    {
      oddballDone();
      return true;
    }

    if (mode != OddballMode.PLAY_WAIT)
    {
      showWait();
      double waitTime = WAIT_MIN_SEC + Library.RANDOM.nextDouble() * (WAIT_MAX_SEC - WAIT_MIN_SEC);
      timeCurrentSymbolEnd += waitTime;
      return true;
    }

    totalEventCount++;
    System.out.println("Oddball: event #=" + totalEventCount + ", timeTotalElapsed=" + timeTotalElapsed);

    if (totalEventCount - lastOddBallIdx < 3) showNormal();
    else if (totalEventCount - lastOddBallIdx >= 6) showOddball();
    else
    {
      if (Library.RANDOM.nextDouble() < 0.189) showOddball();
      else showNormal();
    }

    timeCurrentSymbolEnd += EVENT_SEC;
    return true;
  }

  private void oddballDone()
  {
    mode = OddballMode.DONE;
    System.out.println(standardCount + ":" + oddballCount + " = " + ((double) standardCount / (double) oddballCount));

    if (currentUser.isLogging())
    {
      game.log.sendByteBySocket(SocketToParallelPort.TRIGGER_ODDBALL_DONE);
    }
  }

  private void showNormal()
  {
    standardCount++;
    if (currentUser.isLogging())
    {

      System.out.println("Oddball: Socket Send TRIGGER=" + SocketToParallelPort.TRIGGER_ODDBALL_STANDARD_EVENT);
      game.log.sendByteBySocket(SocketToParallelPort.TRIGGER_ODDBALL_STANDARD_EVENT);
    }

    label_friendImg.setVisible(true);
    label_enemyImg.setVisible(false);
    label_waitImg.setVisible(false);
    mode = OddballMode.PLAY_STANDARD;
  }

  private void showOddball()
  {
    oddballCount++;
    lastOddBallIdx = totalEventCount;
    if (currentUser.isLogging())
    {

      System.out.println("Oddball: Socket Send TRIGGER=" + SocketToParallelPort.TRIGGER_ODDBALL_RARE_EVENT);
      game.log.sendByteBySocket(SocketToParallelPort.TRIGGER_ODDBALL_RARE_EVENT);
    }
    label_enemyImg.setVisible(true);
    label_friendImg.setVisible(false);
    label_waitImg.setVisible(false);
    mode = OddballMode.PLAY_ODDBALL;
  }

  private void showWait()
  {
    /*
     * The port wasn't listening when we sent the same data over and over again
     * sending a 0 byte resets it.
     */
    if (currentUser.isLogging())
    {
      if (mode != OddballMode.START)
      {
        System.out.println("Oddball: Socket Send TRIGGER=" + SocketToParallelPort.TRIGGER_SIGNAL_GROUND);
        game.log.sendByteBySocket(SocketToParallelPort.TRIGGER_SIGNAL_GROUND);
      }
    }
    label_waitImg.setVisible(true);
    label_friendImg.setVisible(false);
    label_enemyImg.setVisible(false);
    mode = OddballMode.PLAY_WAIT;
  }
}
