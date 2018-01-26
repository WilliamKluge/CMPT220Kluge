import java.util.Scanner;

/**
 * Main class for Lab 2.5
 *
 * @author William Kluge
 * Contact: klugewilliam@gmail.com
 */
public class driver_lab25 {
    /**
     * Main method for Lab 2.5
     *
     * @param args This program does not use arguments
     */
    public static void main(String[] args) {
        // Setup program input
        Scanner input = new Scanner(System.in);
        // Get value from user
        System.out.print("Enter subtotal and a gratuity rate: ");
        double subtotal = input.nextInt();
        double gratuityRate = input.nextInt();
        // Calculate gratuity
        double gratuity = subtotal * gratuityRate / 100.0;
        // Display output to user
        System.out.format("The gratuity is $%.2f and the total is $%.2f", gratuity, gratuity + subtotal);
    }
}
