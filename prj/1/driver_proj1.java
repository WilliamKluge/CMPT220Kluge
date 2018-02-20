import java.util.Arrays;
import java.util.Scanner;

/**
 * Program to solve https://open.kattis.com/problems/a1paper
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
public class driver_proj1 {

  public static void main(String args[]) {
    // Setup input
    Scanner input = new Scanner(System.in);

    // Get the minimum paper size he has (done this way to consume the EOL character)
    int minPaperSize = Integer.parseInt(input.nextLine());
    // Get the amount of each paper size he has, starting with A2
    int[] paperCounts = Arrays.stream(input.nextLine().split(" "))
        .mapToInt(Integer::parseInt).toArray();

    double tapeLength = 0;

    // Iterate through the amounts of paper he has
    for (int i = minPaperSize - 2; i >= 0; --i) {
      int combinedPieces = (i == 0) ? 1 : paperCounts[i] / 2;
      tapeLength += calculateLongSideLength(i + 2) * combinedPieces;

      paperCounts[i] -= combinedPieces * 2;

      if (i != 0)
        paperCounts[i - 1] += combinedPieces;

    }

    // Account for extra pieces
    for (int i = 1; i < paperCounts.length; ++i) {

    }

    if (paperCounts[0] >= 2) {
      System.out.println(tapeLength);
    } else {
      System.out.println("impossible");
    }

  }

  /**
   * Calculates the long side length of a specified size of paper using the An standard
   *
   * @param paperSize Size of paper to calculate for
   * @return The length of the long side for the specified size
   */
  private static double calculateLongSideLength(int paperSize) {
    double currentLargestSide = Math.pow(2, -3.0 / 4);
    for (int i = 3; i <= paperSize; ++i) {
      currentLargestSide *= Math.sqrt(0.5);
    }

    return currentLargestSide;
  }

}
