import java.util.Arrays;
import java.util.Scanner;

/**
 * Program to calculate the mean and standard deviation of n numbers
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
public class driver_lab711 {

    /**
     * Main method for lab 7.11
     *
     * @param args This program does not utilize arguments
     */
    public static void main(String[] args) {

        // Setup input
        Scanner input = new Scanner(System.in);

        // Get the numbers
        double[] numbers = Arrays.stream(input.nextLine().split(" ")).mapToDouble(Double::parseDouble).toArray();

        System.out.format("The mean is %f\nThe standard deviation is %f", mean(numbers), deviation(numbers));

    }

    /**
     * Find the mean of some numbers
     * @param numbers Numbers to find the mean for
     * @return The mean of the numbers
     */
    private static double mean(double[] numbers) {

        double sum = 0;

        for (double number : numbers)
            sum += number;

        return sum / numbers.length;

    }

    /**
     * Finds the standard deviation of numbers
     * @param numbers Numbers to find the deviation in
     * @return The standard deviation
     */
    private static double deviation(double[] numbers) {

        // element - mean squared
        double mean = mean(numbers);

        double sum = 0;

        for (double number : numbers)
            sum += Math.pow(number - mean, 2);

        return Math.sqrt(sum);

    }
}
