import java.util.Arrays;
import java.util.Scanner;

/**
 * Program to find the sum of a specified column in a matrix
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
public class driver_lab81 {

    /**
     * Main method for lab 8.1
     *
     * @param args This program does not utilize arguments
     */
    public static void main(String[] args) {

        // Setup input
        Scanner input = new Scanner(System.in);

        // Get the matrix
        System.out.println("Enter a 3-by-4 matrix row-by-row: ");
        double[][] matrix = new double[3][4];
        matrix[0] = Arrays.stream(input.nextLine().split(" ")).mapToDouble(Double::parseDouble).toArray();
        matrix[1] = Arrays.stream(input.nextLine().split(" ")).mapToDouble(Double::parseDouble).toArray();
        matrix[2] = Arrays.stream(input.nextLine().split(" ")).mapToDouble(Double::parseDouble).toArray();

        // Print the sums
        for (int column = 0; column < 4; ++column)
            System.out.format("Sum of elements at column %d is %.1f\n", column, sumColumn(matrix, column));

    }

    /**
     * Finds the sum of the specified column in matrix m
     * @param m Matrix to use for summation
     * @param columnIndex Index to get the sum for
     * @return Sum of column columnIndex of m
     */
    private static double sumColumn(double[][] m, int columnIndex) {

        double sum = 0;
        for (double[] row : m)
            sum += row[columnIndex];

        return sum;

    }
}
