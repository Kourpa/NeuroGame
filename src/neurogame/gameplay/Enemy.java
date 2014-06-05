package neurogame.gameplay;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;

import neurogame.level.World;
import neurogame.library.Library;

public abstract class Enemy extends GameObject
{

  public Enemy(double x, double y, double width, double height, String name,
      Image image, World world)
  {
    super(x, y, width, height, name, image, world);
  }
  

  public boolean checkCollisionWithPlayer()
  {
    if (!isActive()) return false;

    if (collision(player))
    {
      setActive(false);
//      Library.log(getName() + " collided with player", world.getRisk());
      double hitX = (getCenterX() + player.getCenterX())/2.0;
      double hitY = (getCenterY() + player.getCenterY())/2.0;
      
      player.enemyKilled();
      player.loseHealth(hitX, hitY, Library.DAMAGE_PER_ENEMY_HIT);
      return true;
    }
    return false;
  }

  
  public boolean checkCollisionWithWall()
  {
    if (!isActive()) return false;
    
    if (wallCollision())
    { setActive(false);
      return true;
    }
    return false;
  }

}
