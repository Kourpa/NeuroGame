package neurogame.level;

import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import neurogame.gameplay.Coin;
import neurogame.gameplay.GameObject;
import neurogame.gameplay.PowerUp;
import neurogame.gameplay.Zapper;
import neurogame.library.Library;

/**
 *
 * @author Marcos
 */
public final class Chunk {

    public Path2D.Double wall = new Path2D.Double(); // combination of the roof and floor
    public Path2D.Double roof = new Path2D.Double(); // top layer
    public Path2D.Double floor = new Path2D.Double();// bottom layer

    public static final double WIDTH = 3; // the generated width of the world, OK to change
    public static final double HEIGHT = 1;// the generated height of the world, NOT OK to change

    private final List<Point2D.Double> roofPoints = new ArrayList<>(); // roof verticies, used for spawning
    private final List<Point2D.Double> floorPoints = new ArrayList<>();// floor verticies, used for spawning

    private Type chunkType = Type.FLAT; // current chunktype, initialized to flat

    private final Random random = Library.RANDOM; // the  random

    private final double max_change = .4; // maximum amount the verticies can be change by

    private double top_y, bottom_y; // last used top and bottom y cordinate, used for smooth transition through chunks

    private final double ROCK_PADDING = .05f; //amount of padding from the top and bottom
    private final double SHIP_PADDING = 0f; // amount of padding to allow for the player to pass through

    private final double FREQUENCY = WIDTH / 10f; // number of verticies per chunk. making it really small has some weird effects

    /**
     * Generates the first ever chunk.
     * This constructor sets the chunk type to flat,
     * and initializes the top and bottom to the their maximums
     */
    public Chunk(){
        chunkType = Type.FLAT;
        top_y = 0;
        bottom_y = 1;

        //builds chunks
        buildChunk(this, chunkType);
    }

    /**
     * Builds the chunk using the last chunks last top and bottom
     *
     * @param chunk
     */
    public Chunk(Chunk chunk){
        buildChunk(chunk, chunkType);
    }

    /**
     * Builds the chunk given a chunk and a chunk type.
     *
     * @param chunk
     * @param chunkType
     */
    public Chunk(Chunk chunk, Type chunkType){
        this.chunkType = chunkType;
        buildChunk(chunk, chunkType);
    }

    /**
     * Randomly generate the chunk while keeping enough space for the player.
     *
     * @param chunk last generated chunk
     * @param chunkType
     */
    public void buildChunk(Chunk chunk, Type chunkType){
        top_y = chunk.getTop_y(); // the last top value in the chunk 
        bottom_y = chunk.getBottom_y(); // the last bottom value in the chunk
        this.chunkType = chunkType;

        roofPoints.clear(); //remove all points
        floorPoints.clear();// remove all points

        roof.reset();
        floor.reset();

        roof.moveTo(0, 0);
        roof.lineTo(0, top_y);

        roofPoints.add(new Point2D.Double(0, top_y));

        floor.moveTo(0, HEIGHT);
        floor.lineTo(0, bottom_y);

        floorPoints.add(new Point2D.Double(0, top_y));

        switch(chunkType){
            case SPIKED:
                spikeChunk();
                break;
            case SMOOTH:
                smoothChunk();
                break;
            case FLAT:
                flatChunk();
                break;
            case CUBE:
                cubeChunk();
                break;
            case RANDOM:
                randomChunk();
                break;
            case SPLIT:
                splitChunk();
                break;
            default:
                break;

        }

        roof.lineTo(WIDTH, 0);
        floor.lineTo(WIDTH, HEIGHT);

        roof.closePath();
        floor.closePath();

        wall.reset();
        wall.append(roof, false);
        wall.append(floor, false);
    }

    /**
     * Generated a completely flat chunks
     */
    private void flatChunk(){
        double next_top_y = ROCK_PADDING;
        double next_bottom_y = HEIGHT - ROCK_PADDING;
        roof.lineTo(WIDTH, next_top_y);
        floor.lineTo(WIDTH, next_bottom_y);

        roofPoints.add(new Point2D.Double(WIDTH, next_top_y));
        floorPoints.add(new Point2D.Double(WIDTH, next_bottom_y));

        bottom_y = next_bottom_y;
        top_y = next_top_y;
    }

