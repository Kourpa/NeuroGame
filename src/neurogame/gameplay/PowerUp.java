/**
 * NeuroGame.
 * CS 351, Project 3
 * 
 * Team members:
 * Ramon A. Lovato
 * Danny Gomez
 * Marcos Lemus
 */

package neurogame.gameplay;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

import neurogame.level.World;
import neurogame.library.Library;

/**
 * A PowerUp class for NeuroGame.
 * 
 * @author Ramon A. Lovato
 * @team Danny Gomez
 * @team Marcos Lemus
 */

public class PowerUp extends GameObject
{
  public static final double DEFAULT_WIDTH = 0.075;
  public static final double DEFAULT_HEIGHT = 0.075;
  public static final double DEFAULT_SPEED = 0.005;

  private static final float ALPHA_DELTA = (float) ((2 * Math.PI) / (Library.MIN_FRAME_MILLISEC));
  private static String name = "powerUp";
  private static BufferedImage image = Library.getSprites().get(name);

  private PowerType type;
  private BufferedImage uiImage;

  private Player player = null;
  private double speed = DEFAULT_SPEED;
  private int timer;
  private boolean inUse;
  private boolean alreadyUsed;
  private Direction direction;
  // For animation.
  private int frameCounter;
  private float alphaStep;
  private float alpha;

  private boolean laserDraw = false;
  private double laserWidth;
  private double laserHeight;
  protected Path2D laserPoly = new Path2D.Double(); // it's a hit box
  private int laserCounter = 0;

  private boolean superDraw = false;

  private boolean bombDraw = false;

  private double blinkRange = 0.25;

  /**
   * Instantiate a new PowerUp with a random type.
   * 
   * @param x
   *          X-coordinate at which to spawn the PowerUp.
   * @param y
   *          Y-coordinate at which to spawn the PowerUp.
   * @param world
   *          World in which to spawn the PowerUp.
   * @param direction
   *          Direction this PowerUp should move.
   */
  public PowerUp(double x, double y, World world, Direction direction)
  {
    this(x, y, world, direction, PowerType.values()[Library.RANDOM
        .nextInt(PowerType.values().length)]);
  }

  /**
   * Instantiate a new PowerUp of the provided type.
   * 
   * @param x
   *          X-coordinate at which to spawn the PowerUp.
   * @param y
   *          Y-coordinate at which to spawn the PowerUp.
   * @param world
   *          World in which to spawn the PowerUp.
   * @param @param direction Direction this PowerUp should move.
   * @param type
   *          PowerType to give this PowerUp.
   */
  public PowerUp(double x, double y, World world, Direction direction,
      PowerType type)
  {
    super(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, name, world);
    this.type = type;
    uiImage = type.getUIImage();
    timer = type.getTimerMax();
    inUse = false;
    alreadyUsed = false;
    frameCounter = 0;
    alphaStep = 0;
    alpha = 1.0f;
    this.direction = direction;
  }

  /**
   * Override of GameObject's update - updates object state every frame.
   */
  @Override
  public boolean update(double deltaTime, double scrollDistance)
  {
    if (getX() < Library.leftEdgeOfWorld) return false;
    if (isAlive())
    {
      animate();
      if (direction != Direction.NONE)
      {
        move();
      }
      // update the player obj
      player = world.getPlayer();

      // System.out.println(collision(player) + "Player: ("+ player.getHitMinX()
      // + "," + player.getHitMinY() + ") - (" +
      // player.getHitMaxX() + "," + player.getHitMaxY() + ")  " +
      // getName() + ":(" + getHitMinX() + "," + getHitMinY() + ") - (" +
      // getHitMaxX() + "," + getHitMaxY() + ")  ");

      if (collision(player))
      {
        pickUp();
      }
    }
    else if (inUse)
    {
      if (--timer == 0)
      {
        deactivate();
      }
      else
      {
        performAction();
        animateUI();
      }
    }

    return false;
  }

