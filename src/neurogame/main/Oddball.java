package neurogame.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;

import org.lwjgl.input.Controller;

import neurogame.library.Library;
import neurogame.library.SpriteMap;


/**
 * @author kourpa
 */
public class Oddball // implements KeyListener
{
  private static final byte TRIGGER_START    = 0x15;
  private static final byte TRIGGER_WAIT     = 0x2;
  private static final byte TRIGGER_NORMAL   = 0x4;
  private static final byte TRIGGER_ODDBALL  = 0x8;
  private static final byte TRIGGER_DONE     = 0x1;
  
  private CommPort parallelPort;
  private static final boolean SEND_TRIGGERS = true;
  
  
  private long time;
  private final int numberOfGoodScreens = 50;
  private final int numberOfBadScreens = 200;
  private int currentNumber;
  private boolean testFinished;
  private boolean showCount;
  private final ArrayList<Screen> options;
  private Screen currentScreen;
  private BufferedImage badImage, goodImage, waitImage, welcomeImage, instructionImage, finishedImage;

  private long startTime, screenTime, waitTime, InstructionTime;
  private boolean wait, instructions;
  private int currentImageNum = 0;
  
  private int oddballCount = 0;

  private JLabel background;

  public Oddball(final NeuroFrame frame)
  {
    
    if (SEND_TRIGGERS) {
    	System.out.println("Opening Parallel Port");
    	parallelPort = new CommPort();
    }

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

    // Draw the image here
    background = new JLabel(new ImageIcon(badImage));
    background.setBackground(Color.BLACK);
    background.setBounds(0, 0, width, height);
    background.setOpaque(true);

    frame.getContentPane().add(background);

    
    wait = instructions = false;
    startTime = System.currentTimeMillis();
    screenTime = 800 + Library.RANDOM.nextInt(400);
    waitTime = 1600 + Library.RANDOM.nextInt(400);
    InstructionTime = 2000;
    time = startTime;
    options = new ArrayList<>();

    for (int i = 0; i < numberOfGoodScreens; i++)
    {
      options.add(Screen.GOOD);
    }
    for (int i = 0; i < numberOfBadScreens; i++)
    {
      options.add(Screen.BAD);
    }

    Collections.shuffle(options);
    instructions = true;
    showCount = false;
    testFinished = false;
    currentNumber = 0;

    // Timer
    Timer timer = new Timer(100, new ActionListener()
    {

      @Override
      public void actionPerformed(ActionEvent arg0)
      {
        update();
      }
    });
    timer.start();
  }

  public void update()
  {
    float randomFloat;
    double probabilityOfTarget;

    time = System.currentTimeMillis();

    if (instructions)
    {
      currentScreen = Screen.INSTRUCTIONS;
     // if (time > 2) currentScreen = Screen.GOOD;

    }
    else if (showCount)
    {
      currentScreen = Screen.FINISHED;
    }
    else if (wait)
    {
      if (time - startTime > waitTime)
      {

        probabilityOfTarget = currentImageNum / 5.0; // Max of 6

        // Target or False screen
        if (currentScreen == Screen.INSTRUCTIONS)
        	{ System.out.println("Parallel Port: Sending Start trigger");
        	parallelPort.write(TRIGGER_START);
        	}
        randomFloat = Library.RANDOM.nextFloat();
        if (randomFloat < probabilityOfTarget)
        {
          currentImageNum = 0;
          currentScreen = Screen.BAD;
        }
        else
        {
          currentScreen = Screen.GOOD;
          currentImageNum += 1;
          currentNumber++;
        }

        wait = false;
        startTime = System.currentTimeMillis();

        // Finished the test
        if (currentNumber > numberOfGoodScreens)
        {
          if (SEND_TRIGGERS)
          { parallelPort.write(TRIGGER_DONE);
            parallelPort.close();
          }
          System.out.println("Oddball Test Done. Total oddball count = " + oddballCount);
          showCount = true;
        }
      }
    }
    else if (time - startTime > screenTime)
    {
      wait = true;
      currentScreen = Screen.WAIT;
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

  public void render()
  {
    if (currentScreen == null) { return; }

    switch (currentScreen)
    {
    case GOOD:
      if (SEND_TRIGGERS) {System.out.println("Parallel Port: Send TRIGGER_NORMAL="+TRIGGER_NORMAL); parallelPort.write(TRIGGER_NORMAL);
      }background.setIcon(new ImageIcon(goodImage));
      
      break;
    case BAD:
      if (SEND_TRIGGERS) {System.out.println("Parallel Port: Send TRIGGER_ODDBALL="+TRIGGER_ODDBALL); parallelPort.write(TRIGGER_ODDBALL); 
      }
      background.setIcon(new ImageIcon(badImage));
      oddballCount++;
      break;
    case WAIT:
      if (SEND_TRIGGERS) {System.out.println("Parallel Port: Send TRIGGER_WAIT"+TRIGGER_WAIT); parallelPort.write(TRIGGER_WAIT);
      }background.setIcon(new ImageIcon(waitImage));
      break;
    case INSTRUCTIONS:
      background.setIcon(new ImageIcon(instructionImage));
      break;
    case FINISHED:
      background.setIcon(new ImageIcon(finishedImage));
      break;
    }
  }

  
  
  
  
  private enum Screen
  {
    INSTRUCTIONS, GOOD, BAD, WAIT, FINISHED;
  }





//  @Override
//  public void keyPressed(KeyEvent arg0)
//  {
//    // TODO Auto-generated method stub
//    
//  }
//
//  @Override
//  public void keyReleased(KeyEvent arg0)
//  {
//    // TODO Auto-generated method stub
//    
//  }
//
//  @Override
//  public void keyTyped(KeyEvent arg0)
//  {
//    int keyCode = e.getKeyCode();
//    if (keyCode == KeyEvent.VK_SPACE)
//    
//  }
}
