package neurogame.level;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author kourpa
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
        if(c != 0 && x % 2 == 0 && y % 2 == 0){
          pixels.add(new Pixel(x, y, new Color(c)));
        }
      }
    }
    
    nameSpriteMap.put(name, pixels);
  }
  
  public static ArrayList<Pixel> getPixels(String name)
  { ArrayList<Pixel> newList = new ArrayList<>();
    Pixel p;
    for(Pixel pp: nameSpriteMap.get(name)){
      p = new Pixel(pp.getX(), pp.getY(), pp.getColor());
      newList.add(p);
    }
    return newList;
  }
}