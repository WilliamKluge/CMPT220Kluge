public class GenerateSpread {

  public static void main(String[] args) {

    String[] voices = new String[] {"p", "h", "f", "d", "b", "u", "w", "r", "k", "v"};

    System.out.println("[:phoneme on]");

    for (int i = 1; i <= 37; ++i) {

      for (String voice : voices) {
        System.out.println("[:n" + voice + "]\n"
            + "[duw<500," + i + ">]");
      }

    }

  }

}
