import java.util.*;

/**
 * Program to eliminate duplicate numbers
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
public class driver_lab715 {

    /**
     * Main method for lab 7.15
     *
     * @param args This program does not utilize arguments
     */
    public static void main(String[] args) {

        // Setup input
        Scanner input = new Scanner(System.in);

        // Get the numbers
        System.out.print("Enter 10 numbers: ");
        int[] numbers = Arrays.stream(input.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray();

        // Print distinct array
        System.out.print("The distinct numbers are: ");
        for (int i : eliminateDuplicates(numbers))
            System.out.print(i + " ");

    }

    /**
     * Eliminates duplicate numbers from a list.
     * @param list Array to eliminate duplicates from
     * @return A list with no duplicates
     * @note Javadoc doesn't have a freaking note tag?! C'mon Javadoc...
     */
    private static int[] eliminateDuplicates(int[] list) {
        ArrayList<Integer> printedNumbers = new ArrayList<>();

        for (int number : list)
            if (!printedNumbers.contains(number))
                printedNumbers.add(number);
        // See this is why Java is terrible: Primitive data types are fine, stop trying to be cool, Java.
        return printedNumbers.stream().mapToInt(i->i).toArray();

    }
}
