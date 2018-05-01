import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Program to find the largest number in an array
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
public class driver_lab1813 {

  /**
   * Main method for lab 18.13
   *
   * @param args This program does not utilize arguments
   */
  public static void main(String[] args) {
    Scanner input = new Scanner(System.in);

    System.out.println("Enter the numbers to use in your array: ");
    int[] arr = Arrays.stream(input.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray();

    System.out.format("The largest int in your input was %d", largestInt(arr));

  }

  private static int largestInt(int[] arr) {
    int val = arr[0];
    int comp = arr.length > 1 ? largestInt(Arrays.copyOfRange(arr, 1, arr.length)) : arr[0];

    return val > comp ? val : comp;
  }
}
