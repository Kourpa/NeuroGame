/**
 * @author Marcos Lemus
 * */
package neurogame.level;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import neurogame.gameplay.EnumCollisionType;
import neurogame.library.Library;

/**
 * Holds a list of paths, and the shapes generated by all the vertices in the
 * chunk.
 */
public final class Chunk
{

  private List<PathVertex> vertexList;
  private Path2D.Double[] topAndBottom = new Path2D.Double[2];
  private EnumChunkType pathType;
  private double minYofBottom;
  private double maxYofTop;
  private double shipPadding;

  private double startX;
  private double chunkWidth;
  private static final double CHUNK_HEIGHT = 1.0;
  public static final int TOP = 0;
  public static final int BOTTOM = 1;

  private final Random random = Library.RANDOM;

  /**
   * Class constructor takes a reference the chunk size and path type
   * 
   * @param reference
   * @param chunkSize
   * @param pathType
   */
  public Chunk(Chunk lastChunk, double requestedWidth, EnumChunkType pathType,
      double shipPadding)
  {
    this.shipPadding = shipPadding;
    int vertexPairCount = 1 + (int) (requestedWidth / pathType.getStepSize());

    vertexList = new ArrayList<>();
    topAndBottom[0] = new Path2D.Double();
    topAndBottom[1] = new Path2D.Double();

    minYofBottom = 1.0;
    maxYofTop = 0.0;

    this.pathType = pathType;
    vertexList = new ArrayList<>();

    PathVertex firstVertex;
    if (lastChunk == null)
    {
      firstVertex = new PathVertex(0, 0, 1);
    }
    else
    {
      firstVertex = new PathVertex(lastChunk.getLastVertex());
    }

    startX = firstVertex.getX();

    topAndBottom[0].moveTo(firstVertex.getX(), 0);
    topAndBottom[1].moveTo(firstVertex.getX(), 1);

    topAndBottom[0].lineTo(firstVertex.getX(), firstVertex.getTopY());
    topAndBottom[1].lineTo(firstVertex.getX(), firstVertex.getBottomY());

    vertexList.add(firstVertex);

    // PathVertex vertex = new PathVertex(firstVertex, pathType);

    if (pathType == EnumChunkType.CURVED)
    {
      curved(firstVertex, vertexPairCount);
    }
    else if (pathType == EnumChunkType.SQUARE)
    {
      square(firstVertex, vertexPairCount);
    }
    else
    {
      PathVertex vertex = firstVertex;
      //System.out.println("vertexPairCount=" + vertexPairCount);
      for (int c = 0; c < vertexPairCount; c++)
      {

        vertex = new PathVertex(vertex, pathType, shipPadding);
        vertexList.add(vertex);
        topAndBottom[0].lineTo(vertex.getX(), vertex.getTopY());
        topAndBottom[1].lineTo(vertex.getX(), vertex.getBottomY());

      }
    }

    topAndBottom[0].lineTo(getLastVertex().getX(), 0);
    topAndBottom[1].lineTo(getLastVertex().getX(), 1);

    topAndBottom[0].closePath();
    topAndBottom[1].closePath();

    Rectangle2D boundsTop = topAndBottom[TOP].getBounds2D();
    Rectangle2D boundsBot = topAndBottom[BOTTOM].getBounds2D();

    double widthTop = boundsTop.getWidth();
    double widthBot = boundsBot.getWidth();

    //System.out.println("chunk.bounds=" + Library.bounds2DString(boundsTop)
    //    + ", " + Library.bounds2DString(boundsBot));

    chunkWidth = Math.min(widthTop, widthBot);

    minYofBottom = boundsBot.getMinY();
    maxYofTop = boundsTop.getMaxY();

  }

