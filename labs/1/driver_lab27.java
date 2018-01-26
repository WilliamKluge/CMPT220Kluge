import java.util.Scanner;

/**
 * Main class for Lab 2.7
 *
 * @author William Kluge
 * Contact: klugewilliam@gmail.com
 */
public class driver_lab27 {
    /**
     * Main method for Lab 2.7
     *
     * @param args This program does not use arguments
     */
    public static void main(String[] args) {
        // Setup program input
        Scanner input = new Scanner(System.in);
        // Get value from user
        System.out.print("Enter the number of minutes: ");
        int minutes = input.nextInt();
        // Calculate years and days
        int total_days = minutes / 60 / 24;
        int years = total_days / 365;
        int days = total_days % 365;
        // Print output to user
        System.out.format("%d minutes is approximately %d years and %d days", minutes, years, days);
    }
}
