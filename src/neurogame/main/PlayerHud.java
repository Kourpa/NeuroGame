package neurogame.main;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Map;
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

  Rectangle area;

  public PlayerHud(NeuroGame frame, World world, User user)
  {
    this.world = world;
    this.user = user;
    sprites = Library.getSprites();
	
    windowWidth = frame.getWidth();
    windowHeight = frame.getHeight();


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
    int score = world.getPlayer().getScore();
    int highScore = Math.max(score, user.getHighScore());
    String scoreMessage = "Score: " + score;
    String highscoreMessage = "Best:  " + highScore;
    
    
    // Score
    canvasObjectLayer.setFont(Library.FONT30);
    canvasObjectLayer.setColor(Color.WHITE);
    canvasObjectLayer.drawString("Score: ", (int) (windowWidth * 0.5 - 75 - scoreMessage.length() / 2),
        (int) (windowHeight * 0.08));

    canvasObjectLayer.setColor(Library.HIGHLIGHT_TEXT_COLOR);
    canvasObjectLayer.drawString("" + score,
        (int) (windowWidth * 0.5 + 100 - scoreMessage.length() / 2), (int) (windowHeight * 0.08));

    //canvasObjectLayer.setFont(Library.FONT30);
    canvasObjectLayer.setColor(Color.WHITE);
    canvasObjectLayer.drawString("Best: ", (int) (windowWidth * 0.5 - 65 - highscoreMessage.length() / 2),
        (int) (windowHeight * 0.04));

    canvasObjectLayer.setColor(Library.HIGHLIGHT_TEXT_COLOR);
    canvasObjectLayer.drawString("" + highScore,
        (int) (windowWidth * 0.5 + 100 - highscoreMessage.length() / 2), (int) (windowHeight * 0.04));

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