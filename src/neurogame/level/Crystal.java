package neurogame.level;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Random;

import neurogame.library.QuickSet;

public class Crystal
{
  public static final int DIR_NONE = -1;
  public static final int DIR_NORTH = 0;
  public static final int DIR_EAST = 1;
  public static final int DIR_SOUTH = 2;
  public static final int DIR_WEST = 3;

  public static final int[] DIR_LIST_CARDINAL =
  { DIR_NORTH, DIR_EAST, DIR_SOUTH, DIR_WEST };

  public static final int[] DIR_DELTA_X =
  { 0, 1, 0, -1, 1, 1, -1, -1 };

  public static final int[] DIR_DELTA_Y =
  { -1, 0, 1, 0, -1, 1, 1, -1 };

  public static final int PALETT_BASKET_OF_APPLES = 0;
  public static final int PALETT_SAINT_VICTOIRE_BARNES = 1;
  public static final int PALETT_ANCIENT_OF_DAYS = 2;

  public static final Point NONE = new Point();

  private static int gridX;
  private static int gridY;
  private static int gridRightMostGrowX;

  private static final int[][][] PALETT =
  {
  {
  { 96, 48, 0 },
  { 192, 192, 144 },
  { 120, 144, 120 },
  { 192, 144, 72 },
  { 168, 168, 168 } }, // The Basket of Apples by Paul Cezanne
      {
      { 72, 120, 72 },
      { 144, 192, 168 },
      { 168, 168, 96 },
      { 120, 144, 72 },
      { 192, 216, 168 } }, // La Montagne Saint Victoire Barnes by Paul Cezanne
      {
      { 168, 72, 0 },
      { 192, 144, 24 },
      { 192, 120, 24 },
      { 216, 168, 48 },
      { 24, 0, 0 } } // Ancient of Days by William Blake
  };

  private static final int PALETT_COLORS = PALETT[0].length;
  //private static final int PALETT_COUNT = PALETT.length;

  private int palettIdx;
  private static Random rand = new Random();

  private BufferedImage image;

  private static final int MAXCELLS = 3000;
  private QuickSet<Cell> edgeList = new QuickSet<Cell>(MAXCELLS);
  private QuickSet<Cell> cellList = new QuickSet<Cell>(MAXCELLS);

  private int colorIdx1, colorIdx2;

  private int chunkId;
  private int offsetX;
  

  public Crystal(BufferedImage imageBuffer, int x, int y, int chunkId, int palettIdx)
  {
    this.chunkId = chunkId;
    this.image = imageBuffer;
    this.palettIdx = palettIdx;
    
    offsetX = 0;
   
    colorIdx1 = rand.nextInt(PALETT_COLORS);
    colorIdx2 = colorIdx1;
    while (colorIdx1 == colorIdx2)
    {
      colorIdx2 = rand.nextInt(PALETT_COLORS);
    }

    createCell(x, y, null);

  }
  
  
  public static int getPalettIdxOfChunkType(EnumChunkType chunkType)
  {
    if (chunkType == EnumChunkType.CURVED) return 0;
    else if (chunkType == EnumChunkType.SPIKE) return 1;
    return 2;
  }

  public static void setup(int gridX, int gridY)
  {
    Crystal.gridX = gridX;
    Crystal.gridY = gridY;
    gridRightMostGrowX = (int)(gridX * 0.95);

  }

  public void addOffsetX(int offset)
  {
    offsetX += offset;
  }

