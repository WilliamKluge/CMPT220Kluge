//package Lab105;

import java.util.Scanner;

/**
 * Program to find the prime factors of a number
 */
public class driver_lab105 {

  /**
   * Main method for lab 10.5
   */
  public static void main(String[] args) {

    Scanner input = new Scanner(System.in);

    System.out.print("Enter an integer to find the smallest factors of: ");

    int value = input.nextInt();

    StackOfIntegers stackOfIntegers = new StackOfIntegers();

    int[] i; // Used so we can second (larger) factor of the final iteration so it can be added
    for (i = factors(value); i[0] != 1; i = factors(i[1])) {
      // Initialized with the first factors of the value
      // Runs while the second (larger) factor can still be factored
      // Next iteration uses the factors of the larger factor
      stackOfIntegers.push(i[0]);
    }

    stackOfIntegers.push(i[1]); // Add the last factor

    while (stackOfIntegers.getSize() > 0) {
      System.out.print(stackOfIntegers.pop() + " ");
    }

  }

  /**
   * Finds the factors of a number
   *
   * @param val Number to find factors of
   * @return Factors of val represented by an int[]
   */
  private static int[] factors(int val) {

    int[] factors = new int[]{1, val};

    for (int i = 2; i < val / 2; ++i) {

      if (val % i == 0) {

        factors[0] = i;
        factors[1] = val / i;
        break;

      }

    }

    return factors;

  }

}
