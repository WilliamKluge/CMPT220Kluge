import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/**
 * Program to sort an array
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
public class driver_lab1111 {

  /**
   * Main method for lab 11.11
   *
   * @param args This program does not utilize arguments
   */
  public static void main(String[] args) {
    Scanner input = new Scanner(System.in);

    System.out.println("Enter five numbers: ");
    ArrayList<Integer> integerArrayList = new ArrayList<>();
    for (int i = 0; i < 5; ++i) {
      integerArrayList.add(input.nextInt());
    }
    sort(integerArrayList);
    System.out.println("Here are your numbers sorted in ascending order: "
        + integerArrayList.toString());
  }

  /**
   * Sorts the given list
   *
   * @param list List to sort
   */
  public static void sort(ArrayList<Integer> list) {
    list.sort((a, b) -> a < b ? -1 : a > b ? 1 : 0);
  }

}
