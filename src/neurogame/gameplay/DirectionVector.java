package neurogame.gameplay;

public class DirectionVector
{
  public double x = 0;
  public double y = 0;

  private static String numFormat = "%.4f";

  public double getAcceleration()
  {
    return Math.sqrt(x * x + y * y);
  }

  public String toString()
  {
    return String.format("(" + numFormat + ", " + numFormat + ")", x, y);
  }

}
