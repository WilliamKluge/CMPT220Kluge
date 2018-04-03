import java.util.ArrayList;
import java.util.Scanner;

/**
 * Program to
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
public class driver_lab111 {

  /**
   * Main method for lab
   *
   * @param args This program does not utilize arguments
   */
  public static void main(String[] args) {

    Scanner input = new Scanner(System.in);

    System.out.print("Enter three numbers representing the sides of the triangles: ");
    Triangle triangle = new Triangle(input.nextDouble(), input.nextDouble(), input.nextDouble());
    input.nextLine(); // Eat EOL from nextDoubles
    System.out.print("Enter the color of the triangle: ");
    triangle.setColor(input.nextLine());
    System.out.print("Is the triangle filled? (y/n)");
    String filledResponse = input.nextLine().toLowerCase();
    triangle.setFilled(filledResponse.equals("y"));

    System.out.println("Here is your triangle:");
    System.out.println("Area: " + triangle.getArea());
    System.out.println("Perimeter: " + triangle.getPerimeter());
    System.out.println("Color: " + triangle.getColor());
    System.out.println("Filled: " + triangle.isFilled());

  }

}
