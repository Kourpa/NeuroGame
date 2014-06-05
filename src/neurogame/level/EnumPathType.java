/**
 * @author Marcos Lemus
 * */
package neurogame.level;

public enum EnumPathType{
    SPIKE{
        public final double speed = .01;
        private final double maxChange = .01;
        private final double stepSize = .01;
        private final double shipPadding = .5;
        
    };
    
    public double speed = .01;
    
    public double getSpeed(){
        return speed;
    }
}