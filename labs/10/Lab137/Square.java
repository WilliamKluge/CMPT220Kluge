package Lab137;

/**
 * Defines what a square is
 */
public class Square extends GeometricObject implements Colorable {

  private double side;

  public Square() {
    side = 0;
  }

  public Square(double side) {
    this.side = side;
  }

  @Override
  public void howToColor() {
    System.out.println("Color all four sides");
  }

  public double getSide() {
    return side;
  }

  public void setSide(double side) {
    this.side = side;
  }
}
