package neurogame.level;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import neurogame.library.Library;
import neurogame.library.QuickSet;

public class CrystalGrower //implements ActionListener
{

    private static Random rand = new Random();

    public int chunkWidth = (int) (Library.U_VALUE * Chunk.WIDTH);
    public int gridX = chunkWidth * 2;
    public int gridY = (int) (Library.U_VALUE * Chunk.HEIGHT) * 3;

    private static final int MAX_CRYSTALS = 2000;

    private BufferedImage imageBuffer;
    private Graphics2D canvas;

    private QuickSet<Crystal> crystalList = new QuickSet<Crystal>(MAX_CRYSTALS);

    private static final int TOTAL_ACTIVE_CHUNKS = 2;
    private Path2D.Double[] chunkPath = new Path2D.Double[TOTAL_ACTIVE_CHUNKS];
    private int chunkOffsetX; //pixels from start of current left most chunk
    private int chunkIdx; //index of left most active chunk.
    private double deltaX; //from beginning of first frame in world coordinates
    private double deltaY; //0 = top of first screen. Negative is above top of first screen.
    private int frameCount;

    public static final int PLAY_AREA_INT = 0xFF000000;
    public static final Color PLAY_AREA_COLOR = new Color(PLAY_AREA_INT);
    public static final int ROCK_AREA_INT = 0xFF202020;
    public static Color ROCK_AREA_COLOR = new Color(ROCK_AREA_INT);

  //private static final Color[] debugChunkColor = {Color.GREEN, Color.BLUE, Color.CYAN, Color.MAGENTA, Color.ORANGE};
    //private int debugChunkColorIdx = 0;
    public CrystalGrower(Path2D.Double firstChunk, Path2D.Double secondChunk) {
        imageBuffer = new BufferedImage(gridX, gridY, BufferedImage.TYPE_INT_RGB);

        canvas = (Graphics2D) imageBuffer.getGraphics();

        chunkOffsetX = 0;
        chunkIdx = 0;
        deltaX = 0;
        frameCount = 0;

        Crystal.setup(gridX, gridY, chunkWidth);

        // draw the walls
        addChunk(firstChunk);
        addChunk(secondChunk);

        chunkOffsetX = 0;
    }

    public void addChunk(Path2D.Double chunk) {

        Path2D.Double chunkCopy = (Path2D.Double) chunk.clone();
        chunkCopy.transform(AffineTransform.getTranslateInstance(0, 1.0));

    //ROCK_AREA_COLOR = debugChunkColor[debugChunkColorIdx];
        //debugChunkColorIdx = (debugChunkColorIdx + 1) % debugChunkColor.length;
        if (chunkPath[0] == null) {
            chunkIdx = 2;
            chunkPath[0] = chunkCopy;
            chunkPath[0].transform(AffineTransform.getScaleInstance(Library.U_VALUE, Library.U_VALUE));
            canvas.setColor(ROCK_AREA_COLOR);
            canvas.fill(chunkPath[0]);
            spawnCrystalsInNewChunk(chunkPath[0]);
        }
        else if (chunkPath[1] == null) {
            chunkPath[1] = chunkCopy;
            chunkPath[1].transform(AffineTransform.getScaleInstance(Library.U_VALUE, Library.U_VALUE));
            canvas.setColor(ROCK_AREA_COLOR);
            canvas.fill(chunkPath[1]);
            spawnCrystalsInNewChunk(chunkPath[1]);
        }
        else {
            chunkIdx++;
            chunkPath[0] = chunkPath[1];
      //chunkPath[0].transform(AffineTransform.getTranslateInstance(-Chunk.WIDTH, 0));
            //Shift old second chunk to first chunk

            canvas.drawImage(imageBuffer, -chunkWidth, 0, null);
            for (int i = 0; i < crystalList.size(); i++) {
                Crystal crystal = crystalList.get(i);
                crystal.addOffsetX(-chunkWidth);
            }

            canvas.setColor(PLAY_AREA_COLOR);
            canvas.fillRect(chunkWidth, 0, chunkWidth, gridY);

            chunkPath[1] = chunkCopy;
            chunkPath[1].transform(AffineTransform.getScaleInstance(Library.U_VALUE, Library.U_VALUE));
            canvas.setColor(ROCK_AREA_COLOR);
            canvas.fill(chunkPath[1]);

            spawnCrystalsInNewChunk(chunkPath[1]);
        }
        chunkOffsetX = 0;
    }

