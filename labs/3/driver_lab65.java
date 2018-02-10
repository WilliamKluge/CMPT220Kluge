import java.util.Arrays;
import java.util.Scanner;

/**
 * Program to sort three numbers
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
public class driver_lab65 {

  /**
   * Main method for lab 6.5
   *
   * @param args This program does not utilize arguments
   */
  public static void main(String[] args) {

    Scanner input = new Scanner(System.in);

    System.out.print("Enter three numbers number to sort: ");

    double num1 = input.nextDouble();
    double num2 = input.nextDouble();
    double num3 = input.nextDouble();

    displaySortedNumbers(num1, num2, num3);

  }

  /**
   * Returns the next pentagonal number
   *
   * @param num1 First number to sort
   * @param num2 Second number to sort
   * @param num3 Third number to sort
   */
  private static void displaySortedNumbers(double num1, double num2, double num3) {
    double[] sortMe = {num1, num2, num3};
    Arrays.sort(sortMe);
    System.out.format("%f %f %f", sortMe[0], sortMe[1], sortMe[2]);
  }

}