  /**
   * Animate the powerUp.
   */
  private void animate()
  {
    frameCounter++;
    // TODO: change sprites.
    switch (type)
    {
    case BLINK:
      break;
    case BOOST:
      break;
    case LASER:
      break;
    case BOMB:
      break;
    case SUPER:
      break;

    }

  }

  /**
   * Animate the UI image.
   */
  private void animateUI()
  {
    alphaStep += ALPHA_DELTA;
    alpha = (float) (Math.cos(alphaStep) * 0.5 + 0.5);
  }

  /**
   * Move this powerUp.
   */
  private void move()
  {
    double x = getX();
    double y = getY();
    if (direction == Direction.LEFT)
    {
      setLocation(x - speed, y);
    }
    else if (direction == Direction.RIGHT)
    {
      setLocation(x + speed, y);
    }
  }

  /**
   * Activate the powerUp's effect.
   */
  public void activate()
  {
    if (player != null && !alreadyUsed && !inUse)
    {
      inUse = true;
      // Library.log("Player used powerup " + type, world.getRisk());
      switch (type)
      {
      // TODO
      case BLINK:
        break;
      case BOMB:
        break;
      case LASER:
        break;
      case SUPER:
        player.setInvulnerable(true);
        break;
      default:
        break;
      }
    }
  }

  /**
   * Perform one frame of the powerUp's effect.
   */
  public void performAction()
  {
    switch (type)
    {

    case BOMB:
      bombDraw = true;
      // for (GameObject o : world.getObjectList())
      // {
      // if (o instanceof Enemy && Library.isOnScreen(o))
      // {
      // // o.active = false;
      // o.hitByEmp();
      // }
      // }
      // break;
      // case BOOST:
      // for (GameObject o : world.getObjectList())
      // {
      // if (o instanceof Enemy && player.collision(o))
      // {
      // o.killedByBoost();
      // }
      // }
      // break;
      // case LASER:
      // laserDraw = true;
      //
      // // update laser polygon
      // updateLaserPoly();
      //
      // for (GameObject o : world.getObjectList())
      // {
      // if (o instanceof Enemy && Library.isOnScreen(o))
      // {
      // Area areaLaser = new Area(laserPoly);
      // boolean hit = false;
      // if (areaLaser.contains(o.getX(), o.getY())) hit = true;
      // else if (areaLaser.contains(o.getX() + getWidth(), o.getY())) hit =
      // true;
      // else if (areaLaser.contains(o.getX(), o.getY() + getHeight())) hit =
      // true;
      // else if (areaLaser.contains(o.getX() + getWidth(), o.getY() +
      // getHeight())) hit = true;
      //
      // if (hit)
      // {
      // o.killedByLaser();
      // }
      // }
      // }
      //
      // break;
      // case SUPER:
      // superDraw = true;
      // for (GameObject o : world.getObjectList())
      // {
      // if (player.collision(o) && (o instanceof Enemy))
      // {
      // o.killedBySuper();
      // }
      // }
      // break;
      // default:
      // break;
    }
  }

  /**
   * Deactivate the powerUp's effect.
   */
  public void deactivate()
  {
    if (inUse)
    {
      // Library.log("Powerup " + type + " deactivated", world.getRisk());
      switch (type)
      {
      // TODO
      case BLINK:
        break;
      case BOMB:
        bombDraw = false;
        break;
      case LASER:
        laserDraw = false;
        laserCounter = 0;
        break;
      case SUPER:
        superDraw = false;
        player.setInvulnerable(false);
        break;
      default:
        break;
      }
      inUse = false;
    }
    alreadyUsed = true;
  }

