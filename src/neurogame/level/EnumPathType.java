/**
 * @author Marcos Lemus
 * */
package neurogame.level;

import java.awt.Color;
import neurogame.gameplay.Enemy;
import neurogame.gameplay.PowerUp;
import neurogame.library.Library;

public enum EnumPathType{
    SPIKE( .01,  .1,  .1, .6, null, null, Color.ORANGE),
    SMOOTH(.01, .01, .01, .3, null, null, Color.ORANGE),
    CURVED(.01,  .1,  .1, .5, null, null, Color.ORANGE),
    SQUARE(.01,  .1,  .2, .5, null, null, Color.ORANGE),
    FLAT(  .01,  .1,  .1, .8, null, null, Color.ORANGE);

    private final double speed;
    private final double maxChange;
    private final double stepSize;
    private final double shipPadding;
    private final PowerUp powerUp;
    private final Enemy enemy;
    private final Color color;
    
    EnumPathType(double speed, 
                 double maxChange, 
                 double stepSize, 
                 double shipPadding, 
                 Enemy enemy, 
                 PowerUp powerUp, 
                 Color color){
        this.speed = speed;
        this.maxChange = maxChange;
        this.stepSize = stepSize;
        this.shipPadding = shipPadding;
        this.powerUp = powerUp;
        this.enemy = enemy;
        this.color = color;
    }
    
    public static EnumPathType randomPath(){
        int r = Library.RANDOM.nextInt(values().length - 1);
        return values()[r];
    }
    
    public double getSpeed(){return speed;}
    public double getMaxChange(){return maxChange;}
    public double getStepSize(){return stepSize;}
    public double getShipPadding(){return shipPadding;}
    public PowerUp getPowerUp(){return powerUp;}
    public Enemy getEnemy(){return enemy;}
    public Color getColor(){return color;}
}