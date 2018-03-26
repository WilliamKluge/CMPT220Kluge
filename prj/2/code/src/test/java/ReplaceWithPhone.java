import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * Replaces the "uw"s of a file with a phone
 */
public class ReplaceWithPhone {

  public static void main(String args[]) {

    ///// Read lyrics from file /////

    InputStream lyricsFile;
    try {
      lyricsFile = new FileInputStream("out/DECFiles/201803252024_out.txt");
    } catch (FileNotFoundException e) {
      System.out.println("Lyrics file not found");
      return;
    }

    BufferedReader buf = new BufferedReader(new InputStreamReader(lyricsFile));

    StringBuilder lyrics = new StringBuilder();

    try {
      String in = buf.readLine();
      while (in != null) {
        lyrics.append(in).append("\n");
        in = buf.readLine();
      }
      buf.close();
      lyricsFile.close();
    } catch (IOException e) {
      System.out.println("Could not read line");
      e.printStackTrace();
      return;
    }

    String lyricString = lyrics.toString();

    Scanner in = new Scanner(System.in);

    System.out.println("Enter one line with all phones of the song in it.");

    String input = in.nextLine();

    String[] splitArray = input.split("\\s+");

    for (String phone : splitArray) {
      // Yeah this is inefficient, whatever
      lyricString = lyricString.replaceFirst("uw", PhoneConversion.convertCMUPhone(phone));
    }

    System.out.println(lyricString);

  }

}
