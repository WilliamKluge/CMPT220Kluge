import java.util.Scanner;

/**
 * Program to check if a character is a vowel or a consonant
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
public class driver_lab413 {

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

    // Check and print result
    String message;

    switch (character.toLowerCase().charAt(0)) {
      case 'a': // a
      case 'e': // e
      case 'i': // i
      case 'o': // o
      case 'u': // u
        message = " is a vowel.";
        break;
      default:
        message = " is a consonant.";
    }

    System.out.format(character + message);

  }

}
