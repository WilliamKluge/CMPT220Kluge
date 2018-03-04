//package Lab911;

import java.util.Scanner;

/**
 * Program to test the LinearEquation class
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
public class driver_lab911 {

  /**
   * Main method for lab 9.11
   *
   * @param args This program does not utilize arguments
   */
  public static void main(String[] args) {

    Scanner input = new Scanner(System.in);

    System.out
        .print("Enter variables for a, b, c, d, e, and f (press enter after every variable): ");
    LinearEquation linearEquation = new LinearEquation(input.nextDouble(), input.nextDouble(),
        input.nextDouble(),
        input.nextDouble(), input.nextDouble(), input.nextDouble());

    if (linearEquation.isSolvable()) {
      System.out.format("X: %f\nY: %f\n", linearEquation.getX(), linearEquation.getY());
    } else {
      System.out.println("Equation is not solvable");
    }

  }
}
