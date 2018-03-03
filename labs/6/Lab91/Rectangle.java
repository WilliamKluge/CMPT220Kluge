package Lab91;

/**
 * Class to hold data on a rectangle
 */
public class Rectangle {

  /**
   * Width of the rectangle
   */
  double width = 1;
  /**
   * Height of the rectangle
   */
  double height = 1;

  /**
   * Construct a default Rectangle object
   */
  Rectangle() {
  }

  /**
   * Construct a Rectangle with specified values.
   *
   * @param width Width of the rectangle
   * @param height Height of the rectangle
   */
  Rectangle(double width, double height) {
    this.width = width;
    this.height = height;
  }

  /**
   * Gets the area of the rectangle
   *
   * @return Area of this rectangle
   */
  public double getArea() {
    return width * height;
  }

  /**
   * Gets the perimeter of the rectangle
   *
   * @return Perimeter of the rectangle
   */
  public double getPerimeter() {
    return width * 2 + height * 2;
  }

  /**
   * Prints the width, height, area, and perimeter of the rectangle
   */
  public void displayStats() {
    System.out.format("Width: %s\nHeight: %s\nArea: %s\nPerimeter: %s\n", width, height, getArea(),
        getPerimeter());
  }

}
