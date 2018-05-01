import java.util.Scanner;

/**
 * Program to find the occurrences of a specified character in a string
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
public class driver_lab1815 {

  /**
   * Main method for lab 18.15
   *
   * @param args This program does not utilize arguments
   */
  public static void main(String[] args) {
    Scanner input = new Scanner(System.in);

    System.out.print("Enter the string to count in and then the character to count: ");
    String target = input.next();
    char c = input.next().charAt(0);

    System.out.format("Your search character occurs %d times", count(target, c));
  }

  private static int count(String str, char a) {
    int pos = str.indexOf(a);
    int count = count(str, a, pos);
  }

  private static int count(String str, char a, int high) {

  }
}
