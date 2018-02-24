import java.util.Arrays;

/**
 * Program to find points in a three-dimensional space nearest to each other.
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
public class driver_lab87 {

    /**
     * Main method for lab 8.7
     *
     * @param args This program does not utilize arguments
     */
    public static void main(String[] args) {
        // Given in the problem
        double[][] points = {{-1, 0, 3}, {-1, -1, -1}, {4, 1, 1}, {2, 0.5, 9}, {3.5, 2, -1}, {3, 1.5, 3}, {-1.5, 4, 2},
                {5.5, 4, -0.5}};
        double[] point1 = {};
        double[] point2 = {};
        // Initialize with real value, repeat calculation doesn't matter
        double shortestDistance = distance(points[1], points[2]);

        for (int i = 0; i < points.length; ++i)
            for (int j = i + 1; j < points.length; ++j) {
                double calculatedDistance = distance(points[i], points[j]);

                if (calculatedDistance < shortestDistance) {
                    point1 = points[i];
                    point2 = points[j];
                    shortestDistance = calculatedDistance;
                }
            }

        System.out.format("The two points with the shortest distance are %s and %s with a distance of %f",
                Arrays.toString(point1), Arrays.toString(point2), shortestDistance);

    }

    /**
     * Calculates the distance between two points
     * @param array1 First point
     * @param array2 Second point
     * @return Distance between the first and second point
     */
    private static double distance(double[] array1, double[] array2) {

        return Math.sqrt(Math.pow(array2[0] - array1[1], 2) + Math.pow(array2[1] - array1[1], 2) +
                Math.pow(array2[2] - array1[2], 2));

    }
}
