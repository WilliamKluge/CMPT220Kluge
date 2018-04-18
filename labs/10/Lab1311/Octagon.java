//package Lab1311;

public class Octagon extends GeometricObject implements Comparable<Octagon>, Cloneable {

  private double side;

  public Octagon() {
    side = 0;
  }

  public Octagon(double side) {
    this.side = side;
  }

  @Override
  public double getArea() {
    return (2 + 4 / Math.sqrt(2)) * side * side;
  }

  @Override
  public double getPerimeter() {
    return side * 8;
  }

  @Override
  public int compareTo(Octagon o) {
    return Double.compare(side, o.side);
  }

  public double getSide() {
    return side;
  }

  public void setSide(double side) {
    this.side = side;
  }
}
