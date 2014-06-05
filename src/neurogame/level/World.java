/**
 * World generates and updates randomly generated path.
 * @author Marcos
 */
package neurogame.level;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

import neurogame.gameplay.Player;
import neurogame.library.Library;
import neurogame.main.Drawable;

public class World implements Drawable {

    private final List<Path> pathList;

    private final Path2D.Double top;
    private final Path2D.Double bottom;

    private int PATH_LENGTH = 100;
    private double center = .0;

    private final boolean USE_CRYSTALS = false; // toggle fractals on or off, also toggles splits
    private final Player player;

    private final Color grey = new Color(25, 25, 25); // Colors!

    //Easy speed controls
    public static final double SLOW = 0.001;
    public static final double MED = 0.01;
    public static final double FAST = 0.1;

    private final Area walls = new Area(); // The wrong way to do walls

    private double deltaX = 0; //total horizontal change
    private double scrolled_distance = 0; // used to determine when to generate new chunks
    private double scrollSpeed = MED; // current speed

    private CrystalGrower crystalWalls; // fractals!

    Chunks chunks = new Chunks();

    /**
     * Initializes firstChunk, secondChunk, player and the outer walls.
     * and the fractal.
     */
    public World(){
        player = new Player(0.1, 1 / 2, 0.075, 0.075, this);

        pathList = new ArrayList<>();
        pathList.add(new Path());

        top = new Path2D.Double();
        bottom = new Path2D.Double();

        top.moveTo(0, 0);
        bottom.moveTo(0, 1);

        Path p;
        for(int i = 0; i < PATH_LENGTH; i++){
            p = new Path(pathList.get(pathList.size() - 1), .5);
            pathList.add(p);

            top.lineTo(p.getX(), p.getTopY());
            bottom.lineTo(p.getX(), p.getBottomY());
        }

        top.lineTo(PATH_LENGTH * Path.STEP_SIZE, 0);
        bottom.lineTo(PATH_LENGTH * Path.STEP_SIZE, 1);
    }

    /**
     * Moves the chunk to the left depending on the scrollSpeed
     * If the first chunk reaches its end it is replaced with the second chunk
     * and the second chunk is re-generated.
     */
    public void update(){
        //add the scrollSpeed to the distance
        deltaX += scrollSpeed;
        scrolled_distance += scrollSpeed;

        //Check to see if I should spawn another chunk
        if(deltaX >= Path.STEP_SIZE){
            deltaX = 0;

            pathList.remove(0);
            pathList.add(new Path(pathList.get(pathList.size() - 1), center));
            

            top.reset();
            bottom.reset();

            top.moveTo(0, 0);
            bottom.moveTo(0, 1);

            double firstX = pathList.get(0).getX();

            pathList.forEach(p -> {
                top.lineTo(p.getX() - firstX, p.getTopY());
                bottom.lineTo(p.getX() - firstX, p.getBottomY());
            });

            top.lineTo(PATH_LENGTH * Path.STEP_SIZE, 0);
            bottom.lineTo(PATH_LENGTH * Path.STEP_SIZE, 1);

            top.closePath();
            bottom.closePath();

            walls.reset();
            walls.add(new Area(top));
            walls.add(new Area(bottom));
        }

        //update crystals
        if(USE_CRYSTALS){
            crystalWalls.update(deltaX, 0, scrolled_distance);
        }
    }

    /**
     * draw the world
     * @param g
     */
    @Override
    public void draw(Graphics2D g){

        AffineTransform oldTransform = g.getTransform();
        g.setTransform(AffineTransform.getScaleInstance(Library.U_VALUE, Library.U_VALUE));
        g.translate(-deltaX, 0);

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 2, 1);

        g.setColor(grey);
        g.fill(top);
        g.fill(bottom);

        g.setTransform(oldTransform);

        if(Path.isCentered()){
            center = .8 - Library.RANDOM.nextDouble() * .6;
        }
//        if (USE_CRYSTALS) {
//            crystalWalls.draw(g);
//        }
//        else {
//            AffineTransform oldTransform = g.getTransform();
//            g.setTransform(AffineTransform.getScaleInstance(Library.U_VALUE, Library.U_VALUE));
//
//            g.setColor(grey);
//            g.fill(walls);
//            g.setTransform(oldTransform);
//        }
    }

    //geters for the worldObjects
    public Player getPlayer(){
        return player;
    }

    //Getters for the world information
    public Area getCollisionArea(){
        return walls;
    }

    public double getWidth(){
        return 2;
    }

    public double getHeight(){
        return 1;
    }

    public double getDeltaX(){
        return deltaX;
    }

    public void setSpeed(double scrollSpeed){
        this.scrollSpeed = scrollSpeed;
    }

}
