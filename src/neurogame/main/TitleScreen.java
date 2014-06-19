/**
 * NeuroGame.
 * CS 351, Project 3
 *
 * Team members:
 * Ramon A. Lovato
 * Danny Gomez
 * Marcos Lemus
 */

package neurogame.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import neurogame.library.Library;
import neurogame.library.User;

import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;

/**
 * The title screen for NeuroGame.
 * 
 * @author Ramon A. Lovato
 * @team Danny Gomez
 * @team Marcos Lemus
 */
public class TitleScreen
{
  private Image titleBackground, startButtonPlain, startButtonSelected;
  private Image exitButtonPlain, exitButtonSelected;
  private Image configButtonPlain, configButtonSelected;
 
  public boolean IsExiting, IsStarting, IsOption;
  public int selectedJoystick;
  public User selectedUser;
  
  private static JButton configButton,exitButton,startButton;
  private static JButton newUserButton;
  private static ArrayList<String> Users;
  private static JTextField nameInputField;
  private JComboBox<String> userList;

  private String selected;

  private BufferedImage masterImage;
  private Graphics masterGraphics;
  
  private BufferedImage image;
  private Graphics graphics;
  private Map<String, BufferedImage> sprites;

  private int width;
  private int height;

  /**
   * Instantiate a new TitleScreen.
   * 
   * @param frame
   *          NeuroFrame to contain this TitleScreen.
   */
  public TitleScreen(final NeuroFrame frame)
  {
	
	// For Polling
	IsExiting = false;
	IsStarting = false;
	IsOption = false;
	
	
    //masterImage = new BufferedImage(Library.getWindowWidth(),
    //    Library.getWindowHeight(), BufferedImage.TYPE_INT_ARGB);
    //masterGraphics = masterImage.createGraphics();    
    //image = masterImage;
    //graphics = image.createGraphics();
    
    width = frame.getWidth();
    height = frame.getHeight();
    sprites = Library.getSprites();

    // Get the images.
    titleBackground = sprites.get("titleBackground");
    startButtonPlain = sprites.get("startButtonPlain");
    startButtonSelected = sprites.get("startButtonSelected");
    exitButtonPlain = sprites.get("exitButtonPlain");
    exitButtonSelected = sprites.get("exitButtonSelected");
    configButtonPlain = sprites.get("configButtonPlain");
    configButtonSelected = sprites.get("configButtonSelected");
    
    // New UI
    CreatePanels(frame);
    
    //
    frame.getRootPane().addComponentListener(new ComponentAdapter()
    {
      public void componentResized(ComponentEvent e)
      {
        width = frame.getWidth();
        height = frame.getHeight();
        //image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        //graphics = image.createGraphics();
        //draw();
      }
    });
    
    selected = "start";
  }
  
