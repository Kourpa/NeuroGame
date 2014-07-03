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
  private static final Map<String, ArrayList<Pixel>> nameSpriteMap = new HashMap<>();

  public static void setSprite(String name, BufferedImage image){
    ArrayList<Pixel> pixels = new ArrayList<>();
    
    int c;
    for(int x = 0; x < image.getWidth(); x++){
      for(int y = 0; y < image.getHeight(); y++){
        c = image.getRGB(x, y);
        if(c != 0){// && x % 2 == 0 && y % 2 == 0){
          pixels.add(new Pixel(x, y, new Color(c)));
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
  public static Pixel[] getPixels(String name)
  {
    ArrayList<Pixel> pixels = nameSpriteMap.get(name);
    Pixel[] newList = new Pixel[pixels.size()];
    for(int i = 0; i < newList.length; i++){
      Pixel pp = pixels.get(i);
      newList[i] = new Pixel(pp.getX(), pp.getY(), pp.getColor());
    }
    return newList;
  }
}