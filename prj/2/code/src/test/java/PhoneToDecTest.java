import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.Context;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.result.WordResult;
import edu.cmu.sphinx.util.TimeFrame;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

/**
 * A simple example that shows how to transcribe a continuous audio file that has multiple
 * utterances in it.
 */
public class PhoneToDecTest {

  /* Number of milliseconds that are allowed between phones before a pause is added */
  private static final int PHONE_GAP_PAUSE = 10;
  /* If all phones should be replaced with "uw"(oo)s */
  private static final boolean BEAT_ONLY = false;
  /* If the program should not wait if the pause is more than 1 second */
  private static final boolean DELETE_LONG_PAUSES = true;
  /* Run with user interaction */
  private static final boolean INTERACTIVE_MODE = true;

  /**
   * Tests phone recognition, pitch detection, and DECtalk conversion
   */
  public static void main(String[] args) throws Exception {

    final String SOURCE_FILE = args[0];

    if (SOURCE_FILE == null) {
      System.out.println("No file argument given, please specify a path to the target file as the"
          + "only argument.");
      return;
    }

    Scanner input = new Scanner(System.in);

    ///// Open audio input stream /////
    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(SOURCE_FILE));
    // Load the entire source audio into a clip
    DataLine.Info info = new DataLine.Info(Clip.class, audioInputStream.getFormat());
    Clip sourceClip;
    sourceClip = (Clip) AudioSystem.getLine(info);
    sourceClip.open(audioInputStream);

    ///// Setup audio playback and file handling /////
    File sourceFile = new File(SOURCE_FILE);
    InputStream stream = new FileInputStream(sourceFile);

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
    // Simple recognition with generic model
    recognizer.allocate();
    context.setSpeechSource(stream, TimeFrame.INFINITE);
    Result result;

    ///// Setup phone collection and file output /////
    String outFileName = "out/DECFiles/" + new SimpleDateFormat("yyyyMMddHHmm").format(new Date())
        + "_out.txt";
    DECPhoneCollection dectalkPhones = new DECPhoneCollection(sourceFile, outFileName);
    PitchAnalysis pitchAnalysis = new PitchAnalysis(SOURCE_FILE);

    ///// Process phone recognition results /////
    while ((result = recognizer.recognize()) != null) {
      SpeechResult speechResult = new SpeechResult(result);
      System.out.format("Hypothesis: %s\n", speechResult.getHypothesis());
      System.out.println("List of recognized words and their times:");

      long lastEndTime = 0; // Time that the last word ended (for inserting pauses)

      // If phrases are going to be supported, add them here
      for (WordResult r : speechResult.getWords()) {
        try {

          DECtalkPhone phone = new DECtalkPhone(r.getWord().toString(), r.getTimeFrame());

          ///// Handle pauses between phones /////
          long thisStartTime = r.getTimeFrame().getStart();

          if (thisStartTime > lastEndTime + PHONE_GAP_PAUSE && !DELETE_LONG_PAUSES) {
            dectalkPhones.addPause(new TimeFrame(lastEndTime + 1, thisStartTime - 1));
          }

          lastEndTime = r.getTimeFrame().getEnd();

          ///// Pitch Analysis ///
          phone.setToneNumber(pitchAnalysis.getDECtalkToneNumber(r.getTimeFrame()));

          ///// Output /////
          if (BEAT_ONLY) {
            // If the phone is not important, replace it with an "uw" (as in boom) sound
            phone.setPhone("uw");
          }

          if (phone.getPhone().equals("")) {
            // If the phone is blank (something that CMU processes but DECtalk does not), skip it
            continue;
          }

          dectalkPhones.addPhone(phone);

        } catch (IndexOutOfBoundsException e) {
          System.out.println(e.getMessage());
        }
      }
    }
    recognizer.deallocate(); // We're done with recognition, get rid of the recognizer

    if (!INTERACTIVE_MODE) {
      // If there should not be user interaction, write the output file and end the program
      dectalkPhones.writeDECtalkFile();
      return;
    }

    ///// Get user input about generated phones /////
    System.out.println("Starting user interaction");
    userInteraction:
    while (dectalkPhones.getCurrentPhoneIndex() < dectalkPhones.getPhoneCount()) {
      // While the current phone's index is less than the total number of phones
      dectalkPhones.playCurrentPhone();

      System.out.println(
          "Did this sound like the phone " + dectalkPhones.getCurrentPhonePronunciation()
              + " (y/n)? ");

      String command = input.nextLine();

      commandInput:
      while (!command.equals("")) { // enter nothing to proceed
        switch (command) {
          case "y": // "y" (yes) go to next phone
            break commandInput;
          case "n": // "n" (no) correct the phone
            System.out.print("Enter the correct phone: ");
            dectalkPhones.setCurrentPhonePronunciation(input.nextLine());
            break commandInput;
          case "pa": // "pa" (play again) play the phone again
            dectalkPhones.playCurrentPhone();
            break;
          case "pn": // "pn" (play next) play the next phone in the collection
            dectalkPhones.playNextPhone();
            break;
          case "ps": // "ps" (play surrounding) play the surrounding phones
            System.out.println("Enter two numbers. First for how many to play back, "
                + "second for in front");
            int backwards = input.nextInt();
            int forwards = input.nextInt();
            dectalkPhones.playSurroundingPhones(backwards, forwards);
            input.nextLine(); // Sloppy way of dealing with nextInt not taking EOL
            break;
          case "bal": // "bal" (balance) balance tones numbers for less jarring jumps in pitch
            dectalkPhones.balanceToneNumbers();
            break;
          case "rt": // "rt" (replace tone) replace all occurrences of a tone with a new one
            System.out.println("Enter two numbers. First is tone to replace, "
                + "second is tone to replace with");
            int oldTone = input.nextInt();
            int newTone = input.nextInt();
            dectalkPhones.fullReplaceTone(oldTone, newTone);
            input.nextLine(); // Sloppy way of dealing with nextInt not taking EOL
            break;
          case "del": // "del" (delete) delete the current phone
            dectalkPhones.removeCurrentPhone();
            break commandInput;
          case "sp": // "sp" (squish previous) squish the current phone's time frame into the
            // previous phone's time frame
            dectalkPhones.squishWithPrevious();
            break commandInput;
          case "restart": // "restart" start again from the beginning
            dectalkPhones.restart();
            break commandInput;
          case "goto": // "goto" move currentPhoneIndex to a new value
            System.out.println("Enter a new index to go to");
            int gotoPhone = input.nextInt();
            dectalkPhones.setCurrentPhoneIndex(gotoPhone);
            input.nextLine(); // Eat EOL from nextInt()
            break commandInput;
          case "w": // "w" (write) write the output file, but continue editing
            dectalkPhones.clearOutputFile();
            dectalkPhones.writeDECtalkFile();
            break;
          case "wq":  // "wq" (write quit) Be done editing phones
            dectalkPhones.clearOutputFile();
            dectalkPhones.writeDECtalkFile();
            break userInteraction;
          case "save": // "save" save the current progress to the output file
            dectalkPhones.clearOutputFile();
            dectalkPhones.saveProgress();
            break;
          case "load": // "load" load a save file from a previous session
            System.out.print("Enter the path of the file to load: ");
            dectalkPhones.loadProgress(input.nextLine());
            break;
          default:
            System.out.println("Command not recognized, try again");
            break;
        }
        command = input.nextLine();
      }

      dectalkPhones.goToNextPhone();

    } // End user interaction while loop

    dectalkPhones.writeDECtalkFile();

    dectalkPhones.closeOutput();

  } // End method main

} // End class PhoneToDecTest