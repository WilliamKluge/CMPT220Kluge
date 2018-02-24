import java.util.Arrays;
import java.util.Scanner;

/**
 * Program to determine if a list is already sorted.
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
public class driver_lab719 {

    /**
     * Main method for lab 7.19
     *
     * @param args This program does not utilize arguments
     */
    public static void main(String[] args) {

        // Setup input
        Scanner input = new Scanner(System.in);

        // Get the numbers
        System.out.print("Enter your numbers: ");
        int[] numbers = Arrays.stream(input.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray();

        // Print amount
        System.out.format("The list has %d integers: ", numbers.length);
        // Print the numbers
        for (int i : numbers)
            System.out.print(i + " ");
        // Print if they were sorted
        System.out.println("\nThe array is " + (isSorted(numbers) ? "sorted" : "not sorted"));

    }

    /**
     * Determines if a list is already sorted
     * @param list List to check
     * @return If list was already sorted
     */
    private static boolean isSorted(int[] list) {
        int[] oldList = Arrays.copyOf(list, list.length);
        Arrays.sort(list);
        return oldList == list;
    }
}
