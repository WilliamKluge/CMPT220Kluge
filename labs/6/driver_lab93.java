import java.util.Date;

/**
 * Program to practice using the Date class
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
public class driver_lab93 {

  /**
   * Main method for lab 9.3
   *
   * @param args This program does not utilize arguments
   */
  public static void main(String[] args) {

    for (Date date = new Date(10000); date.getTime() <= Math.pow(10, 11);
        date.setTime(date.getTime() * 10)) {
      System.out.format("Elapsed Time: %d Date: %s\n", date.getTime(), date.toString());
    }

  }
}
