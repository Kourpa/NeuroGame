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
  private double top;
  private double bottom;
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
    top = ROCK_PADDING;
    bottom = 1 - ROCK_PADDING;

    if (reference != null)
    {
      x = reference.getX() + stepSize;

      int r = Library.RANDOM.nextInt(20);
      if (reference.directionMoved == Direction.UP)
      {
        if(r == 1){
          directionMoved = Direction.DOWN;
          top = reference.top + Library.RANDOM.nextDouble() * maxChange;
          bottom = reference.bottom + Library.RANDOM.nextDouble() * maxChange;
        }
        else
        {
          directionMoved = Direction.UP;
          top = reference.top - Library.RANDOM.nextDouble() * maxChange;
          bottom = reference.bottom - Library.RANDOM.nextDouble() * maxChange;
        }
      }
      else
      {
        if(r == 1){
          directionMoved = Direction.UP;
          top = reference.top - Library.RANDOM.nextDouble() * maxChange;
          bottom = reference.bottom - Library.RANDOM.nextDouble() * maxChange;
        }
        else
        {
          directionMoved = Direction.DOWN;
          top = reference.top + Library.RANDOM.nextDouble() * maxChange;
          bottom = reference.bottom + Library.RANDOM.nextDouble() * maxChange;
        }
      }
    }

    /** checks * */
    double dy = bottom - top;
    if (dy < shipPadding)
    {
      bottom += (shipPadding - dy) / 2;
      top -= (shipPadding - dy) / 2;
    }

    if (bottom > 1 - ROCK_PADDING)
    { directionMoved = Direction.UP;
      bottom += 1 - ROCK_PADDING - bottom;
    }
    else if (bottom < shipPadding)
    { directionMoved = Direction.DOWN;
      bottom += shipPadding - bottom;
    }

    if (top < ROCK_PADDING)
    { directionMoved = Direction.DOWN;
      top += ROCK_PADDING - top;
    }
    else if (top > 1 - shipPadding)
    { directionMoved = Direction.UP;
      top += 1 - shipPadding - top;
    }

  }

  public PathVertex(PathVertex clone)
  {
    x = clone.x;
    top = clone.top;
    bottom = clone.bottom;
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
    this.top = topY;
    this.bottom = bottomY;
  }

  public double getX()
  {
    return x;
  }

  public double getTop()
  {
    return top;
  }

  public double getBottom()
  {
    return bottom;
  }

  public double getCenter()
  {
    return (bottom + top) / 2;
  }
  
  @Override
  public String toString()
  {
    return "X:" + x + " TopY:" + top + " BottomY:" + bottom;
  }
  
  enum Direction{
      UP, DOWN;
  }
}