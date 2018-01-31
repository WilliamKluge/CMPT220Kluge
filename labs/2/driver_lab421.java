import java.util.Scanner;

/**
 * Program to check if a SSN is valid
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
public class driver_lab421 {

  /**
   * Main method for driver_lab421
   *
   * @param args This program does not utilize arguments
   */
  public static void main(String[] args) {
    // Setup input
    Scanner input = new Scanner(System.in);

    // Get input
    System.out.print("Enter a SSN: ");
    String ssn = input.nextLine();

    // Check SSN
    boolean valid = true;

    for (int i = 0; i < ssn.length(); ++i) {
      if (i == 3 || i == 6) {
        valid = ssn.charAt(i) == '-';
      } else {
        valid = Character.isDigit(ssn.charAt(i));
      }

      if (!valid) {
        break;
      }
    }

    String message = " is a valid social security number.";

    if (!valid) {
      message = " is not a valid social security number.";
    }

    // Print the result
    System.out.println(ssn + message);

  }

}
