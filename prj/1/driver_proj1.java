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
    int[] combinedPieces = new int[paperCounts.length];

    double tapeLength = 0;
    boolean enoughPaper = false;

    int index = 1;

    while (paperCounts[0] < 2 && index < paperCounts.length) {
      while (paperCounts[index] >= 2 && paperCounts[0] < 2) {
        tapeLength += promoteNext(paperCounts, index);
      }
      ++index;
    }

//    // Iterate through the amounts of paper he has
//    for (int i = minPaperSize - 2; i >= 0; --i) {
//      if (paperCounts[i] + combinedPieces[i] < 2)
//        continue;
//
//      int combinations = (paperCounts[i] + combinedPieces[i]) / 2;
//      tapeLength += calculateLongSideLength(i + 2) * ((i == 0) ? 1 : combinations);
//
//      if (i != 0) {
//        // Remove pieces that were combined to next size
//        subtractFromArrays(paperCounts, combinedPieces, i, combinations * 2);
//        combinedPieces[i - 1] += combinations; // Add the combined pieces
//      }
//
//    }
//
//    if (paperCounts[0] + combinedPieces[0] >= 2) {
//      // If we're at 1 = 0 and there are 2 or more pieces of A2 paper
//      enoughPaper = true;
//      subtractFromArrays(paperCounts, combinedPieces, 0, 2);
//      tapeLength -= calculateLongSideLength(3) * combinedPieces[0];
//    }
//    // If we combine a small piece into a large piece then account that it
//    // only factors in one set of tape usage
//    for (int i = 0; i < paperCounts.length; ++i)
//      // Subtract the extra tape used to make unneeded pieces
//      tapeLength -= calculateLongSideLength(i + 3) * combinedPieces[i];
//
    if (paperCounts[0] >= 2) {
      tapeLength += calculateLongSideLength(2);
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

  private static void subtractFromArrays(int[] array1, int[] array2, int index, int num) {

    if (array2[index] >= num) {
      array2[index] -= num;
    } else {
      array1[index] -= num - array2[index];
      array2[index] = 0;
    }

  }

  private static double promoteNext(int[] pieces, int index) {

    double tapeUsed = 0;

    if (index < pieces.length && index > 0 && pieces[index] >= 2) {
      pieces[index - 1] += 1;
      pieces[index] -= 2;
      tapeUsed += calculateLongSideLength(index + 2);
      tapeUsed += promoteNext(pieces, index - 1);
    }

    return tapeUsed;

  }

}
