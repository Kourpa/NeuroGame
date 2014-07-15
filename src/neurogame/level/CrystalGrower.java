package neurogame.level;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

import neurogame.library.Library;
import neurogame.library.QuickSet;

public class CrystalGrower extends Thread
{
  private static boolean DEBUG_CHUNKS = false;
  private static Random rand = new Random();

  public final int bufferPixelWidth;
  public final int bufferPixelHeight;

  private static final int MAX_CRYSTALS = 2000;

  private volatile BufferedImage imageBuffer;
  private Graphics2D canvas;

  private QuickSet<Crystal> crystalList = new QuickSet<Crystal>(MAX_CRYSTALS);

  private static final int TOTAL_ACTIVE_CHUNKS = 2;
  private Chunk[] chunkList = new Chunk[TOTAL_ACTIVE_CHUNKS];
  private volatile int chunkOffsetX; // pixels from start of current left most
                                     // chunk
  private volatile int chunkIdx; // index of left most active chunk.

  public static final int PLAY_AREA_INT = 0xFF000000;
  public static final Color PLAY_AREA_COLOR = new Color(PLAY_AREA_INT);
  public static final int ROCK_AREA_INT = 0xFF202020;
  //public static final int ROCK_AREA_INT = 0xFFCC9966;
  public static final Color ROCK_AREA_COLOR = new Color(ROCK_AREA_INT);
  public static final long TIME_STEP_MILLISEC = 30;

  public static final Color FOREST_GREEN = new Color(34, 139, 34);
  public static final Color PUMPKIN = new Color(255, 117, 24);

  public CrystalGrower(Chunk firstChunk, Chunk secondChunk)
  {

    int maxChunkWidth = (int) (Library.U_VALUE * (Library.getWindowAspect() + 
        Chunk.EXTRA_VERTEX_COUNT_PER_CHUNK*EnumChunkType.MAX_STEP_SIZE));
    
    bufferPixelWidth = maxChunkWidth * 2;
    bufferPixelHeight = (int) (Library.U_VALUE * firstChunk.getHeight());

    imageBuffer = new BufferedImage(bufferPixelWidth, bufferPixelHeight,
        BufferedImage.TYPE_INT_RGB);

    canvas = (Graphics2D) imageBuffer.getGraphics();

    System.out.println("gridX=" + bufferPixelWidth + ", gridY="
        + bufferPixelHeight);
    canvas.setColor(Color.ORANGE);
    canvas.drawRect(0, 0, bufferPixelWidth, bufferPixelHeight);

    chunkOffsetX = 0;
    chunkIdx = 0;

    Crystal.setup(bufferPixelWidth, bufferPixelHeight);

    // draw the walls
    addChunk(firstChunk);
    addChunk(secondChunk);

    chunkOffsetX = 0;
    this.start();
  }

  public void run()
  {
    // System.out.println(this);
    long lastTick = System.currentTimeMillis();
    while (!interrupted())
    { // System.out.println(lastTick );
      long currentTime = System.currentTimeMillis();
      long deltaTime = currentTime - lastTick;
      lastTick = currentTime;
      if (deltaTime < TIME_STEP_MILLISEC)
      {
        try
        {
          // System.out.println("Sleep: "+TIME_STEP_MILLISEC);
          Thread.sleep(TIME_STEP_MILLISEC - deltaTime);
        }
        catch (InterruptedException e)
        {
          return;
        }

        grow();
      }
    }
  }

