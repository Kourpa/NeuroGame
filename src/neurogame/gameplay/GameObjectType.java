package neurogame.gameplay;


public enum GameObjectType
{
  PLAYER
  { public String getName() {return "player";}
    public double getWidth() {return 0.075;}
    public double getHeight() {return 0.075;}
    public int getHitDamage() {return 0;}
    public boolean isEnemy() {return false;}
    public boolean isDynamic() {return true;}  
    public double getMaxSpeed() {return 0.5;}
  },

  COIN
  { public String getName() {return "coin";}
    public double getWidth() {return 0.05;}
    public double getHeight() {return 0.05;}
    public int getHitDamage() {return 0;}
    public boolean isEnemy() {return false;}
    public boolean isDynamic() {return false;}
    public double getMaxSpeed() {return 0;}
  },

  PARTICLE
  { public String getName() {return "particle";}
    public double getWidth() {return 0.01;}
    public double getHeight() {return 0.01;}
    public int getHitDamage() {return 0;}
    public boolean isEnemy() {return false;}
    public boolean isDynamic() {return true;}
    public double getMaxSpeed() {return 0.5;}
  },
  
  
  MISSILE
  { public String getName() {return "missile";}
    public double getWidth() {return 0.03;}
    public double getHeight() {return 0.01;}
    public int getHitDamage() {return 0;}
    public boolean isEnemy() {return false;}
    public boolean isDynamic() {return true;}
    public double getMaxSpeed() {return 1.25;}
    },

  ENEMY_STRAIGHT
  { public String getName() {return "EnemyStraight";}
    public double getWidth() {return 0.05;}
    public double getHeight() {return 0.05;}
    public int getHitDamage() {return 10;}
    public boolean isEnemy() {return true;}
    public boolean isDynamic() {return true;}
    public double getMaxSpeed() {return 0.7;}
  }, 
  
  ENEMY_FOLLOW
  { public String getName() {return "EnemyFollow";}
    public double getWidth() {return 0.05;}
    public double getHeight() {return 0.05;}
    public int getHitDamage() {return 15;}
    public boolean isEnemy() {return true;}
    public boolean isDynamic() {return true;}
    public double getMaxSpeed() {return 0.5;}
  }, 
  
  ENEMY_SINUSOIDAL
  { public String getName() {return "EnemySinusoidal";}
    public double getWidth() {return 0.05;}
    public double getHeight() {return 0.05;}
    public int getHitDamage() {return 10;}
    public boolean isEnemy() {return true;}
    public boolean isDynamic() {return true;}
    public double getMaxSpeed() {return 0.6;}
  };
  
  public abstract String getName();
  public abstract double getWidth();
  public abstract double getHeight();
  public abstract int getHitDamage();
  public abstract boolean isEnemy();
  public abstract boolean isDynamic();
  public abstract double getMaxSpeed();
}