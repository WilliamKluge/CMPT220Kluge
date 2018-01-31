import java.util.Scanner;

/**
 * Program to check if a SSN is valid
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
public class driver_lab423 {

  /**
   * Main method for driver_lab423
   *
   * @param args This program does not utilize arguments
   */
  public static void main(String[] args) {
    // Setup input
    Scanner input = new Scanner(System.in);

    // Get input
    System.out.print("Enter employees name: ");
    String employee = input.nextLine();

    System.out.print("Enter the number of hours this employee works in a week: ");
    double hoursWorked = input.nextDouble();

    System.out.print("Enter the hourly pay of this worker: ");
    double hourlyPay = input.nextDouble();

    // Print payroll statement
    System.out.format("%s worked %.2f hours for $%.2f", employee, hoursWorked,
        hoursWorked * hourlyPay);

  }

}
