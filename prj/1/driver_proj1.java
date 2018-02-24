import java.util.Arrays;
import java.util.Scanner;

/**
 * Program to solve https://open.kattis.com/problems/a1paper
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
public class driver_proj1 {

  /**
   * Main method for the A! paper program
   * @param args This program does not use command line arguments
   */
  public static void main(String args[]) {
    // Setup input
    Scanner input = new Scanner(System.in);

    // Get the minimum paper size he has (done this way to consume the EOL character)
    int minPaperSize = Integer.parseInt(input.nextLine());
    // Get the amount of each paper size he has, starting with A2
    int[] paperCounts = Arrays.stream(input.nextLine().split(" "))
        .mapToInt(Integer::parseInt).toArray();

    double tapeLength = 0;

    int index = paperCounts.length - 1;

    if (neededPieces(paperCounts, paperCounts.length - 1) > 0) {
      System.out.println("impossible");
      return;
    }

    for (int i = index; i > 0 && paperCounts[i] >= 2; --i) {
      int takenPieces = paperCounts[i] - (neededPieces(paperCounts, i) * -1);
      paperCounts[i - 1] += takenPieces / 2;
      paperCounts[i] -= takenPieces;
      tapeLength += calculateLongSideLength(i + 2) * (takenPieces / 2);
    }

    if (paperCounts[0] >= 2) {
      tapeLength += calculateLongSideLength(2);
      System.out.println(tapeLength);
    }

  }

  /**
   * Calculates the pieces needed of the next tier to complete the paper
   * @param pieces Array of pieces of paper to work with
   * @param index Index of pieces to start at
   * @return The pieces of the current size needed to finish the paper
   */
  private static int neededPieces(int[] pieces, int index) {

    int[] missingPieces = new int[index + 1];

    missingPieces[0] = 2 - pieces[0];

    for (int i = 1; i < missingPieces.length; ++i)
      missingPieces[i] = (missingPieces[i - 1] * 2) - pieces[i];

    return missingPieces[index];

  }

  /**
   * Calculates the long side length of a specified size of paper using the An standard
   *
   * @param paperSize Size of paper to calculate for
   * @return The length of the long side for the specified size
   */
  private static double calculateLongSideLength(int paperSize) {
    double currentLargestSide = Math.pow(2, -3.0 / 4);
    for (int i = 3; i <= paperSize; ++i)
      currentLargestSide *= Math.sqrt(0.5);

    return currentLargestSide;
  }

}
