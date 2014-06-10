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
import neurogame.gameplay.Coin;
import neurogame.gameplay.GameObject;

import neurogame.gameplay.Player;
import neurogame.library.Library;

public class World {

    private final Player player;
    private Chunk[] chunks;
    private final Area walls = new Area(); // The wrong way to do walls
    private List<GameObject> gameObjects;

    private int leadingChunkIndex = 0;
    private int chunkSize;
    private double windowWidth = Library.screenToWorld(Library.getWindowWidth());
    private EnumPathType pathType = EnumPathType.CURVED;
    private double distance;
    private double deltaX = 0; //total horizontal change
    private double chunkScolledDistance = 0; // used to determine when to generate new chunks

    private final Color grey = new Color(25, 25, 25); // Colors!
    private final Color[] colors = new Color[]{Color.CYAN, Color.MAGENTA};

//    private CrystalGrower crystalWalls; // fractals!
    private final boolean USE_CRYSTALS = false; // toggle fractals on or off, also toggles splits

    /**
     * Initializes firstChunk, secondChunk, player and the outer walls.
     * and the fractal.
     */
    public World(){
        chunkSize = 2+ (int) (windowWidth / pathType.getStepSize());
        player = new Player(0.1, 1 / 2.0, 0.075, 0.075, this);
        gameObjects = new ArrayList<>();
        
        chunks = new Chunk[2];
        chunks[0] = new Chunk(chunkSize, pathType);
        chunks[1] = new Chunk(chunks[0].getReference(), chunkSize, pathType);
        distance = 0;
    }

    /**
     * updates the chunks regenerating if the width is reached
     * @param graphics
     * @param deltaTime
     * @return 
     */
    public double update(Graphics2D graphics, long deltaTime){
        distance = deltaTime * pathType.getSpeed();
        
        chunkSize = 2+(int) (windowWidth / pathType.getStepSize());

        /** add the scrollSpeed to the distance* */
        chunkScolledDistance += distance;

        /** if the leading chunk has left the screen re-randomize it.* */
        if(chunkScolledDistance >= chunkSize* pathType.getStepSize()){
            chunkScolledDistance = 0;

            if(leadingChunkIndex == 0){
                chunks[0].randomize(chunks[1].getReference(), chunkSize);
                leadingChunkIndex = 1;
            }
            else{
                chunks[1].randomize(chunks[0].getReference(), chunkSize);
                leadingChunkIndex = 0;
            }
            
            deltaX = chunks[leadingChunkIndex].getStartX();
            
            randomPathType();
        }
        else{
//            deltaX += distance;
        }

        //update crystals
        if(USE_CRYSTALS){
            //crystalWalls.update(deltaX, 0, scrolled_distance);
        }

        spawner();
        draw(graphics);
        return 0;
    }

    /**
     * Spawns the variety of GameObjects into the world.
     */
    private void spawner(){
        gameObjects.add(new Coin(deltaX + windowWidth + chunkScolledDistance, .5, this));
    }
    
    /**
     * draws the world
     * @param g
     */
    public void draw(Graphics2D g){
        AffineTransform oldTransform = g.getTransform();
        g.setTransform(AffineTransform.getScaleInstance(Library.U_VALUE, Library.U_VALUE));
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 2, 1);

        g.translate(-(deltaX+chunkScolledDistance), 0);

        g.setColor(grey);

        //Draw each segment of the chunks.
//        int i = 0;
        for(Chunk c : chunks){
//            g.setColor(colors[i++]);
            for(Path2D.Double area : c.getTopAndBottom()){
                g.fill(area);
            }
        }

        g.setTransform(oldTransform);

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

    private void randomPathType(){
        int r = Library.RANDOM.nextInt(10);
        if(r == 0){
            pathType = EnumPathType.randomPath();
            chunks[0].setPathType(pathType);
            chunks[1].setPathType(pathType);
            System.out.println(pathType);
        }
    }

    //geters for the worldObjects
    public Player getPlayer(){
        return player;
    }

    public List<GameObject> getObjectList(){
        return gameObjects;
    }
    
    //Getters for the world information
    public Area getCollisionArea(){
        return walls;
    }

    public double getWidth(){
        return chunkSize * pathType.getStepSize();
    }

    public double getHeight(){
        return 1;
    }

    public double getDeltaX(){
        return 0;//deltaX + chunkScolledDistance;
    }

}
