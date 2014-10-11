package neurogame.main;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import neurogame.io.SocketToParallelPort;
import neurogame.io.User;
import neurogame.library.Library;
import neurogame.library.SpriteMap;

/**
 * @author kourpa
 */
@SuppressWarnings("serial")
public class Oddball extends JPanel implements MouseListener, KeyListener
{

  private enum OddballMode
  {
    SETUP, INTRO, START, PLAY_STANDARD, PLAY_ODDBALL, PLAY_WAIT, DONE, EXIT
  };

  private OddballMode mode = OddballMode.SETUP;

  private static final double EVENT_SEC = 0.5;

  private static final double WAIT_MIN_SEC = 1.0;
  private static final double WAIT_MAX_SEC = 2.0;

  private static final int TOTAL_EVENTS = 200;

  private NeuroGame game;

  private JLabel msg1, msg3, label_enemyTxt, label_friendTxt;
  private JLabel label_enemyImg, label_friendImg, label_waitImg;
  private JTextArea msg2;

  private BufferedImage enemyImage, friendlyImage, waitImage;
  private int panelWidth, panelHeight;
  private int iconSize;

  private User currentUser;

  private int standardCount, oddballCount, totalEventCount, lastOddBallIdx;
  private double timeTotalElapsed;
  private double timeCurrentSymbolEnd;
  
  

  private static final String INTRO_STR = "In this exercise, we’ll determine if your neuro-perceptual processing "
      + "is adequate for piloting a starfighter\n\n"
      + "We’ll show you images of our Delton-class starfighter interspersed with "
      + "images of Glion battlecruisers.\n\n"
      + "You must keep a mental count of the number of Glion battlecrusiers you see, "
      + "and report this count when this 6 minute exercise is over.";

  public Oddball(NeuroGame game)
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

    msg2 = new JTextArea(INTRO_STR);

    msg2.setBackground(Color.BLACK);
    msg2.setEditable(false);
    msg2.setLineWrap(true);
    msg2.setWrapStyleWord(true);
    msg2.setFont(GUI_util.FONT_ARIAL20);
    this.add(msg2);

    msg3 = GUI_util.makeLabelArial30("Let us know when you are ready to begin training.", this);

    msg3.setForeground(GUI_util.COLOR_SELECTED);

    label_friendImg = new JLabel(new ImageIcon(friendlyImage));

    label_enemyImg = new JLabel(new ImageIcon(enemyImage));

    label_waitImg = new JLabel(new ImageIcon(waitImage));

    
    label_friendTxt = GUI_util.makeLabel20("Our Delton-class starfighter", this);
    label_enemyTxt = GUI_util.makeLabelArial20("Enemy Glion battlecrusiers", this);


    this.add(label_enemyTxt);
    this.add(label_enemyImg);
    this.add(label_waitImg);
    this.add(label_friendTxt);
    this.add(label_friendImg);

