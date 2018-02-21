import java.util.Random;
import java.util.Scanner;

/**
 * Program to print an n-by-n matrix
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
public class driver_lab617 {

    /**
     * Main method for lab 6.17
     *
     * @param args This program does not utilize arguments
     */
    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        System.out.print("Enter n: ");
        int n = input.nextInt();

        printMatrix(n);

    }

    /**
     * Prints a matrix of 0s or 1s
     * @param n Size of the n-by-n matrix
     */
    private static void printMatrix(int n) {

        Random ran = new Random();

        int[][] matrix = new int[n][n];

        for (int[] row : matrix)
            for (int i = 0; i <  row.length; ++i)
                row[i] = ran.nextInt(2);

        for (int[] row : matrix) {
            for (int i : row)
                System.out.print(i + " ");
            System.out.print("\n");
        }


    }
}
