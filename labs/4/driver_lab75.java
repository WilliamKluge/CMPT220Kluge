import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Program to display distinct numbers
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
public class driver_lab75 {

    /**
     * Main method for lab 7.5
     *
     * @param args This program does not utilize arguments
     */
    public static void main(String[] args) {
        // Setup input
        Scanner input = new Scanner(System.in);

        // Get the numbers
        int[] numbers = Arrays.stream(input.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray();
        ArrayList<Integer> printedNumbers = new ArrayList<>();

        for (int number : numbers) {

            if (!printedNumbers.contains(number)) {
                System.out.print(number + " ");
                printedNumbers.add(number);
            }

        }

    }
}