    this.addMouseListener(this);
    this.addKeyListener(this);
  }

  public void init(User currentUser)
  {
    mode = OddballMode.INTRO;

    this.currentUser = currentUser;

    panelWidth = Library.getWindowPixelWidth();
    panelHeight = Library.getWindowPixelHeight();

    this.setSize(panelWidth, panelHeight);

    FontMetrics fm30 = this.getFontMetrics(GUI_util.FONT_ARIAL30);
    FontMetrics fm20 = this.getFontMetrics(GUI_util.FONT_ARIAL20);

    int fontW20 = fm20.stringWidth("X");
    int fontW30 = fm30.stringWidth("X");

    int fontH30 = fm30.getLeading() + fm30.getMaxAscent() + fm30.getMaxDescent();
    int boxH30 = fontH30 + 8;
    int rowH30 = boxH30 + 3;

    int fontH20 = fm20.getLeading() + fm20.getMaxAscent() + fm20.getMaxDescent();
    int boxH20 = fontH20 + 8;
    int rowH20 = boxH20 + 3;

    int titleLeft = fontW30 * 4;
    int introLeft = titleLeft + fontW30 * 3;

    int titleWidth = panelWidth - titleLeft * 2;
    int introWidth = panelWidth - introLeft * 2;
    int introHeight = fontH20 * 10;

    int titleTop = fontH30;
    int introTop = titleTop + fontH30 * 2;

    msg1.setBounds(titleLeft, titleTop, titleWidth, boxH30);
    msg2.setBounds(introLeft, introTop, introWidth, introHeight);

    iconSize = 256;

    label_enemyImg.setBounds((int) (panelWidth * 0.25) - 128, (int) (panelHeight * 0.5), iconSize, iconSize);
    label_friendImg.setBounds((int) (panelWidth * 0.75) - 128, (int) (panelHeight * 0.5), iconSize, iconSize);
    label_friendTxt.setBounds((int) (panelWidth * 0.25) - 128, (int) (panelHeight * 0.3), iconSize, 256);
    label_enemyTxt.setBounds((int) (panelWidth * 0.75) - 128, (int) (panelHeight * 0.3), iconSize, 256);

    msg3.setBounds((int) (panelWidth * 0.5) - 400, (int) (panelHeight * 0.75), 800, 150);

    this.repaint();

    setIntroGUI(true);
    this.setVisible(true);
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
    timeCurrentSymbolEnd = 0.25;

    int imageLeft = (panelWidth - iconSize) / 2;
    int imageTop = (panelHeight - iconSize) / 2;

    label_friendImg.setBounds(imageLeft, imageTop, iconSize, iconSize);
    label_enemyImg.setBounds(imageLeft, imageTop, iconSize, iconSize);
    label_waitImg.setBounds(imageLeft, imageTop, iconSize, iconSize);

    mode = OddballMode.START;
    showWait();
    mode = OddballMode.PLAY_WAIT;

  }

  private void setIntroGUI(boolean state)
  {
    msg1.setVisible(state);
    msg2.setVisible(state);
    msg3.setVisible(state);
    label_enemyTxt.setVisible(state);
    label_friendTxt.setVisible(state);
    label_enemyImg.setVisible(state);
    label_friendImg.setVisible(state);

    // areaScrollPane.setVisible(state);
  }

  public boolean oddballUpdate(double deltaSec)
  {
    if (mode == OddballMode.EXIT) return false;

    if (mode == OddballMode.INTRO) return true;
    if (mode == OddballMode.START) return true;
    if (mode == OddballMode.DONE) return true;
    if (mode == OddballMode.SETUP) return true;

    timeTotalElapsed += deltaSec;
    if (timeTotalElapsed < timeCurrentSymbolEnd) return true;

    totalEventCount++;
    if (totalEventCount > TOTAL_EVENTS)
    {
      oddballDone();
      return true;
    }

    System.out.println("Oddball: event #=" + totalEventCount + ", timeTotalElapsed=" + timeTotalElapsed);

    if (mode != OddballMode.PLAY_WAIT)
    {
      showWait();
      double waitTime = WAIT_MIN_SEC + Library.RANDOM.nextDouble() * (WAIT_MAX_SEC - WAIT_MIN_SEC);
      timeCurrentSymbolEnd += waitTime;
      return true;
    }

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

  // /* Menu Button Methods */
  // /**
  // * Called from the HighscoreUpdate() in gamecontroller To select the buttons
  // * with the joystick
  // */
  // public void updateJoystick(Controller joystick, int JOYSTICK_X, int
  // JOYSTICK_Y)
  // {
  // joystick.poll();
  //
  // for (int i = 0; i < 5; i++)
  // {
  // if (joystick.isButtonPressed(i))
  // {
  // instructions = false;
  // }
  // }
  // }

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

  @Override
  public void keyTyped(KeyEvent event)
  {
    int code = event.getKeyCode();
    if (mode == OddballMode.INTRO)
    {
      if ((code == KeyEvent.VK_ENTER) || (code == KeyEvent.VK_SPACE)) startOddball();
    }

    if (code == KeyEvent.VK_ESCAPE) mode = OddballMode.EXIT;

  }

  @Override
  public void keyPressed(KeyEvent e)
  {}

  @Override
  public void keyReleased(KeyEvent event)
  {

  }

  @Override
  public void mouseClicked(MouseEvent e)
  {
    if (mode == OddballMode.INTRO) startOddball();
  }

  @Override
  public void mousePressed(MouseEvent e)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void mouseReleased(MouseEvent e)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void mouseEntered(MouseEvent e)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void mouseExited(MouseEvent e)
  {
    // TODO Auto-generated method stub

  }

}
