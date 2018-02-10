import java.util.Scanner;

/**
 * Program to figure out if a number is a palindrome
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
public class driver_lab63 {

  /**
   * Main method for lab 6.3
   *
   * @param args This program does not utilize arguments
   */
  public static void main(String[] args) {

    Scanner input = new Scanner(System.in);

    System.out.print("Enter a number to test for palindromeness: ");

    int number = input.nextInt();

    if (isPalindrome(number)) {
      System.out.format("%d is a palindrome", number);
    } else {
      System.out.format("%d is not a palindrome", number);
    }

  }

  /**
   * Reverses a number
   *
   * @param n Number to reverse
   * @return Pentagonal number associated with n
   */
  private static int reverse(int n) {
    String workValue = Integer.toString(n);
    StringBuilder reversedNumber = new StringBuilder();

    for (int i = workValue.length() - 1; i >= 0; --i) {
      reversedNumber.append(workValue.charAt(i));
    }

    return Integer.valueOf(reversedNumber.toString());
  }

  /**
   * Determines if a number is a palindrome
   *
   * @param number Number to test
   * @return If number is a palindrome
   */
  private static boolean isPalindrome(int number) {
    return number == reverse(number);
  }

}