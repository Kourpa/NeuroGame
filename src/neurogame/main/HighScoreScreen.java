package neurogame.main;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import neurogame.io.User;
import neurogame.library.Library;


@SuppressWarnings("serial")
public class HighScoreScreen extends JPanel implements ActionListener, KeyListener
{

	public int selectedJoystick;
	public User currentUser;

	private JButton buttonMainMenu, buttonRestart;
	

	private int width;
	private int height;
	
	private JLabel labelTitle;
	private JLabel[] labelScores;
	
	
	
	private NeuroGame frame;
	

	public HighScoreScreen(NeuroGame frame) 
	{
	  this.frame = frame;
//		width = (int)(frame.getWidth()*0.75);
//		height = (int)(frame.getHeight()*0.75);;
//
//		int left = (frame.getWidth() - width)/2;
//		int top = (frame.getHeight() - height)/2;
//		
//		this.setBounds(left, top, width, height);
    width = Library.getWindowPixelWidth();
    height = Library.getWindowPixelHeight();

  
    this.setBounds(0, 0, width, height);
	  
		this.setVisible(false);
		
		this.setLayout(null);
		this.setBackground(Color.BLACK);

		labelTitle = GUI_util.makeLabel30("High Scores", this);
		
		ArrayList<User> userList = User.getUserList();
		labelScores = new JLabel[userList.size()]; 
		
    FontMetrics fm30 = this.getFontMetrics(GUI_util.FONT36);
    FontMetrics fm20 = this.getFontMetrics(GUI_util.FONT20);
    
    int fontW20 = fm20.stringWidth("X");
    int fontW30 = fm30.stringWidth("X");
    int edge = 25;
    
    int titleWidth = fm30.stringWidth(labelTitle.getText());
    int titleLeft = (width - titleWidth)/2;
    

    
    int fontH30 = fm30.getLeading() + fm30.getMaxAscent() + fm30.getMaxDescent();
    int boxH30  = fontH30 + 8;
    int rowH30  = boxH30 + 3;
    
    int fontH20 = fm20.getLeading() + fm20.getMaxAscent() + fm20.getMaxDescent();
    int boxH20  = fontH20 + 8;
    int rowH20  = boxH20 + 3;
		
    int scoreListTop = edge + rowH30;
    labelTitle.setBounds(titleLeft, edge, titleWidth, boxH30);
		
		for (int i=0; i<labelScores.length; i++) 
		{ 
		  int row = scoreListTop + i*rowH20; 
		  labelScores[i] = GUI_util.makeLabel20(" ", this);
		  labelScores[i].setForeground(GUI_util.COLOR_DESELECTED);
		  labelScores[i].setBounds(edge, row, width-edge, boxH20);
		}


		buttonRestart = GUI_util.makeButton("New Game", this, this);


		buttonMainMenu = GUI_util.makeButton("Main Menu", this, this);

		
		int butRow = (height - rowH30) - edge;
		int butWidth = fontW30*10;
    int col1L = edge;
    int col2L =  (width - butWidth) - edge;
		buttonRestart.setBounds(col1L, butRow, butWidth, boxH30);
		buttonMainMenu.setBounds(col2L, butRow, butWidth, boxH30);
		
		GUI_util.setSelected(buttonRestart, true);
		this.addKeyListener(this);
	}
	
	
	
	public void showScorePanel(User currentUser)
	{
	  this.currentUser = currentUser;
	  ArrayList<User> userList = User.getUserList();
	  for (int i=0; i<labelScores.length; i++)
	  { 
	    User user = userList.get(i);
	    if (user == currentUser) labelScores[i].setForeground(GUI_util.COLOR_SELECTED);
	    int score = user.getHighScore();
	    if (score > 0)
	    {
	      String str = score + "       " + user.getHighScoreDate() + "      " + user.getName(); 
	      labelScores[i].setText(str);
	    }  
	  }
	  setVisible(true); 
	  this.requestFocus();
	}
	
	
	public void update()
	{
	  this.requestFocus();
	  
	  //System.out.println("HighScoreScreen.update()");
//
//	  if (game.getGameOverScreen() != null)
//	    {
//	      if (joystick != null)
//	      {
//	        game.getGameOverScreen().updateJoystick(joystick, joystickAxisX, joystickAxisY);
//	      }
//	
//	      if (game.getGameOverScreen().IsStarting)
//	      {
//	        disableAll();
//	        game.showTitle();
//	      }
//	      else if (game.getGameOverScreen().IsExiting)
//	      {
//	        disableAll();
//	        game.quit();
//	      }
//	      else if (game.getGameOverScreen().IsRestarting)
//	      {
//	        disableAll();
//	        //game.newGame();
//	      }
//	    } 
  }
	


  public void keyTyped(KeyEvent arg0) {
  }

  

	public void actionPerformed(ActionEvent event) 
  {
    Object source = event.getSource();
    if (source == buttonRestart) frame.startGame(currentUser);
    else if (source == buttonMainMenu) frame.showTitle();
	}



  @Override
  public void keyPressed(KeyEvent event)
  {
    int code = event.getKeyCode();
    System.out.println("keyTyped code= " + code);
    
    if ((code == KeyEvent.VK_ENTER) || (code == KeyEvent.VK_SPACE))
    { 
      if (buttonMainMenu.isSelected()) frame.showTitle();
      else frame.startGame(currentUser);
    }
    
    if ((code == KeyEvent.VK_TAB) || (code == KeyEvent.VK_LEFT) || (code == KeyEvent.VK_RIGHT))
    {
      if (buttonMainMenu.isSelected())
      { GUI_util.setSelected(buttonMainMenu, false);
        GUI_util.setSelected(buttonRestart, true);
      }
      else 
      { GUI_util.setSelected(buttonMainMenu, true);
        GUI_util.setSelected(buttonRestart, false);
      }
    } 
  }



  public void keyReleased(KeyEvent event)
  {

  }
}

