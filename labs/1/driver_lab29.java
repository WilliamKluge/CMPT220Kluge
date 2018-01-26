import java.util.Scanner;

/**
 * Main class for Lab 2.9
 *
 * @author William Kluge
 * Contact: klugewilliam@gmail.com
 */
public class driver_lab29 {
    /**
     * Main method for Lab 2.9
     *
     * @param args This program does not use arguments
     */
    public static void main(String[] args) {
        // Setup program input
        Scanner input = new Scanner(System.in);
        // Get values from user
        System.out.print("Enter v0, v1, and t: ");
        double v0 = input.nextDouble();
        double v1 = input.nextDouble();
        double t = input.nextDouble();
        // Calculate average acceleration
        double acceleration = (v1 - v0) / t;
        // Display output to user
        System.out.format("The average acceleration is %.4f", acceleration);
    }
}
