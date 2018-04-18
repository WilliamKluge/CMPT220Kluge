//package Lab1311;

/**
 * Program to test the Octagon class
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
public class driver_lab1311 {

  /**
   * Main method for lab 13.11
   *
   * @param args This program does not utilize arguments
   */
  public static void main(String[] args) {
    Octagon octagonOne = new Octagon(10);
    Octagon octagonTwo = new Octagon(20);

    System.out.println("Area of First Octagon: " + octagonOne.getArea());
    System.out.println("Comparison of the two octagons: " + octagonOne.compareTo(octagonTwo));
  }
}
