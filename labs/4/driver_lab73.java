import java.util.Arrays;
import java.util.Scanner;

/**
 * Program to count the occurance of numbers
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
public class driver_lab73 {

    /**
     * Main method for lab 7.3
     *
     * @param args This program does not utilize arguments
     */
    public static void main(String[] args) {

        // Setup input
        Scanner input = new Scanner(System.in);

        // Get the numbers
        int[] numbers = Arrays.stream(input.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray();
        int[] counts = new int[100];

        for (int i = 0; i < numbers.length - 1; ++i)
            ++counts[numbers[i] - 1];

        for (int i = 0; i < counts.length; ++i)
            if (counts[i] > 0)
                System.out.format("%d occurs %d %s\n", i + 1, counts[i], (counts[i] > 1) ? "times" : "time");

    }
}