    /**
     * Generates a cube chunk
     * Chooses random y coordinate creating a square
     */
    private void cubeChunk(){
        double next_top_y = 0;
        double next_bottom_y = 0;

        // generate points from the start of the last chunk to the end of this chunk
        // FREQUENCY is the number of points generated per chunk
        for(double i = FREQUENCY; i <= WIDTH; i += FREQUENCY){
            next_top_y = random.nextFloat();
            next_top_y *= max_change / 2;

            if(next_top_y < ROCK_PADDING){
                next_top_y = ROCK_PADDING;
            }
            else if(next_top_y > (HEIGHT - (SHIP_PADDING + ROCK_PADDING))){
                next_top_y -= SHIP_PADDING + ROCK_PADDING;
            }

            // makes a straight line up from the last point and
            // makes a horizontal line to the next point
            roof.lineTo(i - FREQUENCY, next_top_y);
            roof.lineTo(i, next_top_y);

            roofPoints.add(new Point2D.Double(i, next_top_y));

            // Choose the next point within the specified min and max.
            // min = the last point that was used to the top + padding for the ship
            // max = height
            next_bottom_y = next_top_y + SHIP_PADDING + (random.nextFloat() * (HEIGHT - (next_top_y + SHIP_PADDING)));
            next_bottom_y = 1 - (next_bottom_y * max_change / 2);

            if(next_bottom_y > HEIGHT - ROCK_PADDING){
                next_bottom_y = HEIGHT - ROCK_PADDING;
            }

            floor.lineTo(i - FREQUENCY, next_bottom_y);
            floor.lineTo(i, next_bottom_y);

            floorPoints.add(new Point2D.Double(i, next_bottom_y));
        }

        bottom_y = next_bottom_y;
        top_y = next_top_y;
    }

    /**
     * Generates a spiky chunk
     * Generates random points and connect them with a line
     */
    private void spikeChunk(){
        double next_top_y = 0;
        double next_bottom_y = 0;

        for(double i = FREQUENCY; i <= WIDTH; i += FREQUENCY){
            next_top_y = random.nextFloat();
            next_top_y *= max_change;

            if(next_top_y < ROCK_PADDING){
                next_top_y = ROCK_PADDING;
            }
            else if(next_top_y > (HEIGHT - (SHIP_PADDING + ROCK_PADDING))){
                next_top_y -= SHIP_PADDING + ROCK_PADDING;
            }
            roof.lineTo(i, next_top_y);

            roofPoints.add(new Point2D.Double(i, next_top_y));

            top_y = next_top_y;

            next_bottom_y = next_top_y + SHIP_PADDING + (random.nextFloat() * (HEIGHT - (next_top_y + SHIP_PADDING)));
            next_bottom_y = 1 - (next_bottom_y * max_change);

            if(next_bottom_y > HEIGHT - ROCK_PADDING || next_bottom_y - next_top_y < SHIP_PADDING + ROCK_PADDING){
                next_bottom_y = HEIGHT - ROCK_PADDING;
            }
            floor.lineTo(i, next_bottom_y);
            floorPoints.add(new Point2D.Double(i, next_bottom_y));
        }

        bottom_y = next_bottom_y;
        top_y = next_top_y;
    }

    /**
     * Generates smooth chunks
     * Generates random points and connects them with a curved line
     */
    private void smoothChunk(){
        double next_top_y = 0;
        double next_bottom_y = 0;
        int flip;

        for(double i = FREQUENCY; i < WIDTH; i += FREQUENCY){
            flip = random.nextInt(3); // flip is used to determin the concavity of the curve

            next_top_y = random.nextFloat();
            next_top_y *= max_change;

            if(next_top_y < ROCK_PADDING){
                next_top_y = ROCK_PADDING;
            }
            else if(next_top_y > (HEIGHT - (SHIP_PADDING + ROCK_PADDING))){
                next_top_y -= SHIP_PADDING + ROCK_PADDING;
            }
            roof.curveTo(i - 2 * FREQUENCY / 3, next_top_y, i - FREQUENCY / 3, next_top_y + flip == 1 ? -.0001 : .0001, i, next_top_y);
            roofPoints.add(new Point2D.Double(i, next_top_y));
            top_y = next_top_y;

            next_bottom_y = next_top_y + SHIP_PADDING + (random.nextFloat() * (HEIGHT - (next_top_y + SHIP_PADDING)));
            next_bottom_y = 1 - (max_change * next_bottom_y);

            if(next_bottom_y > HEIGHT - ROCK_PADDING){
                next_bottom_y = HEIGHT - ROCK_PADDING;
            }
            flip = random.nextInt(3);

            floor.curveTo(i - 2 * FREQUENCY / 3, next_bottom_y, i - FREQUENCY / 3, next_bottom_y + (flip == 1 ? -.0001 : .0001), i, next_bottom_y);
            floorPoints.add(new Point2D.Double(i, next_bottom_y));
        }

        bottom_y = next_bottom_y;
        top_y = next_top_y;
    }

