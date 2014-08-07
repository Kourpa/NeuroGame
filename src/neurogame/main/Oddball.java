package neurogame.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

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
  private static final byte TRIGGER_START = 0x15;
  private static final byte TRIGGER_WAIT = 0x2;
  private static final byte TRIGGER_NORMAL = 0x4;
  private static final byte TRIGGER_ODDBALL = 0x8;
  private static final byte TRIGGER_DONE = 0x1;

  private SocketToParallelPort socket;
  private static final String HOST = "127.0.0.1";
  private static final int PORT = 55555;

  private static boolean SEND_TRIGGERS_VIA_SOCKET = false;

  private boolean finishedInstructions;
  private long time;
  private NeuroFrame frame;
  private NeuroGame game;
  private JPanel pane;
  private final int numberOfGoodScreens = 50;
  private final int numberOfBadScreens = 200;
  private int currentNumber;
  private boolean testFinished;
  private boolean showCount;
  private final ArrayList<Screen> options;
  private Screen currentScreen;
  private BufferedImage badImage, goodImage, waitImage, instructionImage, finishedImage;

  private long startTime, screenTime, waitTime;
  private boolean wait, instructions;
  private int currentImageNum = 0;

  private int oddballCount = 0;

  private JLabel background;


  public Oddball(final NeuroFrame frame, NeuroGame game)
  {
	  this.frame = frame;
	  this.game = game;
	  	  
	if(game.getLoggingMode()){
		SEND_TRIGGERS_VIA_SOCKET=true;
	}
	  
    if (SEND_TRIGGERS_VIA_SOCKET)
    {
      socket = new SocketToParallelPort(HOST, PORT);
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
    
    finishedInstructions = false;

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
    msg1.setBounds((int)(width*0.15), (int)(height*0.02), width, 150);
    
    
    // Instructions with a scroll pane
    JTextArea msg2 = new JTextArea(msgInstructions);
    msg2.setForeground(Color.white);
    msg2.setBackground(Color.BLACK);
    msg2.setEditable(false);
    msg2.setLineWrap(true);
    msg2.setWrapStyleWord(true);
    msg2.setFont(FONT_SMALL);
    
    JScrollPane areaScrollPane = new JScrollPane(msg2);
    areaScrollPane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    areaScrollPane.setBorder(null);
    areaScrollPane.setBounds((int)(width*0.2), (int)(height*0.2), (int)(width*0.6), 150);
    areaScrollPane.setOpaque(false);
    areaScrollPane.setBackground(new Color(0,0,0,0));
    areaScrollPane.setForeground(Color.BLACK);
    
    
    // Message explaining the controlls
    JLabel msg3 = new JLabel(msgInput);
    msg3.setForeground(Color.BLUE);
    msg3.setFont(FONT_SMALL);
    msg3.setBounds((int)(width*0.2), (int)(height*0.75), width, 150);
    
    // The enemy and friendly ship
    JLabel enemyImage = new JLabel(new ImageIcon(goodImage));
    enemyImage.setBounds((int)(width*0.25)-128, (int)(height*0.5), 256, 256);
    
    JLabel friendlyImage = new JLabel(new ImageIcon(badImage));
    friendlyImage.setBounds((int)(width*0.75)-128, (int)(height*0.5), 256, 256);
    
    JLabel enemyText = new JLabel(msgEnemy);
    enemyText.setBounds((int)(width*0.25)-128, (int)(height*0.3), 256, 256);
    enemyText.setFont(FONT_SMALL);
    
    JLabel friendlyText = new JLabel(msgFriendly);
    friendlyText.setFont(FONT_SMALL);
    friendlyText.setBounds((int)(width*0.75)-128, (int)(height*0.3), 256, 256);
    
    // background for displaying the images
    background = new JLabel();
    background.setBackground(Color.BLACK);
    background.setBounds(width/2 - 128, 0, width, height);
    
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

    wait = instructions = false;
    startTime = System.currentTimeMillis();
    screenTime = 800 + Library.RANDOM.nextInt(400);
    waitTime = 1600 + Library.RANDOM.nextInt(400);
    time = startTime;
    options = new ArrayList<>();

    for (int i = 0; i < numberOfGoodScreens; i++)
    {
      options.add(Screen.NORMAL);
    }
    for (int i = 0; i < numberOfBadScreens; i++)
    {
      options.add(Screen.ODDBALL);
    }

    Collections.shuffle(options);
    instructions = true;
    showCount = false;
    testFinished = false;
    currentNumber = 0;
    
    // Start the test
    update();
  }

  public void update()
  {
    float randomFloat;
    double probabilityOfTarget;
    
    while(true){
    	
    	// This only runs once when you start the game
    	if((!instructions) && (finishedInstructions == false)){
    		finishedInstructions = true;
    		pane.setVisible(false);
    		frame.getContentPane().remove(pane);
    		
    		System.out.println("Parallel Port: Sending Start trigger");
            if (socket != null)
            {
              socket.sendByte(TRIGGER_START);
            }
        }
    	
    	// Which screen to display
	    if (instructions){
	    }
	    else if (showCount){
	      currentScreen = Screen.FINISHED;
	    }
	    
	    else if (wait){	
	        probabilityOfTarget = currentImageNum / 5.0; // Max of 6
		        
	        // Choose which image to display
	        randomFloat = Library.RANDOM.nextFloat();
	        if (randomFloat < probabilityOfTarget){
	          currentImageNum = 0;
	          currentScreen = Screen.ODDBALL;
	        }
	        else{
	          currentScreen = Screen.NORMAL;
	          currentImageNum += 1;
	          currentNumber++;
	        }
	
	        wait = false;
	
	        // Finished the test
	        if (currentNumber > numberOfGoodScreens)
	        {
	          if (socket != null)
	          {
	            socket.sendByte(TRIGGER_DONE);
	            socket.close();
	          }
	
	          System.out.println("Oddball Test Done. Total oddball count = " + oddballCount);
	          showCount = true;
	        }
	      }
    	
	    else if (wait==false)
	    {
	    	wait = true;
	    	currentScreen = Screen.WAIT;
	    }
	    	
	    render();
	    try{
	    	Thread.sleep(screenTime);
	    }catch(Exception e){
	    }
    }
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
  
  public void forceClose(){
	  background.setVisible(false);
      frame.getContentPane().removeAll();
      frame.getContentPane().setLayout(null);
      testFinished = true;
      game.showTitle();
  }

  public void render()
  {
    if (currentScreen == null) { return; }

    switch (currentScreen)
    {
    case NORMAL:
      if (socket != null)
      {
        System.out.println("Oddball: Socket Send TRIGGER_NORMAL=" + TRIGGER_NORMAL);
        socket.sendByte(TRIGGER_NORMAL);
      }
      background.setIcon(new ImageIcon(goodImage));
      break;

    case ODDBALL:
      if (socket != null)
      {
        System.out.println("Oddball: Socket Send TRIGGER_NORMAL=" + TRIGGER_ODDBALL);
        socket.sendByte(TRIGGER_ODDBALL);
      }
      background.setIcon(new ImageIcon(badImage));
      oddballCount++;
      break;

    case WAIT:
      if (socket != null)
      {
        System.out.println("Oddball: Socket Send TRIGGER_NORMAL=" + TRIGGER_WAIT);
        socket.sendByte(TRIGGER_WAIT);
      }
      background.setIcon(new ImageIcon(waitImage));
      break;

    case INSTRUCTIONS:
      //background.setIcon(new ImageIcon(instructionImage));
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
