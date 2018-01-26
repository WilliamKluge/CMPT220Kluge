/**
 * Main class for Lab 1.4
 *
 * @author William Kluge
 * Contact: klugewilliam@gmail.com
 */
public class driver_lab14 {
    /**
     * Main method for Lab 1.4
     *
     * @param args This program does not use arguments
     */
    public static void main(String[] args) {
        System.out.println("a\ta^2\ta^3");
        for (int a = 1; a <= 4; ++a) {
            System.out.println(a + "\t" + a * a + "\t" + a * a * a);
        }
    }

}
