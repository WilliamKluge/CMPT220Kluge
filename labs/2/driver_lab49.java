import java.util.Scanner;

/**
 * Program to print the number related to a character
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
public class driver_lab49 {

  public static void main(String[] args) {
    // Setup input
    Scanner input = new Scanner(System.in);

    // Get input
    System.out.print("Enter one character: ");
    String character = input.nextLine();

    while (character.length() > 1 || Character.isDigit(character.charAt(0))) {
      System.out.print("Invalid input, try again: ");
      character = input.nextLine();
    }

    // Print result
    System.out.format("%s is %d", character.charAt(0), (int) (character.charAt(0)));

  }

}