  private void square(PathVertex vertex, int chunkSize)
  {
    PathVertex vertex2;
    for (int c = 0; c < chunkSize; c++)
    {
      double x = vertex.getX() - pathType.getStepSize();
      vertex2 = new PathVertex(x, vertex.getTopY(), vertex.getBottomY());

      topAndBottom[0].lineTo(vertex2.getX(), vertex2.getTopY());
      topAndBottom[1].lineTo(vertex2.getX(), vertex2.getBottomY());

      topAndBottom[0].lineTo(vertex.getX(), vertex.getTopY());
      topAndBottom[1].lineTo(vertex.getX(), vertex.getBottomY());

      vertex = new PathVertex(vertex, pathType, shipPadding);
      vertexList.add(vertex2);
      vertexList.add(vertex);

    }
  }

  /**
   * Generate the chunk using curved lines.
   * 
   * @param p
   * @param chunkSize
   */
  private void curved(PathVertex vertex, int chunkSize)
  {
    int r;
    double curve;
    double lastX;
    double lastTopY;
    double lastBottomY;

    for (int c = 1; c < chunkSize; c++)
    {
      r = random.nextInt(2);
      curve = r == 1 ? random.nextDouble() * .1 : random.nextDouble() * -.1;

      lastX = vertex.getX();
      lastTopY = vertex.getTopY();
      lastBottomY = vertex.getBottomY();

      vertex = new PathVertex(vertex, pathType, shipPadding);
      vertexList.add(vertex);

      topAndBottom[0].curveTo(lastX, lastTopY, (lastX + vertex.getX()) / 2,
          (vertex.getTopY() + lastTopY) / 2 + curve, vertex.getX(),
          vertex.getTopY());

      /** re randomize the curve to avoid identical sides. **/
      r = random.nextInt(2);
      curve = r == 1 ? random.nextDouble() * .1 : random.nextDouble() * -.1;

      topAndBottom[1].curveTo(lastX, lastBottomY, (lastX + vertex.getX()) / 2,
          (vertex.getBottomY() + lastBottomY) / 2 + curve, vertex.getX(),
          vertex.getBottomY());

    }
  }

  /**
   * sets the chunks next generated pathType.
   * 
   * @param pathType
   */
  public void setPathType(EnumChunkType pathType)
  {
    this.pathType = pathType;
  }

  // ///////////////////////////////////////////////////////////////
  // Getters
  // ///////////////////////////////////////////////////////////////
  public List<PathVertex> getPathList()
  {
    return vertexList;
  }

  public Path2D.Double[] getTopAndBottom()
  {
    return topAndBottom;
  }

  public Path2D.Double getTop()
  {
    return topAndBottom[0];
  }

  public Path2D.Double getBottom()
  {
    return topAndBottom[1];
  }

  public PathVertex getLastVertex()
  {
    return vertexList.get(vertexList.size() - 1);
  }

  public PathVertex getVertexRightOf(double x)
  {
    for (PathVertex vertex : vertexList)
    {
      if (vertex.getX() > x) return vertex;
    }
    return null;
  }

  
  public PathVertex getInterpolatedWallTopAndBottom(double x)
  {
    if (x < startX) return null;

    if (x > startX + chunkWidth) return null;
    
    PathVertex vertex0 = vertexList.get(0);
    
    for (PathVertex vertex1 : vertexList)
    {
      if (vertex1.getX() >= x)
      {
        if (vertex0 == vertex1) return vertex0;
        
        double top0 = vertex0.getTopY();
        double top1 = vertex1.getTopY();
        
        double bot0 = vertex0.getBottomY();
        double bot1 = vertex1.getBottomY();
        
        double x0 = vertex0.getX();
        double x1 = vertex1.getX();
        
        double scale = (x - x0) / (x1 - x0);
        double top = top0 + (top1-top0)*scale;
        double bot = bot0 + (bot1-bot0)*scale;
        
        return new PathVertex(x, top, bot);
      }
      
      vertex0 = vertex1;
    }
    return null;
  }
  
  
  public double getMinYofBottom()
  {
    return minYofBottom;
  }

  public double getMaxYofTop()
  {
    return maxYofTop;
  }

  public double getWidth()
  {
    return chunkWidth;
  }

  public double getHeight()
  {
    return CHUNK_HEIGHT;
  }

  public double getStartX()
  {
    return startX;
  }

  public EnumChunkType getpathType()
  {
    return pathType;
  }
}
