package neurogame.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JLabel;

import neurogame.io.User;
import neurogame.level.World;
import neurogame.library.Library;

public class PlayerHud
{
  private int windowWidth;
  private int windowHeight;

  private World world;
  private User user;
  
  //private JLabel[] labelScores;

  private Map<String, BufferedImage> sprites;

  
  private static final Color COOL_GREEN = new Color(113, 188, 120);
  private static final Color DARK_GREEN = new Color(60, 100, 64);
  private static final Color DARK_ORANGE = new Color(204, 85, 0);
  private static final Color CRIMSON = new Color(220, 20, 60);
  private static final Color DARK_RED = new Color(109, 10, 30);
  

  private GradientPaint healthPaintFull = new GradientPaint(0, 0, COOL_GREEN, 0, 20, DARK_GREEN, true);
  private GradientPaint healthPaintDamaged = new GradientPaint(0, 0, Color.ORANGE, 0, 20, DARK_ORANGE, true);
  private GradientPaint healthPaintNearDeath = new GradientPaint(0, 0, CRIMSON, 0, 20, DARK_RED, true);

  private BufferedImage MissleIcon;
  Rectangle rect = new Rectangle(0, 0, 100, 50);
  int last_x, last_y;

  boolean firstTime = true;
  boolean isDead = false;

  private static final Color BLUE = new Color(100, 191, 255);
  private Font FONT30;
  private Font FONT70;

  Rectangle area;

  public PlayerHud(NeuroGame frame, World world, User user)
  {
    this.world = world;
    this.user = user;
    sprites = Library.getSprites();
	
    windowWidth = frame.getWidth();
    windowHeight = frame.getHeight();

    FONT30 = new Font("Karmatic Arcade", Font.PLAIN, 23);
    FONT70 = new Font("Karmatic Arcade", Font.PLAIN, 70);
    MissleIcon = sprites.get("missileIcon");
  }

  // private void loadFont(){
  // String path = System.getProperty("user.dir");
  // path += "/resources/fonts/";
  //
  // try {
  // GraphicsEnvironment ge = GraphicsEnvironment
  // .getLocalGraphicsEnvironment();
  // ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File(path
  // + "KarmaticArcade.ttf")));
  // System.out.println("Registered Font");
  // } catch (IOException | FontFormatException e) {
  // System.out.println("Error Loading Font - PlayerHUD.java");
  // }
  //
  //
  // }

  public void updateHUD(Graphics2D canvasObjectLayer, NeuroGame frame, int ammo)
  {
    String highscoreMessage = "Best:  " + user.getHighScore();
    String scoreMessage = "Score: " + world.getPlayer().getScore();
    String highScores = "High Scores: ";
    // Score
    canvasObjectLayer.setFont(FONT30);
    canvasObjectLayer.setColor(Color.WHITE);
    canvasObjectLayer.drawString("Score: ", (int) (windowWidth * 0.5 - 75 - scoreMessage.length() / 2),
        (int) (windowHeight * 0.08));

    canvasObjectLayer.setColor(BLUE);
    canvasObjectLayer.drawString("" + world.getPlayer().getScore(),
        (int) (windowWidth * 0.5 + 100 - scoreMessage.length() / 2), (int) (windowHeight * 0.08));

    canvasObjectLayer.setFont(FONT30);
    canvasObjectLayer.setColor(Color.WHITE);
    canvasObjectLayer.drawString("Best: ", (int) (windowWidth * 0.5 - 65 - highscoreMessage.length() / 2),
        (int) (windowHeight * 0.04));

    canvasObjectLayer.setColor(BLUE);
    canvasObjectLayer.drawString("" + user.getHighScore(),
        (int) (windowWidth * 0.5 + 100 - highscoreMessage.length() / 2), (int) (windowHeight * 0.04));

    // Only display this
    if (!isDead)
    {

      // Ammo
      for (int i = 0; i < ammo; i++)
      {
        if (i < 10)
        {
          canvasObjectLayer.drawImage(MissleIcon, null, (int) (windowWidth * 0.8 + MissleIcon.getWidth() * (i % 10)
              * 1.1), (int) (windowHeight * 0.015));
        }
        else
        {
          canvasObjectLayer.drawImage(MissleIcon, null, (int) (windowWidth * 0.8 + MissleIcon.getWidth() * (i % 10)
              * 1.1), (int) (windowHeight * 0.055));
        }
      }

      // Health bar
      drawHealth(canvasObjectLayer, frame);
    }

    // Show game over
    if (isDead)
    {    	
      canvasObjectLayer.setFont(FONT70);
      canvasObjectLayer.setColor(Color.blue);
      canvasObjectLayer.drawString("GAME OVER", (int) (windowWidth * 0.47 - 225), (int) (windowHeight * 0.25));

      //canvasObjectLayer.setFont(FONT30);
      //canvasObjectLayer.setColor(Color.blue);
      //canvasObjectLayer.drawString(highscoreMessage, (int) (windowWidth * 0.475 - 225), (int) (windowHeight * 0.35));
      
      canvasObjectLayer.setFont(FONT30);
      canvasObjectLayer.setColor(Color.white);
      canvasObjectLayer.drawString(highScores, (int) (windowWidth * 0.475 - 75), (int) (windowHeight * 0.375));
      
      // ADD HIGH SCORES IN HERE
      canvasObjectLayer.setFont(FONT30);
      canvasObjectLayer.setColor(Color.white);
      ArrayList<User> userList = User.getUserList();
      double yValue = 0.45;
      for (int i = 0; i < userList.size(); i++)
      {
    	  User cUser = userList.get(i);
    	  if (cUser == user) 
    	  {
    		canvasObjectLayer.setColor(GUI_util.COLOR_SELECTED);
    	  }
    	  else
    	  {
    	    canvasObjectLayer.setColor(Color.white);
    	  }
    	  int score = cUser.getHighScore();
    	  if (score > 0)
    	  {
    		  String str = score + "    " + cUser.getHighScoreDate() + "     " + cUser.getName();
    		  canvasObjectLayer.drawString(str,  (int) (windowWidth * 0.475 - 225), (int) (windowHeight * yValue));
    		  yValue += .05;
    	  }
      }      
      
      canvasObjectLayer.setFont(new Font("Karmatic Arcade", Font.PLAIN, 25));
      canvasObjectLayer.setColor(BLUE);
      canvasObjectLayer.drawString("[ Press Spacebar or Game controller button ]", (int) (windowWidth * 0.275 - 125), (int) (windowHeight * 0.8));
    }
  }

  public void drawGameOver(boolean dead)
  {
    isDead = dead;
  }

  /**
   * Draw the health display.
   */
  public void drawHealth(Graphics2D canvasObjectLayer, NeuroGame frame)
  {
    int health = world.getPlayer().getHealth();
    Color outline = COOL_GREEN;
    if (health >= 0.9 * Library.HEALTH_MAX)
    {
      canvasObjectLayer.setPaint(healthPaintFull);
    }
    else if (health > 0.2 * Library.HEALTH_MAX)
    {
      canvasObjectLayer.setPaint(healthPaintDamaged);
      outline = Color.ORANGE;
    }
    else
    {
      canvasObjectLayer.setPaint(healthPaintNearDeath);
      outline = Color.RED;
    }

    int width = (health * 300) / Library.HEALTH_MAX;
    canvasObjectLayer.fillRect(5, 5, width, 32);
    canvasObjectLayer.setPaint(outline);
    canvasObjectLayer.drawRect(5, 5, 300, 32);

  }
}