  private void CreatePanels(final NeuroFrame frame){
	  // Background
	    frame.getContentPane().setLayout(new FlowLayout());
	    final JLabel background = new JLabel(new ImageIcon(titleBackground));
	    background.setLayout(new GridLayout(3,1));
	    background.setBackground(Color.BLACK);
	    
	    // To align the buttons...
	    background.add(new JLabel(""));
	    
	    // Panels
	    final JPanel test = new JPanel();
	    test.setBackground(Color.BLACK);
	    test.setLayout(new FlowLayout(1,100,0));
	    
	    // Buttons
	    startButton = new JButton("");
	    startButton.setIcon(new ImageIcon(startButtonPlain));
	    startButton.setPreferredSize(new Dimension(300,100));
	    startButton.setBackground(Color.BLACK);
	    startButton.addActionListener(new ActionListener() {
	    	 
	        public void actionPerformed(ActionEvent e)
	        {
	        	background.setVisible(false);
	        	frame.getContentPane().setLayout(new GridLayout(1,1));
	            frame.getContentPane().remove(background);
	            
	            selectedUser = Library.getUser(userList.getSelectedIndex());
	            IsStarting = true;
	        }
	    });
	    
	    exitButton = new JButton("");
	    exitButton.setIcon(new ImageIcon(exitButtonPlain));
	    exitButton.setPreferredSize(new Dimension(300,100));
	    exitButton.setBackground(Color.BLACK);
	    exitButton.addActionListener(new ActionListener() {
	    	 
	        public void actionPerformed(ActionEvent e)
	        {
	            IsExiting = true;
	        }
	    });      
	        
	    configButton = new JButton("");
	    configButton.setIcon(new ImageIcon(exitButtonPlain));
	    configButton.setPreferredSize(new Dimension(300,100));
	    configButton.setBackground(Color.BLACK);
	    configButton.addActionListener(new ActionListener() {
	    	 
	        public void actionPerformed(ActionEvent e)
	        {
	            IsOption = true;
	            Options(frame);
	        }
	    });      
	        
	    // User Selection
	    JPanel userInfo = new JPanel(new GridBagLayout());
	    userInfo.setBackground(Color.BLACK);
	    
	    JLabel text = new JLabel("Select User: ");
	    text.setFont(new Font("Consolas", Font.BOLD, 32));
	    text.setForeground(Color.WHITE);
	    userInfo.add(text);
	    
	    userList = new JComboBox<String>(Library.getUserNames());
	    userList.setPreferredSize(new Dimension(250,50));
	    userInfo.add(userList);
	    
	    JLabel text2 = new JLabel("   New User: ");
	    text2.setForeground(Color.WHITE);
	    text2.setFont(new Font("Consolas", Font.BOLD, 32));
	    userInfo.add(text2);
	    
	    nameInputField = new JTextField(16);
	    nameInputField.setPreferredSize(new Dimension(400,50));
	    userInfo.add(nameInputField);
	    
	    newUserButton = new JButton("New User");
	    newUserButton.setPreferredSize(new Dimension(100,50));
	    userInfo.add(newUserButton);
	    
	    newUserButton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e)
	        {
	        	Library.addUser(nameInputField.getText());
	        	
	            nameInputField.setText("");
	            userList.removeAll();
	            userList = new JComboBox<String>(Users.toArray(new String[0]));
	            System.out.println("Users: "+Users);
	        }
	    });    
	    
	    //
	    test.add(startButton);
	    background.add(userInfo);
	    test.add(configButton);
	    test.add(exitButton);
	    
	    background.add(test);
	    frame.getContentPane().add(background);
	    frame.setVisible(true);
  }
  
  public void Options(final NeuroFrame frame){
	  ArrayList<String> ControllerNames = new ArrayList<String>();
	  final JDialog dialog = new JDialog(frame, "Options");
	  
	  // Joysticks 
      try{
          Controllers.create();
      } catch(Exception e){
          e.printStackTrace();
          System.exit(0);
      }
      
      // add the keyboard as default
      ControllerNames.add("Keyboard");

      int count = Controllers.getControllerCount();

      for(int i = 0; i < count; i++){
          Controller controller = Controllers.getController(i);
          System.out.println(controller.getName());
          ControllerNames.add(controller.getName());
      }
      
      
      // Options Menu
      final JComboBox<String> controllerList = new JComboBox<String>(ControllerNames.toArray(new String[0]));
      controllerList.setPreferredSize(new Dimension(250,40));
      
      JPanel mainBox = new JPanel();
      mainBox.setLayout(new BoxLayout(mainBox, BoxLayout.Y_AXIS));
      
      // Joysticks
      JPanel message = new JPanel();
      message.setLayout(new BorderLayout());
      message.add(new JLabel("Select Controller:"), BorderLayout.WEST);
      message.setBackground(Color.WHITE);
      message.add(controllerList, BorderLayout.EAST);
            
      
      // Buttons
      JPanel message2 = new JPanel();
      JButton exit = new JButton("Exit");
      exit.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e)
          {
        	  dialog.dispose();
          }
      });    
      
      JButton accept = new JButton("Accept");
      accept.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e)
          {
        	  //Set the Joystick then close
        	  selectedJoystick = controllerList.getSelectedIndex();
        	  dialog.dispose();
          }
      });    
      message2.add(exit);
      message2.add(accept);
      
      //
      mainBox.add(message);
      mainBox.add(message2);
      
      dialog.setModal(true);
      dialog.setContentPane(mainBox);
      dialog.pack();
      dialog.setLocationRelativeTo(frame);
      dialog.setVisible(true);
  }

  /**
   * Toggles which button is selected.
   */
  public void switchButton()
  {
    selected = (selected == "start" ? "exit" : "start");
    //draw();
  }

  /**
   * Getter for selected.
   * 
   * @return selected String representation of selected button.
   */
  public String getSelected()
  {
    return selected;
  }

  /**
   * Getter for the buffered image.
   * 
   * @return BufferedImage of the title screen.
   */
  public BufferedImage getImage()
  {
    //System.out.println("TitleScreen.getImage(): image.size = ("+image.getWidth()+", "+image.getHeight()+")");
    return masterImage;
  }

}