    /**
     * Generates randomly arranged chunk
     * Generated a randomly chunk using a mix of the three methods above
     */
    private void randomChunk(){
        double next_top_y = 0;
        double next_bottom_y = 0;
        int r;

        for(double i = FREQUENCY; i <= WIDTH; i += FREQUENCY){
            r = random.nextInt(3);

            next_top_y = random.nextFloat();
            next_top_y *= max_change;

            if(next_top_y < ROCK_PADDING){
                next_top_y = ROCK_PADDING;
            }
            else if(next_top_y > (HEIGHT - (SHIP_PADDING + ROCK_PADDING))){
                next_top_y -= SHIP_PADDING + ROCK_PADDING;
            }

            if(r == 0){
                roof.curveTo(i - 2 * FREQUENCY / 3, next_top_y, i - FREQUENCY / 3, next_top_y - .0001, i, next_top_y);
            }
            else if(r == 1){
                roof.lineTo(i, next_top_y);
            }
            else if(r == 2){
                roof.lineTo(i - FREQUENCY, next_top_y);
                roof.lineTo(i, next_top_y);
            }

            roofPoints.add(new Point2D.Double(i, next_top_y));

            top_y = next_top_y;

            next_bottom_y = next_top_y + SHIP_PADDING + (random.nextFloat() * (HEIGHT - (next_top_y + SHIP_PADDING)));
            next_bottom_y = 1 - (next_bottom_y * max_change);

            if(next_bottom_y > HEIGHT - ROCK_PADDING){
                next_bottom_y = HEIGHT - ROCK_PADDING;
            }

            if(r == 0){
                floor.curveTo(i - 2 * FREQUENCY / 3, next_bottom_y, i - FREQUENCY / 3, next_bottom_y + .0001, i, next_bottom_y);
            }
            else if(r == 1){
                floor.lineTo(i, next_bottom_y);
            }
            else if(r == 2){
                floor.lineTo(i - FREQUENCY, next_bottom_y);
                floor.lineTo(i, next_bottom_y);
            }

            floorPoints.add(new Point2D.Double(i, next_bottom_y));
        }

        bottom_y = next_bottom_y;
        top_y = next_top_y;
    }

    /**
     * Generates two chunks to split into different sections
     */
    private void splitChunk(){
        roof.reset();
        floor.reset();

        roof.moveTo(0, -HEIGHT);
        roof.lineTo(-WIDTH, 0);
        roof.lineTo(0, 0);
        roof.lineTo(0, top_y);
        roof.lineTo(WIDTH, -HEIGHT + ROCK_PADDING);
        roof.lineTo(WIDTH, -HEIGHT);
        roof.closePath();

        roof.moveTo(0.2, HEIGHT / 2);
        roof.lineTo(WIDTH, HEIGHT / 2);
        roof.lineTo(WIDTH, HEIGHT + ROCK_PADDING);
        roof.closePath();

        floor.moveTo(0, HEIGHT * 2);
        floor.lineTo(-WIDTH, HEIGHT);
        floor.lineTo(0, HEIGHT);
        floor.lineTo(0, bottom_y);
        floor.lineTo(WIDTH, HEIGHT * 2 - ROCK_PADDING);
        floor.lineTo(WIDTH, HEIGHT * 2);
        floor.closePath();

        floor.moveTo(.2, HEIGHT / 2);
        floor.lineTo(WIDTH, -ROCK_PADDING);
        floor.lineTo(WIDTH, HEIGHT / 2);
        floor.closePath();

        bottom_y = HEIGHT - ROCK_PADDING;
        top_y = ROCK_PADDING;
    }

    /**
     * Apply the transformation to the wall and the points being used for spawning items/enemies.
     *
     * @param transformation
     */
    public void transform(AffineTransform transformation){
        wall.transform(transformation);

        double x = transformation.getTranslateX();
        double y = transformation.getTranslateY();

        Point2D pd;
        for(Point2D p : roofPoints){
            pd = new Point2D.Double(p.getX() + x, p.getY() + y);
            p.setLocation(pd);
        }

        for(Point2D p : floorPoints){
            pd = new Point2D.Double(p.getX() + x, p.getY() + y);
            p.setLocation(pd);
        }
    }

