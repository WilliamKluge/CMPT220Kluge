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

    System.out.print("Enter federal tax withholding rate: ");
    double federalTaxWithholding = input.nextDouble();

    System.out.print("Enter state tax withholding rate: ");
    double stateTaxWithholding = input.nextDouble();

    // Calculations
    double grossIncome = hoursWorked * hourlyPay;
    double federalWithholding = grossIncome * federalTaxWithholding;
    double stateHolding = grossIncome * stateTaxWithholding;
    double totalDeduction = federalWithholding + stateHolding;
    double netPay = grossIncome - totalDeduction;

    // Print payroll statement
    System.out.format("Employee Name: %s\n" +
                    "Hours Worked: %.2f\n" +
                    "Pay Rate: $%.2f\n" +
                    "Gross Pay: $%.2f\n\n" +
                    "Deductions:\n" +
                    "\tFederal Withholding (%.2f): $%.2f\n" +
                    "\tState Withholding (%.2f): $%.2f\n" +
                    "\tTotal Deduction: $%.2f\n" +
                    "Net Pay: $%.2f", employee, hoursWorked, hourlyPay, grossIncome, federalTaxWithholding * 100,
            federalWithholding, stateTaxWithholding * 100, stateHolding, totalDeduction, netPay);

  }

}
