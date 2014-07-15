
package neurogame.gameplay;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

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
    
    if (x < Library.leftEdgeOfWorld + GameObjectType.INFO.getWidth())
    {  setLocation(Library.leftEdgeOfWorld + GameObjectType.INFO.getWidth(),   getY());
    }
    
    
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
    
    //System.out.println("InfoMessage.update(): ("+getX()+", "+getY()+")  leftEdgeOfWorld="+ Library.leftEdgeOfWorld +
 //     ", Screen(x)="+ Library.worldPosXToScreen(getX()) +", WindowPixelWidth()="+ Library.getWindowPixelWidth());
    if (!Library.isOnScreen(getX(), getY())) die(false);
    
   
    if (!isAlive()) return;

    double maxDistanceChange = GameObjectType.INFO.getMaxSpeed() * deltaSec;

    move(scrollDistance, -maxDistanceChange);
    
  }
  
  
  public void hit(GameObject obj)
  { 

  }
    



  
  
  
  public void render(Graphics2D g)
  {
    int xx = Library.worldPosXToScreen(getX());
    int yy = Library.worldPosYToScreen(getY());
    g.setColor(msgColor);
    g.setFont(msgFont);
    g.drawString(msg, xx, yy);
  }

}
