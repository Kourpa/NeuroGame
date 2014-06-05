/**
 *
 * @author Marcos
 */
package neurogame.level;

import java.util.Random;
import neurogame.library.Library;

public class Path {

    private Random random = Library.RANDOM;
    private final double x;
    private double topY;
    private double bottomY;
    private double center;

    private final double MAX_CHANGE = .01;
    public static final double STEP_SIZE = .01;

    private final double ROCK_PADDING = .01;
    private double shipPadding = .2;

    private static boolean centered = false;

    public Path(){
        x = 0;
        topY = ROCK_PADDING;
        bottomY = 1 - ROCK_PADDING;
        center = (topY + bottomY) / 2;
    }

    public Path(Path p, double center){
        x = p.getX() + STEP_SIZE;

        if(Math.abs(p.center - center) > shipPadding){
            if(p.center > center){
                topY = p.topY - random.nextDouble() * MAX_CHANGE;
                bottomY = p.topY + shipPadding - random.nextDouble() * MAX_CHANGE;
            }
            else{
                topY = p.topY + random.nextDouble() * MAX_CHANGE;
                bottomY = p.topY + shipPadding + random.nextDouble() * MAX_CHANGE;
            }
        }
        else{
            centered = true;
            int r = random.nextInt(2);
            
            if(r == 1){
                topY = p.topY - random.nextDouble() * MAX_CHANGE;
                bottomY = p.topY + shipPadding - random.nextDouble() * MAX_CHANGE;
            }
            else{
                topY = p.topY + random.nextDouble() * MAX_CHANGE;
                bottomY = p.topY + shipPadding + random.nextDouble() * MAX_CHANGE;
            }
        }

        double dy = bottomY - topY;
        if(dy < shipPadding){
            bottomY += shipPadding - dy;
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

    public Path(double x, double topY, double bottomY){
        this.x = x;
        this.topY = topY;
        this.bottomY = bottomY;
        center = (topY + bottomY) / 2;
    }

    public void setPathType(EnumPathType pt){
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

    public static boolean isCentered(){
        boolean c = centered;
        centered = false;
        return c;
    }

    @Override
    public String toString(){
        return "X:" + x + " TopY:" + topY + " BottomY:" + bottomY;
    }
}
