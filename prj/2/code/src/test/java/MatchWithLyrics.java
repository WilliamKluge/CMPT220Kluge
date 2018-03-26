
import edu.cmu.sphinx.alignment.LongTextAligner;
import edu.cmu.sphinx.api.SpeechAligner;
import edu.cmu.sphinx.result.WordResult;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates DECtalk code from matching lyrics to a song and using the pitches at time to finish the
 * codes
 */
public class MatchWithLyrics {

  public static void main(String[] args) throws IOException {
    // Audio file to work with
    final String TEST_AUDIO_FILE = args[0];
    // Lyrics file to work with
    final String TEST_LYRICS_FILE = args[1];

    ///// Read lyrics from file /////

    InputStream lyricsFile;
    try {
      lyricsFile = new FileInputStream(TEST_LYRICS_FILE);
    } catch (FileNotFoundException e) {
      System.out.println("Lyrics file not found");
      return;
    }

    BufferedReader buf = new BufferedReader(new InputStreamReader(lyricsFile));

    String lyrics;

    try {
      lyrics = buf.readLine();
      buf.close();
      lyricsFile.close();
    } catch (IOException e) {
      System.out.println("Could not read line");
      e.printStackTrace();
      return;
    }

    ///// Align speech /////

    SpeechAligner aligner = new SpeechAligner("resource:/edu/cmu/sphinx/models/en-us/en-us",
        "resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict",
        null);
    List<WordResult> results = aligner.align(new URL(TEST_AUDIO_FILE), lyrics);
    List<String> stringResults = new ArrayList<String>();
    for (WordResult wr : results) {
      stringResults.add(wr.getWord().getSpelling());
    }

    LongTextAligner textAligner = new LongTextAligner(stringResults, 2);
    List<String> sentences = aligner.getTokenizer().expand(lyrics);
    List<String> words = aligner.sentenceToWords(sentences);

    int[] aid = textAligner.align(words);

    int lastId = -1;
    for (int i = 0; i < aid.length; ++i) {
      if (aid[i] == -1) {
        System.out.format("- %s\n", words.get(i));
      } else {
        if (aid[i] - lastId > 1) {
          for (WordResult result : results.subList(lastId + 1,
              aid[i])) {
            System.out.format("+ %-25s [%s]\n", result.getWord()
                .getSpelling(), result.getTimeFrame());
          }
        }
        System.out.format("  %-25s [%s]\n", results.get(aid[i])
            .getWord().getSpelling(), results.get(aid[i])
            .getTimeFrame());
        lastId = aid[i];
      }
    }

    if (lastId >= 0 && results.size() - lastId > 1) {
      for (WordResult result : results.subList(lastId + 1, results.size())) {
        System.out.format("+ %-25s [%s]\n", result.getWord().getSpelling(), result.getTimeFrame());
      }
    }

  }

  /**
   * Gets the word alignment matching lyrics to an audio file
   * @param lyricFilePath
   * @param audioFilePath
   * @return
   * @throws IOException
   */
  public static List<WordResult> getWordAlignment(String lyricFilePath, String audioFilePath)
      throws IOException {

    ///// Read lyrics from file /////

    InputStream lyricsFile;
    lyricsFile = new FileInputStream(lyricFilePath);

    BufferedReader buf = new BufferedReader(new InputStreamReader(lyricsFile));

    String lyrics;

    lyrics = buf.readLine();
    buf.close();
    lyricsFile.close();


    ///// Align speech /////

    SpeechAligner aligner = new SpeechAligner("resource:/edu/cmu/sphinx/models/en-us/en-us",
        "resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict",
        null);
    List<WordResult> results = aligner.align(new URL(audioFilePath), lyrics);

    return results;

  }

}
