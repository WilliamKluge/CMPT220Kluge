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
     * @return
     */
    private static boolean isValid(double side1, double side2, double side3) {

        boolean valid = true;

        valid = side1 + side2 > side3;
        valid = valid & side2 + side3 > side1;

        return valid & side1 + side3 > side2;

    }

    /**
     *
     * @param side1
     * @param side2
     * @param side3
     * @return
     */
    private static double area(double side1, double side2, double side3) {

        double height = Math.sqrt(Math.pow(side1/2, 2) + Math.pow(side2, 2));

        return side3 * height;

    }
}
