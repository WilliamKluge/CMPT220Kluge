package Lab913;

import java.util.Arrays;
import java.util.Scanner;

/**
 * Program to test the Location class
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
public class driver_lab913 {

  /**
   * Main method for lab 9.13
   *
   * @param args This program does not utilize arguments
   */
  public static void main(String[] args) {

    Scanner input = new Scanner(System.in);

    System.out.print("Enter rows and column for the array: ");
    int rows = input.nextInt();
    int columns = input.nextInt();
    input.nextLine(); // Consume the EOL character so that the Arrays.steam input isn't messed up

    double[][] matrix = new double[rows][columns];

    System.out.println("Enter the array:");

    for (int i = 0; i < matrix.length; ++i) {
      matrix[i] = Arrays.stream(input.nextLine().split(" ")).mapToDouble(Double::parseDouble)
          .toArray();
    }

    Location maxLocation = Location.locateLargest(matrix);

    System.out.format("The largest value is %f at (%d, %d).", maxLocation.maxValue,
        maxLocation.row, maxLocation.column);

  }
}
