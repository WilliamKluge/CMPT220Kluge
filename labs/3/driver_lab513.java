/**
 * Program to find the largest number n so that n^3 < 12000
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
public class driver_lab513 {

  /**
   * Main method for lab 5.13
   *
   * @param args This program does not utilize arguments
   */
  public static void main(String[] args) {

    int n = 12000;

    while (Math.pow(n, 3) >= 12000) {
      n--;
    }

    System.out.format("The largest number n such that n^3 < 1200 is %d", n);

  }

}
