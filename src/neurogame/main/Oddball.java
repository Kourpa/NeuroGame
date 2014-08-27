package neurogame.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import neurogame.io.SocketToParallelPort;
import neurogame.library.Library;
import neurogame.library.SpriteMap;

import org.lwjgl.input.Controller;

/**
 * @author kourpa
 */
public class Oddball // implements KeyListener
{

  //private SocketToParallelPort socket;
//  private static final String HOST = "127.0.0.1";
//  private static final int PORT = 55555;

  private static boolean SEND_TRIGGERS_VIA_SOCKET = false;

  private static final int EVENT_MIN_MSEC = 800;
  private static final int EVENT_MAX_MSEC = 1200;

  private static final int WAIT_MIN_MSEC = 1600;
  private static final int WAIT_MAX_MSEC = 2000;

  private static final int TOTAL_EVENTS = 250;

  private NeuroFrame frame;
  private NeuroGame game;
  private JPanel pane;
  private boolean testFinished;
  private boolean showCount;
  private Screen currentScreen;
  private BufferedImage badImage, goodImage, waitImage, instructionImage, finishedImage;

  private boolean instructions;

  private JLabel background;

  public Oddball(final NeuroFrame frame, NeuroGame game)
  {
    this.frame = frame;
    this.game = game;

//    if (game.getLoggingMode())
//    {
//      SEND_TRIGGERS_VIA_SOCKET = true;
//    }
//
//    if (SEND_TRIGGERS_VIA_SOCKET)
//    {
//      socket = new SocketToParallelPort(HOST, PORT);
//    }
    int width = frame.getWidth();
    int height = frame.getHeight();

    // Images
    SpriteMap sprites = Library.getSprites();

    this.badImage = sprites.get("FalseOddball");
    this.goodImage = sprites.get("TargetOddball");
    this.instructionImage = sprites.get("InstructionOddball");
    this.finishedImage = sprites.get("CountOddball");
    this.waitImage = sprites.get("WaitOddball");


    // Frame
    frame.getContentPane().removeAll();
    frame.getContentPane().setLayout(new BorderLayout());

    KeyAdapter Keys = new KeyAdapter()
    {
      public void keyReleased(KeyEvent e)
      {
        instructions = false;

        if (showCount)
        {
          background.setVisible(false);
          frame.getContentPane().removeAll();
          frame.getContentPane().setLayout(null);
          testFinished = true;
        }
      }
    };
    frame.addKeyListener(Keys);
    frame.requestFocus();

    // Instruction screen
    String msgTitle = "'Oddball' Test Instructions";
    String msgInstructions = "Keep a mental count of the number of times the enemy ship is displayed.  This is a test to see how wrapping text looks like in the textarea.  Does the text area scroll the text when it gets too long?";
    String msgFriendly = "Friendly Ship";
    String msgEnemy = "Enemy Ship";
    String msgInput = "[ Press the space bar or a controller button ]";

    // Fonts
    Font FONT_SMALL = new Font("Karmatic Arcade", Font.PLAIN, 23);
    Font FONT_LARGE = new Font("Karmatic Arcade", Font.PLAIN, 50);

    // Draw the image here
    JLabel msg1 = new JLabel(msgTitle);
    msg1.setForeground(Color.WHITE);
    msg1.setFont(FONT_LARGE);
    msg1.setBounds((int) (width * 0.5) - 500, (int) (height * 0.02), 1000, 150);

    // Instructions with a scroll pane
    JTextArea msg2 = new JTextArea(msgInstructions);
    msg2.setForeground(Color.white);
    msg2.setBackground(Color.BLACK);
    msg2.setEditable(false);
    msg2.setLineWrap(true);
    msg2.setWrapStyleWord(true);
    msg2.setFont(FONT_SMALL);

    JScrollPane areaScrollPane = new JScrollPane(msg2);
    areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    areaScrollPane.setBorder(null);
    areaScrollPane.setBounds((int) (width * 0.5) - 450, (int) (height * 0.2), 1000, 150);
    areaScrollPane.setOpaque(false);
    areaScrollPane.setBackground(new Color(0, 0, 0, 0));
    areaScrollPane.setForeground(Color.BLACK);

    // Message explaining the controlls
    JLabel msg3 = new JLabel(msgInput);
    msg3.setForeground(Color.BLUE);
    msg3.setFont(FONT_SMALL);
    msg3.setBounds((int) (width * 0.5) - 400, (int) (height * 0.75), 800, 150);

    // The enemy and friendly ship
    JLabel enemyImage = new JLabel(new ImageIcon(goodImage));
    enemyImage.setBounds((int) (width * 0.25) - 128, (int) (height * 0.5), 256, 256);

    JLabel friendlyImage = new JLabel(new ImageIcon(badImage));
    friendlyImage.setBounds((int) (width * 0.75) - 128, (int) (height * 0.5), 256, 256);

    JLabel enemyText = new JLabel(msgEnemy);
    enemyText.setBounds((int) (width * 0.25) - 128, (int) (height * 0.3), 256, 256);
    enemyText.setFont(FONT_SMALL);

    JLabel friendlyText = new JLabel(msgFriendly);
    friendlyText.setFont(FONT_SMALL);
    friendlyText.setBounds((int) (width * 0.75) - 128, (int) (height * 0.3), 256, 256);

    // background for displaying the images
    background = new JLabel();
    background.setBackground(Color.BLACK);
    background.setBounds(width / 2 - 128, 0, width, height);

    // Make a panel to center the text
    pane = new JPanel();
    pane.setLayout(null);
    pane.add(msg1);
    pane.add(areaScrollPane);
    pane.add(enemyText);
    pane.add(enemyImage);
    pane.add(friendlyText);
    pane.add(friendlyImage);
    pane.add(msg3);
    pane.add(new JLabel(msgInstructions));
    pane.setBackground(Color.BLACK);

    frame.getContentPane().add(background);
    frame.getContentPane().add(pane);
    frame.getContentPane().setBackground(Color.BLACK);
    frame.setVisible(true);
    frame.repaint();



    instructions = true;
    showCount = false;
    testFinished = false;

    // Start the test
    mainOddBallLoop();
  }

