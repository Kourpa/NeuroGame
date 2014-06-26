/**
 * @author Marcos
 */
package neurogame.level;

import neurogame.library.Library;

/*
 * Data structure used to hold vertex information.
 */
public class PathVertex
{
  private double x;
  private double topY;
  private double bottomY;
  private static final double ROCK_PADDING = .01;
  private Direction directionMoved;
  
  /**
   * Generates vertices given the specified values.
   * 
   * @param reference
   *          keep the vertices flush between chunks
   * @param pathType
   *          type of generation
   * @param shipPadding
   */
  public PathVertex(PathVertex reference, EnumChunkType pathType,
      double shipPadding)
  {
    double stepSize = pathType.getStepSize();
    double maxChange = pathType.getMaxChange();

    x = 0;
    topY = ROCK_PADDING;
    bottomY = 1 - ROCK_PADDING;

    if (reference != null)
    {
      x = reference.getX() + stepSize;

      int r = Library.RANDOM.nextInt(20);
      if (reference.directionMoved == Direction.UP)
      {
        if(r == 1){
          directionMoved = Direction.DOWN;
          topY = reference.topY + Library.RANDOM.nextDouble() * maxChange;
          bottomY = reference.bottomY + Library.RANDOM.nextDouble() * maxChange;
        }
        else
        {
          directionMoved = Direction.UP;
          topY = reference.topY - Library.RANDOM.nextDouble() * maxChange;
          bottomY = reference.bottomY - Library.RANDOM.nextDouble() * maxChange;
        }
      }
      else
      {
        if(r == 1){
          directionMoved = Direction.UP;
          topY = reference.topY - Library.RANDOM.nextDouble() * maxChange;
          bottomY = reference.bottomY - Library.RANDOM.nextDouble() * maxChange;
        }
        else
        {
          directionMoved = Direction.DOWN;
          topY = reference.topY + Library.RANDOM.nextDouble() * maxChange;
          bottomY = reference.bottomY + Library.RANDOM.nextDouble() * maxChange;
        }
      }
    }

    /** checks * */
    double dy = bottomY - topY;
    if (dy < shipPadding)
    {
      bottomY += (shipPadding - dy) / 2;
      topY -= (shipPadding - dy) / 2;
    }

    if (bottomY > 1 - ROCK_PADDING)
    { directionMoved = Direction.UP;
      bottomY += 1 - ROCK_PADDING - bottomY;
    }
    else if (bottomY < shipPadding)
    { directionMoved = Direction.DOWN;
      bottomY += shipPadding - bottomY;
    }

    if (topY < ROCK_PADDING)
    { directionMoved = Direction.DOWN;
      topY += ROCK_PADDING - topY;
    }
    else if (topY > 1 - shipPadding)
    { directionMoved = Direction.UP;
      topY += 1 - shipPadding - topY;
    }

  }

  public PathVertex(PathVertex clone)
  {
    x = clone.x;
    topY = clone.topY;
    bottomY = clone.bottomY;
  }

  /**
   * Specify an exact path location
   * 
   * @param x
   * @param topY
   * @param bottomY
   */
  public PathVertex(double x, double topY, double bottomY)
  {
    this.x = x;
    this.topY = topY;
    this.bottomY = bottomY;
  }

  public double getX()
  {
    return x;
  }

  public double getTopY()
  {
    return topY;
  }

  public double getBottomY()
  {
    return bottomY;
  }

  public double getCenter()
  {
    return (bottomY + topY) / 2;
  }
  
  @Override
  public String toString()
  {
    return "X:" + x + " TopY:" + topY + " BottomY:" + bottomY;
  }
  
  enum Direction{
      UP, DOWN;
  }
}