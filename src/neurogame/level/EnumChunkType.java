/**
 * @author Marcos Lemus
 * */
package neurogame.level;

import neurogame.gameplay.Ammo;
import neurogame.gameplay.GameObjectType;
import neurogame.library.Library;

public enum EnumChunkType
{
  
  FLAT
  { public double getMaxChange()  { return 0.1; }
    public double getStepSize()  { return 0.1; }
    public double getDefaultOpeningHeight()  { return 0.8; }
    public double getMinimumOpeningHeight()  { return 0.8; }
    public Ammo getPowerUp() { return null; }
    public GameObjectType getEnemyType() { return null; }
  },

  SMOOTH
  { public double getMaxChange() { return 0.01; }
    public double getStepSize()  { return 0.01; }
    public double getDefaultOpeningHeight()  { return 0.8; }
    public double getMinimumOpeningHeight()  { return 0.2; }
    public Ammo getPowerUp()  { return null; }
    public GameObjectType getEnemyType()  { return GameObjectType.ENEMY_STRAIGHT; }
  },

  SPIKE
  { public double getMaxChange() { return 0.1; }
    public double getStepSize() { return 0.1; }
    public double getDefaultOpeningHeight() { return 0.8; }
    public double getMinimumOpeningHeight()  { return 0.2; }
    public Ammo getPowerUp() { return null; }
    public GameObjectType getEnemyType() {  return GameObjectType.ENEMY_FOLLOW; }
  },

  CURVED
  { public double getMaxChange() { return 0.05; }
    public double getStepSize() { return 0.1; }
    public double getDefaultOpeningHeight() { return 0.8; }
    public double getMinimumOpeningHeight()  { return 0.2; }
    public Ammo getPowerUp() { return null; }
    public GameObjectType getEnemyType() { return GameObjectType.ENEMY_SINUSOIDAL; }
  },

  SQUARE
  { public double getMaxChange() { return 0.06; }
    public double getStepSize() { return GameObjectType.ZAPPER.getWidth(); }
    public double getDefaultOpeningHeight() { return 0.8; }
    public double getMinimumOpeningHeight()  { return 0.20; }
    public Ammo getPowerUp() { return null; }
    public GameObjectType getEnemyType() { return GameObjectType.ZAPPER; }
  };

  public static EnumChunkType getRandomType()
  {
    //Never return FLAT and Never SQUARE
    int r = Library.RANDOM.nextInt(values().length - 2)+1;
    return values()[r];
    //return values()[values().length-1];
  }
  
  public static final int SIZE = values().length;
  public static final double MAX_STEP_SIZE = 0.1;
  
  public abstract double getMaxChange();
  public abstract double getStepSize();
  public abstract double getDefaultOpeningHeight();
  public abstract double getMinimumOpeningHeight();
  public abstract Ammo getPowerUp();
  public abstract GameObjectType getEnemyType();
}