  public void mainOddBallLoop()
  {

    int standardCount = 0;
    int oddballCount = 0;
    int lastOddBallIdx = 0;

   

    if (game.getLoggingMode())
    {
      game.log.sendByteBySocket(SocketToParallelPort.TRIGGER_ODDBALL_START);
    }
    
    while (instructions)
    { //render();
      try { Thread.sleep(250); } catch (InterruptedException e) { }
    }
    pane.setVisible(false);
    frame.getContentPane().remove(pane);

    // boolean[] event=new boolean[500000];
    for (int i = 0; i < TOTAL_EVENTS; i++)
    {
      if (i - lastOddBallIdx < 3)
      {
        standardCount++;
        currentScreen = Screen.NORMAL;
      }
      else if (i - lastOddBallIdx >= 6)
      {
        oddballCount++;
        currentScreen = Screen.ODDBALL;
        lastOddBallIdx = i;
      }
      else
      {
        if (Library.RANDOM.nextDouble() < 0.189)
        {
          oddballCount++;
          currentScreen = Screen.ODDBALL;
          lastOddBallIdx = i;
        }
        else
        {
          standardCount++;
          currentScreen = Screen.NORMAL;
        }
      }

      render();

      int eventTime = EVENT_MIN_MSEC + Library.RANDOM.nextInt(EVENT_MAX_MSEC - EVENT_MIN_MSEC);
      int waitTime = WAIT_MIN_MSEC + Library.RANDOM.nextInt(WAIT_MAX_MSEC - WAIT_MIN_MSEC);

      try { Thread.sleep(eventTime); } catch (InterruptedException e) { }

      currentScreen = Screen.WAIT;
      render();
      try { Thread.sleep(waitTime); } catch (InterruptedException e) { }
    }

    System.out.println(standardCount + ":" + oddballCount + " = " + ((double) standardCount / (double) oddballCount));

    currentScreen = Screen.FINISHED;
    if (game.getLoggingMode())
    {
      game.log.sendByteBySocket(SocketToParallelPort.TRIGGER_ODDBALL_DONE);
    }
    render();

  }

  /* Menu Button Methods */
  /**
   * Called from the HighscoreUpdate() in gamecontroller To select the buttons
   * with the joystick
   */
  public void updateJoystick(Controller joystick, int JOYSTICK_X, int JOYSTICK_Y)
  {
    joystick.poll();

    for (int i = 0; i < 5; i++)
    {
      if (joystick.isButtonPressed(i))
      {
        instructions = false;
      }
    }
  }

  public boolean isFinished()
  {
    return testFinished;
  }

  public void forceClose()
  {
    background.setVisible(false);
    frame.getContentPane().removeAll();
    frame.getContentPane().setLayout(null);
    testFinished = true;
    game.showTitle();
  }

  public void render()
  {
    if (currentScreen == null){ return; }

    switch (currentScreen)
    {
    case NORMAL:
      if (game.getLoggingMode())
      {

        System.out
            .println("Oddball: Socket Send TRIGGER_NORMAL=" + SocketToParallelPort.TRIGGER_ODDBALL_STANDARD_EVENT);
        game.log.sendByteBySocket(SocketToParallelPort.TRIGGER_ODDBALL_STANDARD_EVENT);
      }
      background.setIcon(new ImageIcon(goodImage));
      break;

    case ODDBALL:
      if (game.getLoggingMode())
      {

        System.out.println("Oddball: Socket Send TRIGGER_NORMAL=" + SocketToParallelPort.TRIGGER_ODDBALL_RARE_EVENT);
        game.log.sendByteBySocket(SocketToParallelPort.TRIGGER_ODDBALL_RARE_EVENT);
      }
      background.setIcon(new ImageIcon(badImage));
      break;

    case WAIT:
      background.setIcon(new ImageIcon(waitImage));
      break;

    case INSTRUCTIONS:
      // background.setIcon(new ImageIcon(instructionImage));
      break;

    case FINISHED:
      background.setIcon(new ImageIcon(finishedImage));
      break;
    }
  }

  private enum Screen
  {
    INSTRUCTIONS, NORMAL, ODDBALL, WAIT, FINISHED;
  }

}
