package Lab139;

/**
 * Program to compare circles
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
public class driver_lab139 {

  /**
   * Main method for lab 13.9
   *
   * @param args This program does not utilize arguments
   */
  public static void main(String[] args) {
    Circle circleOne = new Circle(10);
    Circle circleTwo = new Circle(10);

    System.out.println("Comparison of Circles: " + circleOne.compareTo(circleTwo));
  }
}