  public boolean grow(int currentChunk)
  {
    if (currentChunk > chunkId + 1)
    { // System.out.println("    Kill Crystal: currentChunk > chunkId +1");
      // setColorAll(currentChunk, Color.ORANGE);
      return false;
    }

    // System.out.println("Crystal.grow(): cellList.size()="+cellList.size());
    if ((edgeList.size() < 1) && (cellList.size()) < 1)
    { // System.out.println("    Kill Crystal: cellList.size() < 1");
      return false;
    }

    int growthCount = 0;
    int growthGoal = 10;

    // ============== If Edge list is not empty, grow only there =============
    boolean grewOnEdge = false;
    int i = 0;
    while ((growthCount < growthGoal) && (edgeList.size() > 0))
    {
      Cell cell = edgeList.get(i);
      int dir = getRandomOpenDirection(cell.x, cell.y);

      if (dir == DIR_NONE)
      {
        edgeList.remove(i);
      }
      else
      {
        grewOnEdge = true;
        int x = cell.x + DIR_DELTA_X[dir];
        int y = cell.y + DIR_DELTA_Y[dir];

        createCell(x, y, cell);
        growthCount++;
        i++;
      }
      if (i >= edgeList.size()) i = edgeList.size() - 1;
    }
    if (grewOnEdge) return true;

    // ============== If Edge list is not empty, grow only there =============

    growthGoal = 5;
    if (cellList.size() / 10 > growthGoal) growthGoal = cellList.size() / 10;
    if (growthGoal > 50) growthGoal = 50;

    int idx = cellList.size() - 1;
    while ((growthCount < growthGoal) && (cellList.size() > 0))
    {
      // int idx = rand.nextInt(cellList.size());

      Cell cell = cellList.get(idx);
      int dir = getRandomOpenDirection(cell.x, cell.y);

      if (dir == DIR_NONE)
      { // setRGB(cell.x, cell.y, Color.MAGENTA);
        cellList.remove(idx);
      }
      else
      {
        int x = cell.x + DIR_DELTA_X[dir];
        int y = cell.y + DIR_DELTA_Y[dir];
        growthCount++;
        createCell(x, y, cell);
      }

      idx--;
      if (idx < 0) idx = cellList.size() - 1;
    }

    if ((cellList.size() < 1) && (edgeList.size() < 1))
    {
      return false;
    }

    return true;
  }

  private void createCell(int x, int y, Cell parent)
  {
    int childColorIdx = colorIdx1;
    if (rand.nextDouble() < 0.5) childColorIdx = colorIdx2;

    int red = PALETT[palettIdx][childColorIdx][0];
    int green = PALETT[palettIdx][childColorIdx][1];
    int blue = PALETT[palettIdx][childColorIdx][2];

    if (parent != null)
    {
      red = (3 * parent.red + red) / 4;
      green = (3 * parent.green + green) / 4;
      blue = (3 * parent.blue + blue) / 4;
    }

    Cell cell = new Cell(x, y, red, green, blue);
    setRGB(x, y, red, green, blue);

    if (isCrystalOnEdge(x, y))
    {
      boolean ok = edgeList.add(cell);
      if (!ok)
      { // System.out.println("edgeList is full");
      }
    }
    else
    {
      boolean ok = cellList.add(cell);
      if (!ok)
      { // System.out.println("cellList is full");
      }
    }
  }

  public Point getRandomBirthSite(int currentChunk)
  {
    if (currentChunk > chunkId + 1) return null;

    if (cellList.size() < 1)
    {
      if (edgeList.size() < 1) return null;
      return NONE;
    }

    int idx = rand.nextInt(cellList.size());
    Cell cell = cellList.get(idx);

    int dir = getRandomOpenDirection(cell.x, cell.y);
    if (dir == DIR_NONE)
    {
      cellList.remove(idx);
      if ((cellList.size() < 1) && (edgeList.size() < 1)) return null;
      return NONE;
    }

    int x = cell.x + DIR_DELTA_X[dir];
    int y = cell.y + DIR_DELTA_Y[dir];
    return new Point(x, y);
  }

  public int getChunkID()
  {
    return chunkId;
  }

  // public static Color setRandomPalett()
  // {
  // palettIdx = rand.nextInt(PALETT_COUNT);
  // int red = PALETT[palettIdx][BACKGROUND_PALETT_IDX][0];
  // int green = PALETT[palettIdx][BACKGROUND_PALETT_IDX][1];
  // int blue = PALETT[palettIdx][BACKGROUND_PALETT_IDX][2];
  // backgroundColor = new Color(red, green, blue);
  // return backgroundColor;
  // }