  /**
   * Pick up the PowerUp - links it to the Player and stops drawing it in the
   * world.
   */
  public void pickUp()
  {
    // If the PowerUp is inactive, it was already picked up, so ignore it.
    if (isAlive())
    {
      PowerUp old = player.getPowerUp();
      if (old != null)
      {
        old.deactivate();
      }

      // update player score
      player.collectPowerUp();

      System.out.println("Player collected powerup " + type);
      // Library.log("Player collected powerup " + type, world.getRisk());

      player.setPowerUp(this);
      die();
    }

    // set the size of the laser power up
    laserWidth = 1;
    laserHeight = player.getHeight() * 5;
  }

  /**
   * Draws the effect of the powerup.
   * 
   * @param g
   *          - Graphics object
   */
  public void drawEffect(Graphics2D g)
  {
    if (laserDraw)
    {
      AffineTransform oldTransform = g.getTransform();
      g.setTransform(AffineTransform.getScaleInstance(Library.U_VALUE,
          Library.U_VALUE));

      g.setColor(new Color(0.0f, 0.0f, 1.0f, (alpha * 0.5f + 0.5f)));
      g.fill(laserPoly);

      g.setTransform(oldTransform);
    }

    // if (bombDraw)
    // {
    // double radius = 0.1;
    // g.setColor(Color.BLUE);
    // for (int i = 0; i < 10; i++)
    // {
    // g.drawOval(
    // Library.worldPosXToScreen(player.getX() - radius),
    // Library.worldPosXToScreen(player.getY() - radius),
    // Library.worldToScreen(player.getWidth() + radius * 2),
    // Library.worldToScreen(player.getHeight() + radius * 2));
    // radius -= 0.01;
    // }
    // }

    // if (superDraw)
    // {
    //
    // int a = (int) (255 * alpha);
    //
    // g.setColor(new Color(255, 255, 224, a));
    // g.fillOval(Library.worldToScreen(player.getX()),
    // Library.worldToScreen(player.getY()),
    // Library.worldToScreen(player.getWidth()),
    // Library.worldToScreen(player.getHeight()));
    // }
  }

  public void updateLaserPoly()
  {
    double c = 0.03 * laserCounter;

    if (c <= laserWidth)
    {
      laserCounter++;
    }

    Player p = player;

    laserPoly.reset();

    laserPoly.moveTo(p.getX() + p.getWidth(), p.getY() + p.getHeight() / 2);
    laserPoly.lineTo(p.getX() + p.getWidth() * 2, p.getY() + p.getHeight() / 2
        - laserHeight / 2);
    laserPoly.lineTo(p.getX() + p.getWidth() * 2 + c, p.getY() + p.getHeight()
        / 2 - laserHeight / 2);
    laserPoly.lineTo(p.getX() + p.getWidth() * 2 + c, p.getY() + p.getHeight()
        / 2 + laserHeight / 2);
    laserPoly.lineTo(p.getX() + p.getWidth() * 2, p.getY() + p.getHeight() / 2
        + laserHeight / 2);

    laserPoly.closePath();
  }

  /**
   * Getter for this PowerUp's type.
   * 
   * @return PowerType for this PowerUp.
   */
  public PowerType getType()
  {
    return type;
  }

  /**
   * Getter for this PowerUp's current timer.
   * 
   * @return How many frames are left in this PowerUp's life.
   */
  public int getTimer()
  {
    return timer;
  }

  /**
   * Getter for whether this PowerUp is in use.
   * 
   * @return Whether this PowerUp is currently in use.
   */
  public boolean isInUse()
  {
    return inUse;
  }

  /**
   * Getter for whether this PowerUp has already been used.
   * 
   * @return Whether this PowerUp has already been used.
   */
  public boolean isAlreadyUsed()
  {
    return alreadyUsed;
  }

  /**
   * A pass-through getter for this PowerUp's type's flavor text.
   * 
   * @return The String to draw on screen when picking up this PowerUp.
   */
  public String getFlavorText()
  {
    return type.getFlavorText();
  }

  /**
   * Getter for the image to draw onto the UI.
   * 
   * @return BufferedImage of the sprite to draw onto the GUI.
   */
  public BufferedImage getUIImage()
  {
    return uiImage;
  }

