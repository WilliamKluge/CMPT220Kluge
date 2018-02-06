/**
 * Program to display a conversion chart of kilograms to pounds
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
public class driver_lab55 {

  /**
   * Main method for lab 5.5
   *
   * @param args This program does not utilize arguments
   */
  public static void main(String[] args) {

    final Object[][] table = new String[200][];

    table[0] = new String[]{"Kilograms", "Pounds", "Pounds", "Kilograms"};

    for (int k = 1, i = 1, j = 20; k < 200; ++k, i += 2, j += 5) {
      double pounds = (double) Math.round(i * 2.2 * 100000d) / 100000d;
      double kilograms = (double) Math.round(j / 2.2 * 100000) / 100000;
      table[k] = new String[]{Integer.toString(i), Double.toString(pounds),
          Integer.toString(j), Double.toString(kilograms)};
    }

    for (Object[] row :
        table) {
      System.out.format("%-10s%-6s | %-7s%s\n", row);
    }

  }

}
