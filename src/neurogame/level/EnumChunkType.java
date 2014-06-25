/**
 * @author Marcos Lemus
 * */
package neurogame.level;

import neurogame.gameplay.GameObjectType;
import neurogame.gameplay.PowerUp;
import neurogame.library.Library;

public enum EnumChunkType
{
  FLAT
  { public double getSpeed()  { return .3; }
    public double getMaxChange()  { return 0.1; }
    public double getStepSize()  { return 0.1; }
    public double getDefaultOpeningHeight()  { return 0.8; }
    public PowerUp getPowerUp() { return null; }
    public GameObjectType getEnemyType() { return null; }
  },

  SMOOTH
  { public double getMaxChange() { return 0.01; }
    public double getStepSize()  { return 0.01; }
    public double getDefaultOpeningHeight()  { return 0.3; }
    public PowerUp getPowerUp()  { return null; }
    public GameObjectType getEnemyType()  { return GameObjectType.ENEMY_STRAIGHT; }
  },

  SPIKE
  { public double getMaxChange() { return 0.1; }
    public double getStepSize() { return 0.1; }
    public double getDefaultOpeningHeight() { return 0.4; }
    public PowerUp getPowerUp() { return null; }
    public GameObjectType getEnemyType() {  return GameObjectType.ENEMY_FOLLOW; }
  },

  CURVED
  { public double getMaxChange() { return 0.1; }
    public double getStepSize() { return 0.1; }
    public double getDefaultOpeningHeight() { return 0.4; }
    public PowerUp getPowerUp() { return null; }
    public GameObjectType getEnemyType() { return GameObjectType.ENEMY_SINUSOIDAL; }
  },

  SQUARE
  { public double getMaxChange() { return 0.06; }
    public double getStepSize() { return 0.06; }
    public double getDefaultOpeningHeight() { return 0.4; }
    public PowerUp getPowerUp() { return null; }
    public GameObjectType getEnemyType() { return GameObjectType.ENEMY_STRAIGHT; }
  };

  public static EnumChunkType getRandomType()
  {
    //Never return FLAT
    int r = Library.RANDOM.nextInt(values().length - 2)+1;
    return values()[r];
  }

  public static final double MAX_STEP_SIZE = 0.1;
  public abstract double getMaxChange();
  public abstract double getStepSize();
  public abstract double getDefaultOpeningHeight();
  public abstract PowerUp getPowerUp();
  public abstract GameObjectType getEnemyType();
}