    private void spawnCrystalsInNewChunk(Path2D.Double chunk) {
        Rectangle bounds = chunk.getBounds();
    //System.out.println("CrystalGrower.spawnCrystalsInNewChunk("+chunkIdx+"): "+bounds);

        int x = bounds.x + 3 + rand.nextInt(5);
        int maxX = x + bounds.width - 50;

        while (x < maxX) { //System.out.println("     x="+x);
            int y = bounds.y + 3;
            int maxY = y + bounds.height - 4;

            boolean plotNextIn = false;
            int lastYIn = -1;
            while (y < maxY) { //System.out.println("chunk.contains("+x+", y="+y+")="+chunk.contains(x,y));
                if (chunk.contains(x, y)) {
                    if (plotNextIn) {
                        makeCrystal(x, y);
                        plotNextIn = false;
                    }
                    lastYIn = y;
                }
                else {
                    if (!plotNextIn && (lastYIn >= 0)) {
                        makeCrystal(x, lastYIn);
                        plotNextIn = true;
                        lastYIn = -1;
                    }
                }

                y += 10 + rand.nextInt(10);
            }
            x += rand.nextInt(chunkWidth / 15) + 10;
        }
    }

    private void makeCrystal(int x, int y) {
        if (crystalList.size() >= MAX_CRYSTALS) {
            System.out.println("        CrystalGrower.makeCrystal crystalList.size() >= MAX_CRYSTALS");
            return;
        }

        if (x < chunkOffsetX || x >= gridX) {
            return;
        }
        if (y < 0 || y >= gridY) {
            return;
        }

    //System.out.println("        CrystalGrower.makeCrystal("+x+", "+y+")  Number of Crystals = "+crystalList.size());
        //if (crystalList.size() > 500) System.exit(0);
        if (imageBuffer.getRGB(x, y) != ROCK_AREA_INT) {
            return;
        }

        Crystal crystal = new Crystal(imageBuffer, x, y, chunkIdx);
        crystalList.add(crystal);
    //canvas.setColor(Color.CYAN);
        //canvas.fillOval(x-5, y-5, 11, 11);
    }

    public void update(double deltaX, double deltaY, double scrolled_distance) {
        frameCount++;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        chunkOffsetX = (int) (scrolled_distance * Library.U_VALUE);
        grow();
    }

    private void grow() {
        int i = 0;
        while (i < crystalList.size()) {
            //System.out.println("grow(): crystalList["+i+"]="+crystalList[i] );
            Crystal crystal = crystalList.get(i);
            if (crystal == null) {
                crystalList.remove(i);
            }
            else {
                boolean ok = crystal.grow(chunkIdx);
                if (!ok) { //System.out.println("Kill Crystal:" );
                    crystalList.remove(i);
                }
                else {
                    if (crystalList.size() < MAX_CRYSTALS) {
                        if (rand.nextDouble() < 0.01) {
                            Point p = crystal.getRandomBirthSite(chunkIdx);
                            if ((p != null) && (p != Crystal.NONE)) {
                                makeCrystal(p.x, p.y);
                            }
                        }
                    }
                    i++;
                }
            }
        }
    }

    public void draw(Graphics2D g) { //System.out.println(deltaY);
        int chunkOffsetY = Library.worldToScreen(deltaY + 1.0);
        
        System.out.println(chunkOffsetY);
        
        g.drawImage(imageBuffer, -chunkOffsetX, -chunkOffsetY, null);
    }

}
