/**
 * Program to display the first 100 pentagonal numbers
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
public class driver_lab61 {

  /**
   * Main method for lab 6.1
   *
   * @param args This program does not utilize arguments
   */
  public static void main(String[] args) {

    final int[][] table = new int[10][10];

    for (int i = 0; i < 10; ++i) {
      for (int j = 1; j <= 10; ++j) {
        table[i][j - 1] = pentagonalNumber(i * 10 + j);
      }
    }

    for (int[] row : table) {
      System.out.format("%7d%7d%7d%7d%7d%7d%7d%7d%7d%7d\n",
          row[0], row[1], row[2], row[3], row[4], row[5], row[6], row[7], row[8], row[9]);
    }

  }

  /**
   * Returns the next pentagonal number
   *
   * @param n Number to find pentagonal for
   * @return Pentagonal number associated with n
   */
  private static int pentagonalNumber(int n) {
    return n * (3 * n - 1) / 2;
  }

}