  /**
   * Getter for alpha (used in HUD).
   * 
   * @return Alpha to use for the UIImage in the HUD as a float.
   */
  public float getAlpha()
  {
    return alpha;
  }

  /**
   * Getter for speed.
   * 
   * @return Speed of this PowerUp as a double.
   */
  public double getSpeed()
  {
    return speed;
  }

  /**
   * Setter for speed.
   * 
   * @param speed
   *          Double to use as new speed.
   */
  public void setSpeed(double speed)
  {
    this.speed = speed;
  }

  /**
   * Getter for direction.
   * 
   * @return Direction enum determining movement.
   */
  public Direction getDirection()
  {
    return direction;
  }

  /**
   * Setter for direction.
   * 
   * @param direction
   *          New direction of travel.
   */
  public void setDirection(Direction direction)
  {
    this.direction = direction;
  }

  public void render(Graphics2D g)
  {
    if (isAlive())
    {
      int xx = Library.worldPosXToScreen(getX());
      int yy = Library.worldPosYToScreen(getY());
      g.drawImage(image, xx, yy, null);
    }
    if (inUse)
    {
      drawEffect(g);
    }
  }

  /**
   * A nested enum for the direction of movement for PowerUps.
   */
  public static enum Direction
  {
    LEFT, RIGHT, NONE;
  }

  /**
   * A nested enum to represent the type of PowerUp.
   */
  public static enum PowerType
  {
    BLINK(2, "Now You're Thinking With...", "powerupBlinkForeground"), BOOST(
        Library.MIN_FRAME_MILLISEC / 2, "I Like to Go Fast",
        "powerupBoostForeground"), LASER(3 * Library.MIN_FRAME_MILLISEC,
        "Huge Freakin' Laser", "powerupLaserForeground"), BOMB(
        Library.MIN_FRAME_MILLISEC / 4, "Surprisingly Useful Bomb",
        "powerupBombForeground"), SUPER(5 * Library.MIN_FRAME_MILLISEC,
        "Don't-Die Button", "powerupSuperForeground");

    public final String name;
    public final String uiImageName;
    public final String flavorText;
    public final int timerMax;
    public final BufferedImage uiImage;

    /**
     * PowerType constructor to set properties of the enum.
     * 
     * @param frameCount
     *          Number of frames for which this PowerUp should be active.
     * @param flavorText
     *          String to draw on screen when picking up this PowerUp.
     * @param uiImageName
     *          String containing the name in the SpriteMap of the image to show
     *          in the HUD.
     */
    private PowerType(int frameCount, String flavorText, String uiImageName)
    {
      name = toString().toLowerCase().concat("Power");
      this.uiImageName = uiImageName;
      this.timerMax = frameCount;
      this.flavorText = flavorText;
      uiImage = Library.getSprites().get(uiImageName);
    }

    /**
     * Getter for name.
     * 
     * @return Name of this PowerType.
     */
    public String getName()
    {
      return name;
    }

    /**
     * Getter for uiImage name of this PowerType.
     * 
     * @return Name under which this PowerType's UI image is stored in the
     *         sprite library.
     */
    public String getUIImageName()
    {
      return uiImageName;
    }

    /**
     * Getter for the PowerType's flavor text.
     * 
     * @return String that should be displayed on screen when picking up this
     *         PowerUp.
     */
    public String getFlavorText()
    {
      return flavorText;
    }

    /**
     * Getter for timerMax.
     * 
     * @return Number of frames for which this PowerUp should be active.
     */
    public int getTimerMax()
    {
      return timerMax;
    }

    /**
     * Getter for uiImage.
     * 
     * @return Reference to the sprite that should be shown in the UI when the
     *         player is holding this PowerType.
     */
    public BufferedImage getUIImage()
    {
      return uiImage;
    }
  }

}
