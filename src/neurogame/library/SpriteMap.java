/**
 * NeuroGame.
 * CS 351, Project 3
 * 
 * Team members:
 * Ramon A. Lovato
 * Danny Gomez
 * Marcos Lemus
 */

package neurogame.library;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import neurogame.gameplay.GameObjectType;
import neurogame.level.SpriteParticles;
import neurogame.main.Bandit;
import neurogame.main.NeuroGame;
import neurogame.main.Oddball;

/**
 * An image library for NeuroGame - handles loading and storage of game images.
 * 
 * @author Ramon A. Lovato
 * @team Danny Gomez
 * @team Marcos Lemus
 */
@SuppressWarnings("serial")
public class SpriteMap extends HashMap<String, BufferedImage>
{
  private LinkedList<SpriteMeta> spriteList;
  private NeuroGame frame;

  /**
   * Instantiate a new SpriteMap and load the resources.
   */
  public SpriteMap(NeuroGame frame)
  {
    spriteList = new LinkedList<SpriteMeta>();
    this.frame = frame;
    try
    {
      addResources();
      loadResources();

      SpriteParticles.setSprite("EnemyStraight", get("EnemyStraight"));
      SpriteParticles.setSprite("EnemySinusoidal", get("EnemySinusoidal"));
      SpriteParticles.setSprite("EnemyFollow", get("EnemyFollow"));
      SpriteParticles.setSprite("singleStar", get("singleStar"));
      SpriteParticles.setSprite("powerupMissileAmmo", get("powerupMissileAmmo"));
    }
    catch (IOException ex)
    {
      System.err.println("Error: graphics resources not found.");
    }
  }

  /**
   * Add the image metadata to the list.
   */
  public void addResources() throws IOException
  {

    // Title screen.

    System.out.println("SpriteMap.addResources(): Library.getWindowWidth()="
        + Library.getWindowPixelWidth() + "Library.getWindowHeight()="
        + Library.getWindowPixelHeight());

    add("titleBackground", "/images/title-screen.png",
            Library.getWindowPixelWidth(), Library.getWindowPixelHeight());
    add("tutControls", "/images/tutControls.png",
            Library.getWindowPixelWidth(), Library.getWindowPixelHeight());
    add("tutEnemy", "/images/tutEnemy.png",
            Library.getWindowPixelWidth(), Library.getWindowPixelHeight());
    add("tutHealthMeter", "/images/tutHealthMeter.png",
            Library.getWindowPixelWidth(), Library.getWindowPixelHeight());
    add("tutStar", "/images/tutStar.png",
            Library.getWindowPixelWidth(), Library.getWindowPixelHeight());
    add("tutMissilesLeft", "/images/tutMissilesLeft.png",
            Library.getWindowPixelWidth(), Library.getWindowPixelHeight());
     
    // Game graphics.
    int width = Library.worldUnitToScreen(GameObjectType.PLAYER.getWidth());
    int height = Library.worldUnitToScreen(GameObjectType.PLAYER.getHeight());
    add("player", "/images/playerShip.png", width, height);
    add("pDmg1", "/images/damage1.png", width, height);
    add("pDmg2", "/images/damage2.png", width, height);
    
    // Visual test graphics
    add("OddballPlayerShip","/images/playerShip.png", Oddball.ICON_SIZE, Oddball.ICON_SIZE);
    add("OddballEnemyShip","/images/enemySinusoidal.png", Oddball.ICON_SIZE, Oddball.ICON_SIZE);
    add("WaitOddball","/images/cross.png", Oddball.ICON_SIZE, Oddball.ICON_SIZE);
    add("EnemyStraight", "/images/enemyStraight.png",
        Library.worldUnitToScreen(GameObjectType.ENEMY_STRAIGHT.getWidth()),
        Library.worldUnitToScreen(GameObjectType.ENEMY_STRAIGHT.getHeight()));
    add("EnemySinusoidal", "/images/enemySinusoidal.png",
        Library.worldUnitToScreen(GameObjectType.ENEMY_SINUSOIDAL.getWidth()),
        Library.worldUnitToScreen(GameObjectType.ENEMY_SINUSOIDAL.getHeight()));
    add("EnemyFollow", "/images/enemyFollow.png",
        Library.worldUnitToScreen(GameObjectType.ENEMY_FOLLOW.getWidth()),
        Library.worldUnitToScreen(GameObjectType.ENEMY_FOLLOW.getHeight()));
    //add("star", "/images/star.png", 64, 2560);
    add("star", "/images/star.png", 53, 2560);
    add("singleStar", "/images/singleStar.png", 53, 53);
    add("zapper", "/images/zapper.png", 128, 128);
    // Power-up HUD icons.
    add("powerupMissileAmmo", "/images/powerup-MissileAmmo.png", Library.worldUnitToScreen(GameObjectType.AMMO.getWidth()), 
        Library.worldUnitToScreen(GameObjectType.AMMO.getHeight()));
    add("missile", "/images/missile.png", Library.worldUnitToScreen(GameObjectType.MISSILE.getWidth()), 
        Library.worldUnitToScreen(GameObjectType.MISSILE.getHeight()));
    add("missileIcon", "/images/missileIcon.png", 15 ,33);



    // Sprites for the one armed bandit
    add("slotMachine", "/images/slotMachine.png", 900, 600);
    add("BanditIntro", "/images/banditIntro.png", 900, 600);
    add("BanditStar", "/images/singleStar.png", Bandit.ICON_SIZE, Bandit.ICON_SIZE);
    add("BanditPlayer","/images/playerShip.png", Bandit.ICON_SIZE, Bandit.ICON_SIZE);
    add("BanditEnemyStraight", "/images/enemyStraight.png", Bandit.ICON_SIZE, Bandit.ICON_SIZE);
    add("BanditEnemySinusoidal", "/images/enemySinusoidal.png", Bandit.ICON_SIZE, Bandit.ICON_SIZE);
    add("BanditEnemyFollow", "/images/enemyFollow.png", Bandit.ICON_SIZE, Bandit.ICON_SIZE);
  }

