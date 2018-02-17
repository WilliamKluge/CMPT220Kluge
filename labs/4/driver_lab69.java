/**
 * Program to convert from feet to meters
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
public class driver_lab69 {

    /**
     * Main method for lab 6.9
     *
     * @param args This program does not utilize arguments
     */
    public static void main(String[] args) {

        final Object[][] table = new String[11][];

        table[0] = new String[]{"Feet", "Meters", "Meters", "Feet"};

        for (int k = 1, i = 1, j = 20; k < 11; ++k, ++i, j += 5) {
            table[k] = new String[]{Integer.toString(i), Double.toString(footToMeters(i)),
                    Integer.toString(j), Double.toString(metersToFoot(j))};
        }

        for (Object[] row :
                table) {
            System.out.format("%-10s%-6s | %-7s%s\n", row);

        }
    }

    /**
     * Convert from imperial to
     * @param foot Amount of feet to convert
     * @return Da meters
     */
    private static double footToMeters(double foot) {

        return 0.305 * foot;

    }

    /**
     * Convert from meters to feet
     * @param meters Amount of meters to convert
     * @return Converted meters to imperial
     */
    private static double metersToFoot(double meters){

        return 3.279 * meters;

    }
}
