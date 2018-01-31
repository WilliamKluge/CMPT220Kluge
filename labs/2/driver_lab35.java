import java.util.Scanner;

/**
 * Program to check what day of the week it will be x days from the present day
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
public class driver_lab35 {

  /**
   * Main function for the driver_lab35 program
   *
   * @param args Arguments are not implemented in this program
   */
  public static void main(String[] args) {
    // Setup input
    Scanner input = new Scanner(System.in);

    // Prompt user and get input
    System.out.print("Enter the current day of the week represented by a positive integer: ");
    int day = input.nextInt();
    System.out.print("Enter the number of days to calculate forward: ");
    int days_forward = input.nextInt();

    // Calculate the result
    String currentDay = calculateDay(day);
    String futureDay = calculateDay((day + days_forward) % 7);

    // Show the result
    System.out.format("Today is %s and the future day is %s", currentDay, futureDay);

  }

  /**
   * Method to determine the day of the week based on an integer
   *
   * @param day Integer representing the day of the week (0-6)
   * @return String representing the day of the week
   */
  static private String calculateDay(int day) {

    String current_day = "";
    switch (day) {
      case 0:
        current_day = "Sunday";
        break;
      case 1:
        current_day = "Monday";
        break;
      case 2:
        current_day = "Tuesday";
        break;
      case 3:
        current_day = "Wednesday";
        break;
      case 4:
        current_day = "Thursday";
        break;
      case 5:
        current_day = "Friday";
        break;
      case 6:
        current_day = "Saturday";
        break;
      default:
        System.out.println("You did not enter a valid integer to represent the day of the week. "
            + "Please run the program again, this time entering an integer 0-6.");
    }

    return current_day;

  }

}
