/**
 * Program to display the ASCII table from ! to ~
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
 // JA: This is the wrong problem. It was supposed to print the prime factors
 // of a number
public class driver_lab516 {

  /**
   * Main method for lab 5.16
   *
   * @param args This program does not utilize arguments
   */
  public static void main(String[] args) {

    final String[][] table = new String[11][11];

    table[0] = new String[]{" ", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

    for (int i = 1; i <= 10; ++i) {
      // If we are in the first row, set j to be 3, otherwise set it to be 1
      // Stop if j > 10, but if i is in the final row keep j below 7
      int table_row = (i * 10) + 20;
      for (int j = (table_row == 30) ? 3 : 0; j < 10 && (table_row < 120 || j < 7); ++j) {
        table[i][j + 1] = Character.toString((char) (table_row + j));
      }
    }

    for (int i = 1; i <= 10; ++i)
    // Add row markers
    {
      table[i][0] = Integer.toString(i + 2);
    }

    // Cleanup null areas
    table[1][1] = " ";
    table[1][2] = " ";
    table[1][3] = " ";
    table[10][8] = " ";
    table[10][9] = " ";
    table[10][10] = " ";

    for (String[] row :
        table) {
      System.out.format("%-4s%-4s%-4s%-4s%-4s%-4s%-4s%-4s%-4s%-4s%-4s\n", row);
    }

  }

}
