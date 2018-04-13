import java.util.ArrayList;
import java.util.Comparator;

/**
 * Program to sort an ArrayList of numbers
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
public class driver_lab133 {

  /**
   * Main method for lab 13.3
   *
   * @param args This program does not utilize arguments
   */
  public static void main(String[] args) {

    ArrayList<Number> numbers = new ArrayList<>();
    numbers.add(48432);
    numbers.add(84.46583);
    numbers.add(49);
    numbers.add(Double.MAX_VALUE);
    numbers.add(Integer.MAX_VALUE);
    numbers.add(-0.1);

    System.out.println("List before sort: " + numbers.toString());

    sort(numbers);

    System.out.println("List after sort: " + numbers.toString());

  }

  /**
   * This just calls list.sort haha, shoutout to IntelliJ's auto-refactoring for making my lambda
   * waaay cleaner
   *
   * @param list List to sort
   */
  public static void sort(ArrayList<Number> list) {
    list.sort(Comparator.comparingDouble(Number::doubleValue));
  }

}