  public static int redBlueGreenToInt(int r, int g, int b)
  {
    return (r << 16) | (g << 8) | b;
  }

  private int getRandomOpenDirection(int x, int y)
  {
    // System.out.print("getRandomOpenDirection("+x+","+y+"):  ");

    // if (isDirectionEmptyRock(x, y, DIR_EAST)) return DIR_EAST;
    // if (isDirectionEmptyRock(x, y, DIR_WEST)) return DIR_WEST;
    // if (rand.nextBoolean())
    // {
    // if (isDirectionEmptyRock(x, y, DIR_NORTH)) return DIR_NORTH;
    // if (isDirectionEmptyRock(x, y, DIR_SOUTH)) return DIR_SOUTH;
    // }
    // else
    // {
    // if (isDirectionEmptyRock(x, y, DIR_SOUTH)) return DIR_SOUTH;
    // if (isDirectionEmptyRock(x, y, DIR_NORTH)) return DIR_NORTH;
    // }

    int dir = rand.nextInt(DIR_LIST_CARDINAL.length);
    for (int i = 0; i < DIR_LIST_CARDINAL.length; i++)
    {
      if (isDirectionEmptyRock(x, y, dir)) return dir;
      dir = (dir + 1) % DIR_LIST_CARDINAL.length;
    }
    return DIR_NONE;
  }

  private boolean isDirectionEmptyRock(int x, int y, int dir)
  {
    int xx = x + DIR_DELTA_X[dir] + offsetX;
    int yy = y + DIR_DELTA_Y[dir];
    if (xx >= 0 && yy >= 0 && xx < gridRightMostGrowX && yy < gridY)
    {
      if (image.getRGB(xx, yy) == CrystalGrower.ROCK_AREA_INT) return true;
    }
    return false;
  }

  private boolean isCrystalOnEdge(int x, int y)
  {
    for (int i = 0; i < DIR_LIST_CARDINAL.length; i++)
    {
      int xx = x + DIR_DELTA_X[i] + offsetX;
      int yy = y + DIR_DELTA_Y[i];
      if (xx >= 0 && yy >= 0 && xx < gridX && yy < gridY)
      {
        if (image.getRGB(xx, yy) == CrystalGrower.PLAY_AREA_INT) return true;
      }
    }
    return false;
  }
  
  public int getPalettIdx() {return palettIdx;}

  // =========================================================================
  // setRGB(int x, int y, int r, int g, int b)
  // =========================================================================
  public void setRGB(int x, int y, int r, int g, int b)
  {
    // if (x<0) x=0;
    // if (y<0) y=0;
    // if (x>imageWidth) x=imageWidth;
    // if (y>imageHeight) y=imageHeight;
    // if (r<0 || g<0 || b<0) return;
    // if (r>255 || g>255 || b>255) return;

    int rgb = (r << 16) | (g << 8) | b;
    image.setRGB(x + offsetX, y, rgb);
  }

  // =========================================================================
  // setRGB(int x, int y, int r, int g, int b)
  // =========================================================================
  public void setRGB(int x, int y, Color c)
  {
    // if (x<0) x=0;
    // if (y<0) y=0;
    // if (x>imageWidth) x=imageWidth;
    // if (y>imageHeight) y=imageHeight;
    // if (r<0 || g<0 || b<0) return;
    // if (r>255 || g>255 || b>255) return;

    image.setRGB(x + offsetX, y, c.getRGB());
  }

  private class Cell
  {
    public int x;
    public int y;
    public int red, green, blue;

    public Cell(int x, int y, int red, int green, int blue)
    {
      // System.out.println("new Cell("+x + ", " + y+")");
      this.x = x;
      this.y = y;

      this.red = red;
      this.green = green;
      this.blue = blue;
    }
  }
}
