package wavetodec;

import java.util.HashMap;
import java.util.Map;

/**
 * Converts phonemes from CMU (sphinx) to DECtalk
 * Note: This class is a simple Java implementation of a Python script found here:
 * http://people.ds.cam.ac.uk/ssb22/gradint/lexconvert.py
 */
public class PhoneConversion {

  /**
   * Map of all phones going OUT of CMU. That means this only uses entries from the original script
   * following the format (string, phoneme) and (string, phoneme, False) as we only need the phones
   * coming out of CMU.
   *
   * This is in the reverse order of DECtalkPhones because here we need phone->key as opposed to
   * key->phone
   */
  public static final Map<String, String> CMUPhones;
  static {
    CMUPhones = new HashMap<>();
    /*### These are the phones from the original script, edited to fit this format ###*/
    // Format of the US-English Carnegie Mellon University Pronouncing Dictionary
    // Contributed by Jan Weiss
    // http://www.speech.cs.cmu.edu/cgi-bin/cmudict
    CMUPhones.put("0", "syllable_separator");
    CMUPhones.put("1", "primary_stress");
    CMUPhones.put("2", "secondary_stress");
    CMUPhones.put("AA", "a_as_in_ah");
    // (var1_a_as_in_ah,"2",False)
    // (ipa_colon,"1",False),
    CMUPhones.put("AE", "a_as_in_apple");
    CMUPhones.put("AH", "u_as_in_but");
    // (o_as_in_orange,"AA",False),
    CMUPhones.put("AW", "o_as_in_now");
    // (a_as_in_ago,"AH",False),
    CMUPhones.put("ER", "e_as_in_herd"); // TODO: check this one
    CMUPhones.put("AY", "eye");
    CMUPhones.put("B", "b");
    CMUPhones.put("CH", "ch");
    CMUPhones.put("D", "d");
    CMUPhones.put("DH", "th_as_in_them");
    CMUPhones.put("EH", "e_as_in_them");
    // (ar_as_in_year,"ER",False),
    //  (a_as_in_air,"ER",False),
    CMUPhones.put("EY", "a_as_in_ate");
    CMUPhones.put("F", "f");
    CMUPhones.put("G", "g");
    CMUPhones.put("HH", "h");
    CMUPhones.put("IH", "i_as_in_it");
    CMUPhones.put("EY AH", "ear");
    CMUPhones.put("IY", "e_as_in_eat");
    CMUPhones.put("JH", "j_as_in_jump");
    CMUPhones.put("K", "k");
    CMUPhones.put("L", "l");
    CMUPhones.put("M", "m");
    CMUPhones.put("N", "n");
    CMUPhones.put("NG", "ng");
    CMUPhones.put("OW", "o_as_in_go");
    CMUPhones.put("OY", "oy_as_in_toy");
    CMUPhones.put("P", "p");
    CMUPhones.put("R", "r");
    CMUPhones.put("S", "s");
    CMUPhones.put("SH", "sh");
    CMUPhones.put("T", "t");
    CMUPhones.put("TH", "th_as_in_think");
    CMUPhones.put("UH", "oor_as_in_poor");
    CMUPhones.put("UW", "oo_as_in_food");
    CMUPhones.put("AO", "close_to_or");
    CMUPhones.put("V", "v");
    CMUPhones.put("W", "w");
    CMUPhones.put("Y", "y");
    CMUPhones.put("Z", "z");
    CMUPhones.put("ZH", "ge_of_blige_etc");
    // # lex_filename not set (does CMU have a lex file?)
    // safe_to_drop_characters=True, # TODO: really?
    /*### These are phones I have added ###*/
    CMUPhones.put("SIL", "ignore"); // These just surround the transcriptions, they aren't needed
  }

