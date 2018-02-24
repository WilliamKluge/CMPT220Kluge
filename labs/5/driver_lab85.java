import java.util.Arrays;
import java.util.Scanner;

/**
 * Program to add together two matrices
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
public class driver_lab85 {

    /**
     * Main method for lab 8.5
     *
     * @param args This program does not utilize arguments
     */
    public static void main(String[] args) {
        // Setup input
        Scanner input = new Scanner(System.in);

        double[][] matrix1 = new double[3][3];
        double[][] matrix2 = new double[3][3];

        { // Don't mind me...just filling some matrices from one line statements...:okay_hand:
            System.out.print("Enter matrix1: ");
            double[] temp1 = Arrays.stream(input.nextLine().split(" ")).mapToDouble(Double::parseDouble).toArray();
            matrix1[0] = Arrays.copyOfRange(temp1, 0, 3);
            matrix1[1] = Arrays.copyOfRange(temp1, 3, 6);
            matrix1[2] = Arrays.copyOfRange(temp1, 6, 9);
            System.out.print("Enter matrix2: ");
            double[] temp2 = Arrays.stream(input.nextLine().split(" ")).mapToDouble(Double::parseDouble).toArray();
            matrix2[0] = Arrays.copyOfRange(temp2, 0, 3);
            matrix2[1] = Arrays.copyOfRange(temp2, 3, 6);
            matrix2[2] = Arrays.copyOfRange(temp2, 6, 9);
        }

        double[][] result = addMatrix(matrix1, matrix2);

        System.out.println("The matrices are added like so:");
        for (int i = 0; i < 3; ++i)
            System.out.format("%.2f %.2f %.2f %s %.2f %.2f %.2f %s %.2f %.2f %.2f\n", matrix1[i][0], matrix1[i][1],
                    matrix1[i][2], (i == 1) ? "+" : " ", matrix2[i][0], matrix2[i][1], matrix2[i][2],
                    (i == 1) ? "+" : " ", result[i][0], result[i][1], result[i][2]);

    }

    /**
     * Adds together two matrices
     * @param a First matrix
     * @param b Second matrix
     * @return The resulting matrix from adding a and b together
     */
    private static double[][] addMatrix(double[][] a, double[][] b) {

        double[][] result = new double[3][3];

        for (int row = 0; row < 3; ++row)
            for (int column = 0; column < 3; ++column)
                result[row][column] = a[row][column] + b[row][column];

        return result;

    }
}