  public void addChunk(Chunk chunk)
  {

    // System.out.println(chunk.getTopAndBottom()
    synchronized (imageBuffer)
    {
      chunkIdx++;

      int deadChunkWidth = 0;
      if (chunkIdx > 2)
      {
        deadChunkWidth = (int) (Library.U_VALUE * chunkList[0].getWidth());
        canvas.drawImage(imageBuffer, -deadChunkWidth, 0, null);

        for (int i = 0; i < crystalList.size(); i++)
        {
          Crystal crystal = crystalList.get(i);
          crystal.addOffsetX(-deadChunkWidth);
        }
      }

      chunkList[0] = chunkList[1];
      chunkList[1] = chunk;

      double widthWorldChunk0 = 0;
      int pixelStartOfNewChunk = 0;
      if (chunkIdx > 1)
      {
        widthWorldChunk0 = chunkList[0].getWidth();
        pixelStartOfNewChunk = (int) (Library.U_VALUE * widthWorldChunk0);
      }

      Path2D.Double chunkPathTop = chunk.getTop();
      Path2D.Double chunkPathBot = chunk.getBottom();

      // System.out.println("chunk.bounds=" +
      // Library.bounds2DString(chunkPathTop.getBounds2D()) +
      // ", " + Library.bounds2DString(chunkPathBot.getBounds2D()));

      canvas.setColor(PLAY_AREA_COLOR);
      canvas.fillRect(pixelStartOfNewChunk, 0, bufferPixelWidth - pixelStartOfNewChunk,
          bufferPixelHeight);

      AffineTransform orginalTransform = canvas.getTransform();
      canvas.setTransform(AffineTransform.getScaleInstance(Library.U_VALUE,
          Library.U_VALUE));
      canvas.translate(widthWorldChunk0 - chunk.getStartX(), 0);

      canvas.setColor(ROCK_AREA_COLOR);
      //if (chunkIdx % 2 == 0) canvas.setColor(FOREST_GREEN);
      //else canvas.setColor(PUMPKIN);
      canvas.fill(chunkPathTop);
      canvas.fill(chunkPathBot);

      canvas.setTransform(orginalTransform);

      if (!DEBUG_CHUNKS) spawnCrystalsInNewChunk(chunk, pixelStartOfNewChunk);
      chunkOffsetX = 0;
    }
  }

  private void spawnCrystalsInNewChunk(Chunk chunk, int pixelStartOfNewChunk)
  {
    List<PathVertex> vertexList = chunk.getPathList();
    int palett = Crystal.getPalettIdxOfChunkType(chunk.getChunkType());
    
    int first90Percent = (int)(vertexList.size() * 0.8);
    for (int i=0; i<first90Percent; i++)
    {
      PathVertex vertex = vertexList.get(i);
      int x = pixelStartOfNewChunk + (int)(Library.U_VALUE*(vertex.getX() - chunk.getStartX()));

      int y = (int)(Library.U_VALUE*vertex.getTopY()) - Library.RANDOM.nextInt(10);
      if (y < 0) y = 0;
      makeCrystal(x, y, palett);
      
      y = (int)(Library.U_VALUE*vertex.getBottomY()) + Library.RANDOM.nextInt(10);
      if (y >= bufferPixelHeight) y = bufferPixelHeight-1;
      makeCrystal(x, y, palett);
    }
  }

  private void makeCrystal(int x, int y, int palett)
  {
    if (crystalList.size() >= MAX_CRYSTALS)
    {
      System.out
          .println("        CrystalGrower.makeCrystal crystalList.size() >= MAX_CRYSTALS");
      return;
    }

    if (x < chunkOffsetX || x >= bufferPixelWidth) return;
    if (y < 0 || y >= bufferPixelHeight) return;

    // System.out.println("        CrystalGrower.makeCrystal("+x+", "+y+")  Number of Crystals = "+crystalList.size());
    // if (crystalList.size() > 500) System.exit(0);
    synchronized (imageBuffer)
    {
      if (imageBuffer.getRGB(x, y) != ROCK_AREA_INT) return;

      
      Crystal crystal = new Crystal(imageBuffer, x, y, chunkIdx, palett);
      crystalList.add(crystal);
    }
    // canvas.setColor(Color.CYAN);
    // canvas.fillOval(x-5, y-5, 11, 11);
  }

  private void grow()
  {
    synchronized (imageBuffer)
    {
      int i = 0;
      while (i < crystalList.size())
      {
        // System.out.println("grow(): crystalList["+i+"]="+crystalList[i] );
        Crystal crystal = crystalList.get(i);
        if (crystal == null)
        {
          crystalList.remove(i);
        }
        else
        {
          boolean ok = crystal.grow(chunkIdx);
          if (!ok)
          { // System.out.println("Kill Crystal:" );

            crystalList.remove(i);
          }
          else
          {
            if (crystalList.size() < MAX_CRYSTALS)
            {
              if (rand.nextDouble() < 0.01)
              {
                Point p = crystal.getRandomBirthSite(chunkIdx);
                if ((p != null) && (p != Crystal.NONE))
                {
                  makeCrystal(p.x, p.y, crystal.getPalettIdx());
                }
              }
            }
            i++;
          }
        }
      }
    }
  }

  public void render(Graphics2D mainGameCanvas, double scrolledDistanceWithinChunk)
  {
    chunkOffsetX = (int) (scrolledDistanceWithinChunk * Library.U_VALUE);
    synchronized (imageBuffer)
    {
      // System.out.println("chunkOffsetX="+chunkOffsetX);
      mainGameCanvas.drawImage(imageBuffer, -chunkOffsetX, 0, null);
    }
  }

}
