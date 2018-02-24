import java.util.Arrays;
import java.util.Scanner;

/**
 * Program to show the heads-tails array associated with a number
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
public class driver_lab811 {

    /**
     * Main method for lab 8.11
     *
     * @param args This program does not utilize arguments
     */
    public static void main(String[] args) {

        // Setup input
        Scanner input = new Scanner(System.in);

        // Get the user's number
        System.out.print("Enter a number between 0 and 511: ");
        int number = input.nextInt();

        // Translate into matrix
        String[] binaryNumberArray = String.format("%9s", Integer.toBinaryString(number)).replace(" ", "0")
                .replace("0", "H").replace("1", "T").split("");
        String[][] matrix = {Arrays.copyOfRange(binaryNumberArray, 0, 3), Arrays.copyOfRange(binaryNumberArray, 3, 6),
                Arrays.copyOfRange(binaryNumberArray, 6, 9)};

        System.out.format("The matrix associated with %d is:\n", number);
        for (String[] row : matrix) {
            for (String column : row)
                System.out.print(column + " ");
            System.out.print("\n");
        }

    }
}
