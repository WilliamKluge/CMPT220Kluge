/**
 * Program to show a table of some arbitrary calculation
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
public class driver_lab613 {

    /**
     * Main method for lab 6.13
     *
     * @param args This program does not utilize arguments
     */
    public static void main(String[] args) {

        final Object[][] table = new String[21][];

        table[0] = new String[]{"i", "m(i)"};

        for (int i = 1; i < 21; ++i) {
            table[i] = new String[]{Integer.toString(i), Double.toString(m(i))};
        }

        for (Object[] row :
                table) {
            System.out.format("%-4s|%6s\n", row);

        }
    }

    /**
     * Program to calculate the sum of all things leading up to the other thing divided by themself + 1
     * @param number Number to calculate for
     * @return The sum
     */
    private static double m(int number) {
        double sum = 0;

        for (int i = 1; i <= number; ++i)
            sum += i / (i + 1.0);

        return sum;
    }
}