    /**
     * Randomly spawn items and enemies.
     *
     * @param world
     * @param spawn_rate
     *
     * @return
     */
    public List[] spawner(World world, double spawn_rate){
        List<GameObject> objects = new ArrayList<>();
        List<GameObject> zappers = new ArrayList<>();

        double deltaX = world.getDeltaX();
        double changeYfloor = bottom_y;
        double changeYroof = top_y;
        double r;
        boolean powerup;

        for(int i = 0; i < roofPoints.size(); i++){
            powerup = false;
            r = Library.RANDOM.nextDouble();
            if(spawn_rate > r && roofPoints.get(i).y != floorPoints.get(i).y){
                r = Library.RANDOM.nextInt(12);

                double x = roofPoints.get(i).x + deltaX;
                double y = roofPoints.get(i).y;

                if(changeYroof > 0 && changeYroof > y && Math.abs(changeYroof - y) > .2){
                    objects.add(new PowerUp(x, y, world, PowerUp.Direction.NONE));
                    powerup = true;
                }

                if(changeYfloor > 0 && changeYfloor < floorPoints.get(i).y && Math.abs(changeYfloor - floorPoints.get(i).y) > .2){
                    objects.add(new PowerUp(x, floorPoints.get(i).y - PowerUp.DEFAULT_HEIGHT, world, PowerUp.Direction.NONE));
                    powerup = true;
                }

                changeYroof = y;
                changeYfloor = floorPoints.get(i).y;

                if(r == 9 && !powerup){
                    int flip = Library.RANDOM.nextInt(2);
                    if(flip == 0){
                        zappers.add(new Zapper(x, y + SHIP_PADDING / 2, x, floorPoints.get(i).y - Zapper.width, world));
                        y = (y + SHIP_PADDING / 2 + floorPoints.get(i).y - Zapper.width) / 2;
                    }
                    else{
                        zappers.add(new Zapper(x, y, x, floorPoints.get(i).y - SHIP_PADDING / 2, world));
                        y = (y + floorPoints.get(i).y - SHIP_PADDING / 2) / 2;
                    }

                    objects.add(new Coin(x, y, world));
                    objects.add(new Coin(x + Coin.WIDTH, y, world));
                    objects.add(new Coin(x - Coin.WIDTH, y, world));
                    objects.add(new Coin(x, y + Coin.HEIGHT, world));
                    objects.add(new Coin(x, y - Coin.HEIGHT, world));
                }
                else if(r == 10){
                    int flip = Library.RANDOM.nextInt(2);
                    if(flip == 0){
                        zappers.add(new Zapper(x, y + SHIP_PADDING / 2, x, floorPoints.get(i).y - Zapper.width, world));
                    }
                    else{
                        zappers.add(new Zapper(x, y, x, floorPoints.get(i).y - SHIP_PADDING / 2, world));
                    }
                }
                else if(r > 10){
                    y = (y + floorPoints.get(i).y) / 2;
                    objects.add(new Coin(x, y, world));
                    objects.add(new Coin(x + Coin.WIDTH, y, world));
                    objects.add(new Coin(x - Coin.WIDTH, y, world));
                    objects.add(new Coin(x, y + Coin.HEIGHT, world));
                    objects.add(new Coin(x, y - Coin.HEIGHT, world));
                }
            }
        }

        return new List[]{objects, zappers};
    }

    /**
     * Copy the information from this chunk into the given chunk.
     *
     * @param chunk
     */
    public void cloneInto(Chunk chunk){
        chunk.wall = (Path2D.Double) wall.clone();
        chunk.setTop_y(top_y);
        chunk.setBottom_y(bottom_y);
    }

    public List<Point2D.Double> getRoofList(){
        return roofPoints;
    }

    public List<Point2D.Double> getFloorList(){
        return roofPoints;
    }

    public double getTop_y(){
        return top_y;
    }

    public double getBottom_y(){
        return bottom_y;
    }

    public void setTop_y(double top_y){
        this.top_y = top_y;
    }

    public void setBottom_y(double bottom_y){
        this.bottom_y = bottom_y;
    }

    public Type getChunkType(){
        return chunkType;
    }

    public enum Type {

        SMOOTH, SPIKED, CUBE, RANDOM, FLAT, SPLIT;

        public Type randomChunk(){
            int r = Library.RANDOM.nextInt(Type.values().length - 3);
            return Type.values()[r];
        }
    }
}
