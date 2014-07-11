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
import neurogame.main.NeuroFrame;

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
  private NeuroFrame frame;

  /**
   * Instantiate a new SpriteMap and load the resources.
   */
  public SpriteMap(NeuroFrame frame)
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
    
    add("profileBackground", "/images/profile-screen.png",
            Library.getWindowPixelWidth(), Library.getWindowPixelHeight());
    
    add("highscoreBackground", "/images/highscore-screen.png",
            Library.getWindowPixelWidth(), Library.getWindowPixelHeight());
    
    add("startButtonPlain", "/images/start-button.png", 300, 50);
    add("startButtonSelected", "/images/start-selected.png", 300, 50);
    
    add("exitButtonPlain", "/images/exit-button.png", 300, 50);
    add("exitButtonSelected", "/images/exit-selected.png", 300, 50);
    
    add("configButtonPlain", "/images/option-button.png", 300, 50);
    add("configButtonSelected", "/images/option-selected.png", 300, 50);
    
    add("rewindButtonPlain", "/images/rewind-button.png", 300, 50);
    add("rewindButtonSelected", "/images/rewind-selected.png", 300, 50);
    
    add("backButtonPlain", "/images/back-button.png", 300, 50);
    add("backButtonSelected", "/images/back-selected.png", 300, 50);
    
    add("mainMenuButtonPlain", "/images/mainMenu-button.png", 300, 50);
    add("mainMenuButtonSelected", "/images/mainMenu-selected.png", 300, 50);
    
    add("restartButtonPlain", "/images/restart-button.png", 300, 50);
    add("restartButtonSelected", "/images/restart-selected.png", 300, 50);
    
    add("newUserButtonPlain", "/images/newUser-button.png", 200, 40);
    add("newUserButtonSelected", "/images/newUser-selected.png", 200, 40);
    
    // Game graphics.
    add("player", "/images/playerShip.png", 68, 68);
    add("pDmg1", "/images/damage1.png", 68, 68);
    add("pDmg2", "/images/damage2.png", 68, 68);

    add("EnemyStraight", "/images/enemyStraight.png",
        Library.worldUnitToScreen(GameObjectType.ENEMY_STRAIGHT.getWidth()),
        Library.worldUnitToScreen(GameObjectType.ENEMY_STRAIGHT.getHeight()));
    add("EnemySinusoidal", "/images/enemySinusoidal.png",
        Library.worldUnitToScreen(GameObjectType.ENEMY_SINUSOIDAL.getWidth()),
        Library.worldUnitToScreen(GameObjectType.ENEMY_SINUSOIDAL.getHeight()));
    add("EnemyFollow", "/images/enemyFollow.png",
        Library.worldUnitToScreen(GameObjectType.ENEMY_FOLLOW.getWidth()),
        Library.worldUnitToScreen(GameObjectType.ENEMY_FOLLOW.getHeight()));
    add("coin", "/images/coin.png", 64, 2560);
    add("zapper", "/images/zapper.png", 48, 48);
    // Power-up HUD icons.
    add("powerupMissileAmmo", "/images/powerup-MissileAmmo.png", 96, 96);
    add("missile", "/images/missile.png", Library.worldUnitToScreen(GameObjectType.MISSILE.getWidth()), 
        Library.worldUnitToScreen(GameObjectType.MISSILE.getHeight()));
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
    for (SpriteMeta s : spriteList)
    {
      BufferedImage original = Library.loadImage(s.path, frame);
      System.out.println("Loading Sprite: " + s.path);
      BufferedImage resized = new BufferedImage(s.width, s.height,
          BufferedImage.TYPE_INT_ARGB);
      resized.createGraphics().drawImage(original, 0, 0, s.width, s.height,
          null);
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
