import edu.cmu.sphinx.util.TimeFrame;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Handles the organization of phones and controls the user's edits to auto-generated results.
 *
 * This class is not responsible for handling user input, however it is responsible for DECtalk file
 * output.
 */
public class DECPhoneCollection {

  /*
   * Index of the phone currently being worked with.
   *
   * Set up like this so that this object does not need to handle any looping and outside methods
   * can preform iteration checks knowing what the index currently is without worrying about the
   * amount of phones changing.
   */
  private int currentPhoneIndex;
  /* Array of phones that take place in the source audio in sequential order */
  private ArrayList<DECtalkPhone> dectalkPhones;
  /* BufferedWriter to handle output of DECtalk file */
  private BufferedWriter DECtalkFile;
  /* Handles playback of source audio */
  private SourceAudioPlayer sourceAudioPlayer;

  /**
   * Constructor for DECPhoneCollection
   *
   * @param outputFileName Name and path of the file to output DECtalk to
   */
  public DECPhoneCollection(File sourceFile, String outputFileName)
      throws IOException, LineUnavailableException, UnsupportedAudioFileException {
    dectalkPhones = new ArrayList<>();
    currentPhoneIndex = 0;

    sourceAudioPlayer = new SourceAudioPlayer(sourceFile);

    { // Make sure the directory for output if it doesn't exist
      File directory = new File("out/DECFiles/");
      if (!directory.exists()) {
        directory.mkdirs();
      }
    }
    DECtalkFile = new BufferedWriter(new FileWriter(outputFileName, true));
  }

  ///// Current phone methods: Handle with the current phone only /////

  /**
   * Plays the audio corresponding to the current phone
   */
  public void playCurrentPhone() {
    dectalkPhones.get(currentPhoneIndex).playClip();
  }

  /**
   * @return Pronunciation of the current phone
   */
  public String getCurrentPhonePronunciation() {
    return dectalkPhones.get(currentPhoneIndex).getPhone();
  }

  /**
   * @param pronunciation DECtalk phone to set the current phone's pronunciation to
   * @throws IllegalArgumentException If pronunciation is not a valid DECtalk phone
   */
  public void setCurrentPhonePronunciation(String pronunciation) throws IllegalArgumentException {
    if (!PhoneConversion.isDECPhone(pronunciation)) {
      throw new IllegalArgumentException("The given phone is not recognized by DECtalk");
    }
    dectalkPhones.get(currentPhoneIndex).setPhone(pronunciation);
  }

  /**
   * Removes the current phone from the collection.
   */
  public void removeCurrentPhone() {
    dectalkPhones.remove(currentPhoneIndex);
    --currentPhoneIndex;
  }

  ///// Surrounding phone methods: Handle the with current phone and phones around it /////

  /**
   * Plays the audio clip of the next phone
   */
  public void playNextPhone() {
    dectalkPhones.get(currentPhoneIndex + 1).playClip();
  }

  /**
   * Play the clips surrounding and including the current phone
   *
   * @param reverseCount Number of phones to play from behind the current phone. If this would cause
   * a phone < 0 to be selected it is adjusted to be the 0th phone.
   * @param forwardsCount Number of phones to play from in front of the current phone. If this would
   * cause a phone > the size of the collection to be selected it is adjusted to be the last phone.
   */
  public void playSurroundingPhones(int reverseCount, int forwardsCount) {
    int backCount = currentPhoneIndex - reverseCount; // Makes sure index is not out of range
    int frontCount = currentPhoneIndex + forwardsCount;

    for (DECtalkPhone phone : dectalkPhones.subList(backCount < 0 ? 0 : backCount,
        frontCount > dectalkPhones.size() -1 ? dectalkPhones.size() -1 : frontCount)) {
      phone.playClip();
    }
  }

  /**
   * Squish Extend the previous phone's time frame with this one. The current phone is discarded.
   */
  public void squishWithPrevious() {
    dectalkPhones.get(currentPhoneIndex - 1).absorbPhone(dectalkPhones.get(currentPhoneIndex));
    removeCurrentPhone();
  }

  ///// Output /////

  /**
   * Writes all the DECtalk information to the specified output file
   *
   * @throws IOException If the output file cannot properly be accessed
   */
  public void writeDECtalkFile() throws IOException {

    System.out.println("Writing output to file");

    DECtalkFile.write("[:phoneme on]\n");

    DECtalkFile.append("["); // Open bracket that DECtalk needs for each line of phones

    for (DECtalkPhone phone : dectalkPhones) {
      DECtalkFile.append(phone.toString());
    }

    DECtalkFile.append("]\n"); // Closes the "[" from the first print statement

    DECtalkFile.close();

  }

  ///// ArrayList operations: Handle adding, removing, or combining parts of the collection /////

  /**
   * Creates an audio clip for a phone then adds it to the end of this collection.
   *
   * @param dectalkPhone Phone to add
   */
  public void addPhone(DECtalkPhone dectalkPhone) {
    try {
      sourceAudioPlayer.createPhoneClip(dectalkPhone);
    } catch (LineUnavailableException e) {
      System.err.println("An exception occurred when trying to use the source audio line.");
      e.printStackTrace();
    } catch (IOException e) {
      System.err.println("An exception occurred when reading the source audio file");
      e.printStackTrace();
    }
    dectalkPhones.add(dectalkPhone);
  }

  /**
   * Adds a pause (DECtalk phone "_") into the collection.
   *
   * @param timeFrame Time frame to add the pause in
   */
  public void addPause(TimeFrame timeFrame) {
    addPhone(new DECtalkPhone(timeFrame));
  }

  /**
   * @return Amount of phones in this collection
   */
  public int getPhoneCount() {
    return dectalkPhones.size();
  }

  /**
   * @return The index of the current phone
   */
  public int getCurrentPhoneIndex() {
    return currentPhoneIndex;
  }

  /**
   * Increments the current phone index
   */
  public void goToNextPhone() {
    ++currentPhoneIndex;
  }

  /**
   * Start the evaluation process back at the beginning of the array
   */
  public void restart() {
    currentPhoneIndex = 0;
  }
}
