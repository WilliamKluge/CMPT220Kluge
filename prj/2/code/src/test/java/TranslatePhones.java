import java.util.Scanner;
import wavetodec.PhoneConversion;

/**
 * Translates CMU phones to DECtalk phones
 */
public class TranslatePhones {

  public static void main(String[] args) {

    Scanner in = new Scanner(System.in);

    System.out.println("Just keep entering phones, this will keep translating them");

    String input = in.nextLine();

    while (input != null) {

      System.out.println(PhoneConversion.convertCMUPhone(input));
      input = in.nextLine();

    }

  }

}
