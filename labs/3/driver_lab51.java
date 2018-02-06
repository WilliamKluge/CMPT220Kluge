import java.util.Scanner;

/**
 * Program to determine the count of positive and negative values entered
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
public class driver_lab51 {

  /**
   * Main method for lab 5.1
   *
   * @param args This program does not utilize arguments
   */
  public static void main(String[] args) {
    // Setup input
    Scanner input = new Scanner(System.in);

    // Create Variables
    int positive_numbers = 0;
    int negative_numbers = 0;
    int sum = 0;
    double count = 0;

    System.out.print("Enter an integer, the input ends if 0 is entered: ");
    int i = input.nextInt();
    while (i != 0) {
      ++count;
      sum += i;

      if (i > 0) {
        ++positive_numbers;
      } else {
        ++negative_numbers;
      }

      i = input.nextInt();

    }

    if (count == 0) {
      System.out.println("No numbers were entered except 0");
    } else {
      System.out.format("The number of positives is %d\n"
          + "The number of negatives is %d\n"
          + "The sum is %d\n"
          + "The average is %.2f", positive_numbers, negative_numbers, sum, sum / count);
    }

  }

}
