//package Lab913;

/**
 * Class to store data of a maximal value and its location in a two-dimensional array
 */
public class Location {
  /** row and column of the max value */
  public int row, column;
  /** Max value of the element */
  public double maxValue = 0;

  /**
   * Finds the location of the max value in a 2D matrix
   * @param a Matrix to search
   * @return Location object containing the data of the max value
   */
  public static Location locateLargest(double[][] a) {

    Location location = new Location();

    for (int i = 0; i < a.length; ++i) {
      for (int j = 0; j < a[i].length; ++j) {
        if (a[i][j] > location.maxValue) {
          location.row = i;
          location.column = j;
          location.maxValue = a[i][j];
        }
      }
    }

    return location;

  }

}
