import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.Context;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.result.WordResult;
import edu.cmu.sphinx.util.TimeFrame;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Uses lyrics to find the time of each word, then takes the phones in that time frame and matches/
 * smashes them to fit the word properly in that frame.
 */
public class LyricPhoneTimeMatch {
  /* Number of milliseconds that are allowed between phones before a pause is added */
  private static final int PHONE_GAP_PAUSE = 10;
  /* If the program should not wait if the pause is more than 1 second */
  private static final boolean DELETE_LONG_PAUSES = true;

  public static void main(String[] args) throws IOException, UnsupportedAudioFileException {

    // Audio file to work with
    final String TEST_AUDIO_FILE = args[0];
    // Lyrics file to work with
    final String TEST_LYRICS_FILE = args[1];

    List<WordResult> results = MatchWithLyrics.getWordAlignment(TEST_LYRICS_FILE, TEST_AUDIO_FILE);

    final String TEST_FILE = args[0];

    if (TEST_FILE == null) {
      System.out.println("No file argument given, please specify a path to the target file as the"
          + "only argument.");
      return;
    }

    Scanner input = new Scanner(System.in);

    ///// Setup Sphinx /////

    System.out.println("Loading models...");

    Configuration configuration = new Configuration();

    // Load model from the jar
    configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");

    // You can also load model from folder
    // configuration.setAcousticModelPath("file:en-us");

    configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
    Context context = new Context(configuration);
    context.setLocalProperty("decoder->searchManager", "allphoneSearchManager");
    Recognizer recognizer = context.getInstance(Recognizer.class);
    InputStream stream = new FileInputStream(new File(TEST_FILE));
    //stream.skip(44);

    // Simple recognition with generic model
    recognizer.allocate();
    context.setSpeechSource(stream, TimeFrame.INFINITE);
    Result result;

    { // Make the directory for output if it doesn't exist
      File directory = new File("out/DECFiles/");
      if (! directory.exists()){
        directory.mkdirs();
        // If you require it to make the entire directory path including parents,
        // use directory.mkdirs(); here instead.
      }
    }

    // Writes to dectalk file
    String fileName = "out/DECFiles/" + new SimpleDateFormat("yyyyMMddHHmm").format(new Date())
        + "_out.txt";
    BufferedWriter DECtalkFile = new BufferedWriter(new FileWriter(fileName, true));
    DECtalkFile.write("[:phoneme on]\n");

    while ((result = recognizer.recognize()) != null) {
      DECtalkFile.write("[");
// TODO check timeframes of words and see if the result here is in that timeframe than ask about replacement
      SpeechResult speechResult = new SpeechResult(result);
      System.out.format("Hypothesis: %s\n", speechResult.getHypothesis());
      System.out.println("List of recognized words and their times:");

      PitchAnalysis pitchAnalysis = new PitchAnalysis(TEST_FILE);

      long lastEndTime = 0; // Time that the last word ended (for inserting pauses)

      for (WordResult r : speechResult.getWords()) {
        try {

          // Pronunciation

          String pronunciation = r.getWord().toString();

          // Timing

          long timeLength = r.getTimeFrame().length();

          long thisStartTime = r.getTimeFrame().getStart();

          if (thisStartTime > lastEndTime + PHONE_GAP_PAUSE && !DELETE_LONG_PAUSES) {
            DECtalkFile.write("_<" + (thisStartTime - lastEndTime) + ">");
          }

          lastEndTime = r.getTimeFrame().getEnd();

          // Pitch Analysis

          int toneNumber = pitchAnalysis.getDECtalkToneNumber(r.getTimeFrame());

          // Ask user about clip



          // Output

          String DECPronunciation = PhoneConversion.convertCMUPhone(pronunciation);

          if (DECPronunciation.equals("")) {
            continue;
          }

          String DECtalkCode = DECPronunciation + "<" + timeLength + "," + toneNumber + ">";
          DECtalkFile.write(DECtalkCode);
          //System.out.println(DECtalkCode);
        } catch (IndexOutOfBoundsException e) {
          System.out.println(e.getMessage());
        }
        //System.out.println(r);
      }
      DECtalkFile.append("]\n"); // Closes the [ from the first print statement
    }
    DECtalkFile.close();
    recognizer.deallocate();

  }

}
