import java.util.Scanner;

/**
 * Program to compute the sum of digits in an integer
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
public class driver_lab1811 {

  /**
   * Main method for lab 18.11
   *
   * @param args This program does not utilize arguments
   */
  public static void main(String[] args) {
    Scanner input = new Scanner(System.in);

    System.out.print("Enter a number: ");
    Integer number = input.nextInt();

    System.out.format("The sum of digits is %d", sumDigits(number));
  }

  private static int sumDigits(Integer number) {
    int sum = 0;

    for (Character c : number.toString().toCharArray()) {
      sum += Character.getNumericValue(c);
    }

    return sum;
  }
}