  /**
   * Map of all phones going IN to DECtalk. That means from the original Python script this only
   * uses the (string, phoneme) and (phoneme, string, False) as we only need to convert to DECtalk,
   * not from.
   *
   * This is in the reverse order of CMUPhones because here we need key->phone as opposed to
   * phone->key
   */
  public static final Map<String, String> DECtalkPhones;
  static {
    DECtalkPhones = new HashMap<>();
    /*### These are the phones from the original script, edited to fit this format ###*/
    // DECtalk hardware synthesizers (American English) (1984-ish serial port; later ISA cards)
    // (syllable_separator,'',False),
    DECtalkPhones.put("primary_stress", "'");
    DECtalkPhones.put("o_as_in_orange", "aa");
    DECtalkPhones.put("a_as_in_apple", "ae");
    DECtalkPhones.put("u_as_in_but", "ah");
    DECtalkPhones.put("close_to_or", "ao"); // bought
    DECtalkPhones.put("o_as_in_now", "aw");
    DECtalkPhones.put("a_as_in_ago", "ax");
    DECtalkPhones.put("eye", "ay");
    DECtalkPhones.put("b", "b");
    DECtalkPhones.put("ch", "ch");
    DECtalkPhones.put("d", "d");
    // ("dx",d,False),
    DECtalkPhones.put("th_as_in_them", "dh");
    DECtalkPhones.put("e_as_in_them", "eh");
    // ("el",l,False), # -le of bottle, allophone ?
    // # TODO: en: -on of button (2 phonemes?)
    DECtalkPhones.put("a_as_in_ate", "ey");
    DECtalkPhones.put("f", "f");
    DECtalkPhones.put("g", "g");
    DECtalkPhones.put("h", "hx");
    DECtalkPhones.put("i_as_in_it", "ih");
    // ("ix",i_as_in_it,False),
    // ("q",e_as_in_eat,False),
    DECtalkPhones.put("e_as_in_eat", "iy");
    DECtalkPhones.put("j_as_in_jump", "jh");
    DECtalkPhones.put("k", "k");
    DECtalkPhones.put("l", "l");
    // ("lx",l,False)
    DECtalkPhones.put("m", "m");
    DECtalkPhones.put("n", "n");
    DECtalkPhones.put("ng", "nx");
    DECtalkPhones.put("o_as_in_go", "ow");
    DECtalkPhones.put("oy_as_in_toy", "oy");
    DECtalkPhones.put("p", "p");
    DECtalkPhones.put("r", "r");
    //  ("rx",r,False),
    // ("rr",e_as_in_herd),
    DECtalkPhones.put("s", "s");
    DECtalkPhones.put("sh", "sh");
    DECtalkPhones.put("t", "t");
    // ("tx",t,False),
    DECtalkPhones.put("th_as_in_think", "th");
    DECtalkPhones.put("opt_u_as_in_pull", "uh");
    DECtalkPhones.put("oo_as_in_food", "uw");
    DECtalkPhones.put("v", "v");
    DECtalkPhones.put("w", "w");
    DECtalkPhones.put("y", "yx");
    DECtalkPhones.put("z", "z");
    DECtalkPhones.put("ge_of_blige_etc", "zh");
    DECtalkPhones.put("ear", "ihr"); // # DECtalk makes this from ih + r
    // approximate_missing=True,
    //  cleanup_regexps=[('yxuw','yu')], # TODO: other allophones ("x',False" stuff above)?
    //  cvtOut_regexps=[('yu','yxuw')],
    //      # lex_filename not set (depends on which model etc)
    //  stress_comes_before_vowel=True,
    //  safe_to_drop_characters=True, # TODO: really?
    //  word_separator=" ",phoneme_separator="",
    //  inline_header="[:phoneme on]\n",
    //  inline_format="[%s]",
    /*### These are phones I have added ###*/
    DECtalkPhones.put("ignore", ""); // Anything from CMU that can just be ignored
    DECtalkPhones.put("oor_as_in_poor", "ur"); // TODO test
    DECtalkPhones.put("e_as_in_herd", "ir"); // TODO test, probably need something better
    DECtalkPhones.put("a_as_in_ah", "aa");
  }

  /**
   * Checks if the given phone is DECtalk compatible.
   *
   * Note: Currently this always returns true. TODO finish possibility checking
   * @param phone Phone to check
   * @return If the given phone is valid in DECtalk
   */
  public static boolean isDECPhone(String phone) {
    return true; // TODO finish this to check all possible substrings
//    for (int from = 0; from < phone.length(); from++) {
//      for (int to = from + 1; to <= phone.length(); to++) {
//        if (DECtalkPhones.containsValue(phone.substring(from, to))) {
//          return true;
//        }
//      }
//    }
//    return false;
  }

  /**
   * Converts a CMU phone to a DECtalk phone
   * @param phone Phone to convert
   * @return DECTalk version of the phone
   * @throws IndexOutOfBoundsException If there is no CMU or DECtalk phone for the input. The
   * message of the exception details what caused the issue. To solve this you only need to add
   * the phones to the dictionary (look it up and test)
   */
  public static String convertCMUPhone(String phone) {

    String commonKey = CMUPhones.get(phone);

    if (commonKey == null)
      throw new IndexOutOfBoundsException("There is no CMU phone for the phone: " + phone);

    String DECtalkPhone = DECtalkPhones.get(commonKey);

    if (DECtalkPhone == null)
      throw new IndexOutOfBoundsException("There is no DECtalk phone for the phone: "
          + phone + "(" + commonKey + ")");

    return DECtalkPhone;

  }

}
