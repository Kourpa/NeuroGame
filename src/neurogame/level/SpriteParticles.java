package neurogame.level;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Creates a List of pixel objects to avoid going through the image multiple times.
 * @author Marcos
 */
public class SpriteParticles
{
  private static final HashMap<String, ArrayList<Particle>> nameSpriteMap = new HashMap<>();
  private static final HashMap<String, Integer> nameWidthMap = new HashMap<>();

  public static void setSprite(String name, BufferedImage image){
    ArrayList<Particle> pixels = new ArrayList<>();
    
    int c;
    int width = image.getWidth();
    int height = image.getHeight();
    nameWidthMap.put(name, width);
    for(int x = 0; x < width; x++){
      for(int y = 0; y < height; y++){
        c = image.getRGB(x, y);
        if(c != 0){
          pixels.add(new Particle(x, y, new Color(c)));
        }
      }
    }
    
    nameSpriteMap.put(name, pixels);
  }

  /**
   * Makes a new list to return so that nothing gets altered.
   * @param name
   * @return
   */
  public static Particle[] getPixels(String name)
  {
    ArrayList<Particle> pixels = nameSpriteMap.get(name);
    Particle[] newArray = new Particle[pixels.size()];
    int width = nameWidthMap.get(name);
    for(int i = 0; i < newArray.length; i++){
      Particle pp = pixels.get(i);
      newArray[i] = new Particle(width - pp.getX(), pp.getY(), pp.getColor());
    }
    return newArray;
  }
}