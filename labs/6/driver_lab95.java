import java.util.GregorianCalendar;

/**
 * Program to display the current date and a modified date
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
public class driver_lab95 {

  /**
   * Main method for lab 9.5
   *
   * @param args This program does not utilize arguments
   */
  public static void main(String[] args) {

    GregorianCalendar gregorianCalendar = new GregorianCalendar();
    displayDate(gregorianCalendar);
    gregorianCalendar.setTimeInMillis(1234567898765L);
    displayDate(gregorianCalendar);

  }

  /**
   * Displays the date of a GregorianCalendar object
   *
   * @param gc Object to display the date with
   */
  private static void displayDate(GregorianCalendar gc) {

    System.out.format("Current Date: %d %d %d\n", gc.get(GregorianCalendar.YEAR),
        gc.get(GregorianCalendar.MONTH), gc.get(GregorianCalendar.DAY_OF_MONTH));

  }
}
