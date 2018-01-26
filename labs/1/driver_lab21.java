import java.util.Scanner;

/**
 * Main class for Lab 2.1
 *
 * @author William Kluge
 * Contact: klugewilliam@gmail.com
 */
public class driver_lab21 {
    /**
     * Main method for Lab 2.1
     *
     * @param args This program does not use arguments
     */
    public static void main(String[] args) {
        // Setup program input
        Scanner input = new Scanner(System.in);
        // Get value from user
        System.out.print("Enter a degree in Celsius: ");
        double celsius = input.nextDouble();
        // Convert
        double fahrenheit = (9.0 / 5) * celsius + 32;
        // Print output to user
        System.out.format("%.1f Celsius is %.1f Fahrenheit", celsius, fahrenheit);
    }
}
