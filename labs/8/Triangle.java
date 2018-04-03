public class Triangle extends GeometricObject {

  /* Three sides of the triangle */
  private double side1 = 1.0, side2 = 1.0, side3 = 1.0;

  /**
   * Default constructor
   */
  public Triangle() {
  }

  /**
   * Constructor for specifying three sides of the triangle
   *
   * @param side1 First side of triangle
   * @param side2 Second side of triangle
   * @param side3 Third side of triangle
   */
  public Triangle(double side1, double side2, double side3) {
    this.side1 = side1;
    this.side2 = side2;
    this.side3 = side3;
  }

  /**
   * @return Perimeter of the triangle
   */
  public double getPerimeter() {
    return side1 + side2 + side3;
  }

  /**
   * @return Area of the triangle
   */
  public double getArea() {
    double perimeter = getPerimeter() / 2;
    return Math.sqrt(perimeter * (perimeter - side1) * (perimeter - side2) * (perimeter * side3));
  }

  /**
   * @return Description of the triangle
   */
  public String toString() {
    return "Triangle: side1 = " + side1 + " side2 = " + side2 + " side3 = " + side3;
  }

  public double getSide1() {
    return side1;
  }

  public void setSide1(double side1) {
    this.side1 = side1;
  }

  public double getSide2() {
    return side2;
  }

  public void setSide2(double side2) {
    this.side2 = side2;
  }

  public double getSide3() {
    return side3;
  }

  public void setSide3(double side3) {
    this.side3 = side3;
  }
}
