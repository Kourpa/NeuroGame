package neurogame.gameplay;


public enum GameObjectType
{
  PLAYER
  { public String getName() {return "player";}
    public double getWidth() {return 0.055;}
    public double getHeight() {return 0.055;}
    public int getHitDamage() {return 0;}
    public boolean isEnemy() {return false;}
    public boolean isDynamic() {return true;}  
    public double getMaxSpeed() {return 0.5;}
    public boolean hasCollider() {return true;}
  },
  
  ENEMY_STRAIGHT
  { public String getName() {return "EnemyStraight";}
    public double getWidth() {return 0.08;}
    public double getHeight() {return 0.07;}
    public int getHitDamage() {return 20;}
    public boolean isEnemy() {return true;}
    public boolean isDynamic() {return true;}
    public double getMaxSpeed() {return 0.7;}
    public boolean hasCollider() {return true;}
  }, 
  
  ENEMY_FOLLOW
  { public String getName() {return "EnemyFollow";}
    public double getWidth() {return 0.09936;}
    public double getHeight() {return 0.06;}
    public int getHitDamage() {return 15;}
    public boolean isEnemy() {return true;}
    public boolean isDynamic() {return true;}
    public double getMaxSpeed() {return 0.5;}
    public boolean hasCollider() {return true;}
  }, 
  
  ENEMY_SINUSOIDAL
  { public String getName() {return "EnemySinusoidal";}
    public double getWidth() {return 0.09;}
    public double getHeight() {return 0.07;}
    public int getHitDamage() {return 15;}
    public boolean isEnemy() {return true;}
    public boolean isDynamic() {return true;}
    public double getMaxSpeed() {return 0.6;}
    public boolean hasCollider() {return true;}
  },
  
  ZAPPER
  { public String getName() {return "Zapper";}
    public double getWidth() {return 0.09;}
    public double getHeight() {return 0.07;}
    public int getHitDamage() {return 5;} //per second
    public boolean isEnemy() {return true;}
    public boolean isDynamic() {return false;}
    public double getMaxSpeed() {return 0.0;}
    public boolean hasCollider() {return false;}
  },

  STAR
  { public String getName() {return "star";}
    public double getWidth() {return 0.05;}
    public double getHeight() {return 0.05;}
    public int getHitDamage() {return 0;}
    public boolean isEnemy() {return false;}
    public boolean isDynamic() {return false;}
    public double getMaxSpeed() {return 0;}
    public boolean hasCollider() {return true;}
  },

  PARTICLE
  { public String getName() {return "particle";}
    public double getWidth() {return 0.01;}
    public double getHeight() {return 0.01;}
    public int getHitDamage() {return 0;}
    public boolean isEnemy() {return false;}
    public boolean isDynamic() {return true;}
    public double getMaxSpeed() {return 0.5;}
    public boolean hasCollider() {return false;}
  },
  
  
  MISSILE
  { public String getName() {return "missile";}
    public double getWidth() {return 0.03;}
    public double getHeight() {return 0.01;}
    public int getHitDamage() {return 0;}
    public boolean isEnemy() {return false;}
    public boolean isDynamic() {return true;}
    public double getMaxSpeed() {return 1.25;}
    public boolean hasCollider() {return true;}
    },


  
  AMMO
  { public String getName() {return "powerupMissileAmmo";}
    public double getWidth() {return 0.08;}
    public double getHeight() {return 0.08;}
    public int getHitDamage() {return 0;}
    public boolean isEnemy() {return false;}
    public boolean isDynamic() {return false;}
    public double getMaxSpeed() {return 0;}
    public boolean hasCollider() {return true;}
  },
  
  INFO
  { public String getName() {return "info";}
    public double getWidth() {return 0.03;}
    public double getHeight() {return 0.06;}
    public int getHitDamage() {return 0;}
    public boolean isEnemy() {return false;}
    public boolean isDynamic() {return true;}
    public double getMaxSpeed() {return 0.25;}
    public boolean hasCollider() {return false;}
  };
  
  
  public abstract String getName();
  public abstract double getWidth();
  public abstract double getHeight();
  public abstract int getHitDamage();
  public abstract boolean isEnemy();
  public abstract boolean isDynamic();
  public abstract double getMaxSpeed();
  public abstract boolean hasCollider();
}