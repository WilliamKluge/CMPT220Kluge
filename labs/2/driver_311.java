import java.util.Scanner;

/**
 * Program to check how many days are in a given month
 * @author William Kluge
 *   Contact: klugewilliam@gmail.com
 */
public class driver_311 {

  /**
   * Main function for the driver_lab311 program
   * @param args Arguments are not implemented in this program
   */
  public static void main(String[] args) {
    // Setup input
    Scanner input = new Scanner(System.in);

    // Prompt user and get input
    System.out.print("Enter the month to calculate for (1-12): ");
    int month = input.nextInt();
    System.out.print("Enter the year to calculate for (>0000): ");
    int year = input.nextInt();

    // Calculate the result
    String month_name = "";
    int days = 0;

    switch (month) {
      case 1: // January
        month_name = "January";
        days = 31;
        break;
      case 2: // February
        month_name = "February";
        if (isLeapYear(year))
          days = 29;
        else
          days = 28;
        break;
      case 3: // March
        month_name = "March";
        days = 31;
        break;
      case 4: // April
        month_name = "April";
        days = 30;
        break;
      case 5: // May
        month_name = "May";
        days = 31;
        break;
      case 6: // June
        month_name = "June";
        days = 30;
        break;
      case 7: // July
        month_name = "July";
        days = 31;
        break;
      case 8: // August
        month_name = "August";
        days = 31;
        break;
      case 9: // September
        month_name = "September";
        days = 30;
        break;
      case 10: // October
        month_name = "October";
        days = 31;
        break;
      case 11: // November
        month_name = "November";
        days = 30;
        break;
      case 12: // December
        month_name = "December";
        days = 31;
        break;
      default:
        System.out.println("You did not enter a valid month number");
    }

    System.out.format("%s in %d had %d days", month_name, year, days);

  }

  /**
   * Checks if the given year is a leap year
   * @param year Year to calculate for
   * @return If the given year is a leap year
   */
  private static boolean isLeapYear(int year) {
    // Leap years must be divisible by 4 and not divisible by 100 unless it is also divisible by 400
    boolean leapYear = year % 4 == 0;
    boolean yearCheck = leapYear && year % 100 != 0;
    leapYear = leapYear && (yearCheck || year % 400 == 0);

    return leapYear;
  }

}
