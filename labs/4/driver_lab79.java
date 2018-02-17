import java.util.Arrays;
import java.util.Scanner;

/**
 * Program to find the minimum of entered numbers
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
public class driver_lab79 {

    /**
     * Main method for lab 7.9
     *
     * @param args This program does not utilize arguments
     */
    public static void main(String[] args) {
        // Setup input
        Scanner input = new Scanner(System.in);

        // Get the numbers
        double[] numbers = Arrays.stream(input.nextLine().split(" ")).mapToDouble(Double::parseDouble).toArray();

        System.out.format("Smallest number is %f", min(numbers));

    }

    /**
     * Finds the minimum number of the array
     * @param numbers Array to find the minimum of
     * @return Smallest number in that array
     */
    private static double min(double[] numbers) {

        double minNumber = numbers[0];

        for (double number : numbers)
            if (number < minNumber)
                minNumber = number;

        return minNumber;

    }
}
