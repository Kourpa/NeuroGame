
package neurogame.gameplay;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;

import neurogame.level.World;
import neurogame.library.Library;

public class InfoMessage extends GameObject
{
  private String msg;
  private static final Font msgFont = new Font("Verdana", Font.BOLD, 48);
  private static final Color msgColor = new Color(113, 188, 120);
 
  public InfoMessage(double x, double y, World world, String msg)
  {
    super(GameObjectType.INFO, x, y, world);  
    this.msg = msg;
  }
  

  
  public void die(boolean showDeathEffect)
  { 
    if (isAlive())
    { isAlive = false;
    }
  }
  
  

  
  public void update(double deltaSec, double scrollDistance)
  {
    if (!Library.isOnScreen(getX(), getY())) die(false);
   
    if (!isAlive()) return;

    double maxDistanceChange = GameObjectType.INFO.getMaxSpeed() * deltaSec;

    move(-scrollDistance, -maxDistanceChange);
    //System.out.println("Missile Update: (" + getX() + ", " + getY() + ")" );
    
  }
  
  
  public void hit(GameObject obj)
  { 
    GameObjectType type = obj.getType();
    
    if (type.isEnemy()) player.defeatedEnemy(type);
    die(true);
  }
    



  
  
  
  public void render(Graphics2D g)
  {
    int xx = Library.worldPosXToScreen(getX());
    int yy = Library.worldPosYToScreen(getY());
    g.setColor(msgColor);
    g.setFont(msgFont);
    g.drawString(msg, yy, yy);
  }

}
