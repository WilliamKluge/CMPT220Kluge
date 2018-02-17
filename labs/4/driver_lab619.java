import java.util.Scanner;

/**
 * Program to
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
public class driver_lab619 {

    /**
     * Main method for lab 6.19
     *
     * @param args This program does not utilize arguments
     */
    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);
        double one = input.nextDouble();
        double two = input.nextDouble();
        double three = input.nextDouble();

        if (isValid(one, two, three))
            System.out.println(area(one, two, three));

    }

    /**
     * Tests if the triangle works
     * @param side1 First side of the triangle
     * @param side2 Second side of the triangle
     * @param side3 Third side of the triangle
     * @return If the triangle is valid
     */
    private static boolean isValid(double side1, double side2, double side3) {

        boolean valid = true;

        valid = side1 + side2 > side3;
        valid = valid & side2 + side3 > side1;

        return valid & side1 + side3 > side2;

    }

    /**
     * Finds the area of the triangle
     * @param side1 First side of triangle
     * @param side2 Second side of triangle
     * @param side3 Third side of triangle
     * @return Area of triangle
     */
    private static double area(double side1, double side2, double side3) {

        return 0.5 * side1 * side2 * Math.sin(side3);

    }
}