  /**
   * Add a sprite to the collection using the provided metadata.
   * 
   * @param name
   *          The object's name.
   * @param path
   *          The object's file path.
   * @param width
   *          The pixel width to which to scale the object.
   * @param height
   *          The pixel height to which to scale the object.
   */
  public void add(String name, String path, int width, int height)
  {
    spriteList.add(new SpriteMeta(name, path, width, height));
  }

  /**
   * Use the sprite metadata to load and resize the images, and add them to the
   * map.
   * 
   * @throws IOException
   *           if the image resources weren't found.
   */
  public void loadResources()
  {
    System.out.println("SpriteMap.loadResources()");
    for (SpriteMeta s : spriteList)
    {
      BufferedImage original = Library.loadImage(s.path, frame);
      //System.out.println("Loading Sprite: " + s.path);
      BufferedImage resized = new BufferedImage(s.width, s.height, BufferedImage.TYPE_INT_ARGB);
      resized.createGraphics().drawImage(original, 0, 0, s.width, s.height, null);
      put(s.name, resized);
    }
  }

  /**
   * A nested Sprite data class containing the metadata for loading an image.
   */
  public static class SpriteMeta
  {
    public final String name;
    public final String path;
    public final int width;
    public final int height;

    /**
     * Instantiate a new Sprite with the specified metadata.
     * 
     * @param name
     *          The object's name.
     * @param path
     *          The object's file path.
     * @param width
     *          The pixel width to which to scale the object.
     * @param height
     *          The pixel height to which to scale the object.
     */
    public SpriteMeta(String name, String path, int width, int height)
    {
      this.name = name;
      this.path = path;
      this.width = width;
      this.height = height;
    }
  }

}
