/**
 * @author Marcos
 */
package neurogame.level;

import java.util.Random;
import neurogame.library.Library;

/*
 * Data structure used to hold vertex information.
 */
public class Path {

    private final Random random = Library.RANDOM;
    private final double x;
    private double topY;
    private double bottomY;
    private double center;
    private final double ROCK_PADDING = .01;

    /**
     * Default constructor initializes
     * x = 0
     * topY = ROCK_PADDING
     * bottomY = 1 - ROCK_PADDING
     * center = (topY + bottomY) / 2
     */
    public Path(){
        x = 0;
        topY = ROCK_PADDING;
        bottomY = 1 - ROCK_PADDING;
        center = (topY + bottomY) / 2;
    }

    /**
     * Generates vertices given the specified values.
     * @param reference keep the vertices flush between chunks
     * @param pathType type of generation
     */
    public Path(Path reference, EnumPathType pathType){
        double shipPadding = pathType.getShipPadding();
        double stepSize = pathType.getStepSize();
        double maxChange = pathType.getMaxChange();

        x = reference.getX() + stepSize;

        int r = random.nextInt(2);
        if(r == 1){
            topY = reference.topY - random.nextDouble() * maxChange;
            bottomY = reference.bottomY - random.nextDouble() * maxChange;
        }
        else{
            topY = reference.topY + random.nextDouble() * maxChange;
            bottomY = reference.bottomY + random.nextDouble() * maxChange;
        }

        /** checks * */
        double dy = bottomY - topY;
        if(dy < shipPadding){
            bottomY += (shipPadding - dy)/2;
            topY -= (shipPadding - dy)/2;
        }

        if(bottomY > 1 - ROCK_PADDING){
            bottomY += 1 - ROCK_PADDING - bottomY;
        }
        else if(bottomY < shipPadding){
            bottomY += shipPadding - bottomY;
        }

        if(topY < ROCK_PADDING){
            topY += ROCK_PADDING - topY;
        }
        else if(topY > 1 - shipPadding){
            topY += 1 - shipPadding - topY;
        }

        this.center = (bottomY + topY) / 2;
    }

    /**
     * Specify an exact path location
     * @param x
     * @param topY
     * @param bottomY
     * @param pathType
     */
    public Path(double x, double topY, double bottomY, EnumPathType pathType){
        this.x = x;
        this.topY = topY;
        this.bottomY = bottomY;
        center = (topY + bottomY) / 2;
    }

    public double getX(){
        return x;
    }

    public double getTopY(){
        return topY;
    }

    public double getBottomY(){
        return bottomY;
    }

    public double getCenter(){
        return center;
    }

    @Override
    public String toString(){
        return "X:" + x + " TopY:" + topY + " BottomY:" + bottomY;
    }
}
