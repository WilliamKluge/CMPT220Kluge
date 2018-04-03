import edu.cmu.sphinx.util.TimeFrame;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
 *
 * TODO insert phone command
 * TODO split phone command
 * TODO phone time management
 *   TODO with preview
 * TODO better file output (multiple files so no need to restart)
 * TODO improve information when playing phones and surrounding phones
 */
public class DECPhoneCollection {

  /**
   * Enumerator for setting how tones should be normalized.
   */
  public enum ToneSelectSetting {
    FAVOR_HIGH,
    FAVOR_LOW,
    AVERAGE
  }

  /**
   * Setting for how tones should be selected.
   */
  public ToneSelectSetting defaultToneSelectSetting;
  /*
   * Index of the phone currently being worked with.
   *
   * Set up like this so that this object does not need to handle any looping and outside methods
   * can preform iteration checks knowing what the index currently is without worrying about the
   * amount of phones changing.
   */
  private int currentPhoneIndex;
  /* Number of phones that have been removed from the collection (for saving progress) */
  private int removedPhoneCount;
  /* Index of the furthest phone edited by the user. Needed for file loading because
  currentPhoneIndex could be set further than where the user actually edited. This is not affected
   by tone numbers, only the phone itself. */
  private int lastEditedPhone;
  /* Array of phones that take place in the source audio in sequential order */
  private ArrayList<DECtalkPhone> dectalkPhones;
  /* BufferedWriter to handle output of DECtalk file */
  private BufferedWriter DECtalkFile;
  /* Handles playback of source audio */
  private SourceAudioPlayer sourceAudioPlayer;
  /* Name of the file to output to */
  private String outputFileName;

  /**
   * Constructor for DECPhoneCollection
   *
   * @param outputFileName Name and path of the file to output DECtalk to
   */
  public DECPhoneCollection(File sourceFile, String outputFileName)
      throws IOException, LineUnavailableException, UnsupportedAudioFileException {
    dectalkPhones = new ArrayList<>();
    currentPhoneIndex = 0;
    removedPhoneCount = 0;
    lastEditedPhone = 0;
    defaultToneSelectSetting = ToneSelectSetting.FAVOR_HIGH;

    sourceAudioPlayer = new SourceAudioPlayer(sourceFile);

    this.outputFileName = outputFileName;

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
    checkLastEditedPhone(currentPhoneIndex);
  }

  /**
   * Removes the current phone from the collection.
   */
  public void removeCurrentPhone() {
    dectalkPhones.remove(currentPhoneIndex);
    --currentPhoneIndex;
    checkLastEditedPhone(currentPhoneIndex);
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
        frontCount > dectalkPhones.size() - 1 ? dectalkPhones.size() - 1 : frontCount)) {
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

  /**
   * Replace over occurrence of a tone with a new tone
   *
   * @param oldTone Tone to replace
   * @param newTone Tone to replace with
   */
  public void fullReplaceTone(int oldTone, int newTone) {
    for (DECtalkPhone phone : dectalkPhones) {
      if (phone.getToneNumber() == oldTone) {
        phone.setToneNumber(newTone);
      }
    }
  }

  /**
   * Shifts the tone of all phones by shiftAmount (can be negative or positive)
   *
   * @param shiftAmount Amount to shift tones by in DECtalk tone number format
   */
  public void fullToneShift(int shiftAmount) {
    for (DECtalkPhone phone : dectalkPhones) {
      int currentTone = phone.getToneNumber();
      int newTone = currentTone + shiftAmount;
      if (newTone < 1) {
        newTone = 1;
      } else if (newTone > 37) {
        newTone = 37;
      }
      phone.setToneNumber(newTone);
    }
  }

  /**
   * Balances the tone numbers of phones with the phones surrounding them for less jarring jumps in
   * pitch.
   *
   * TODO balance tones in a range with each other
   */
  public void balanceToneNumbers() {
    for (int i = 0; i < dectalkPhones.size(); ++i) {
      balanceBackwards(currentPhoneIndex, defaultToneSelectSetting);
      balanceForwards(currentPhoneIndex, defaultToneSelectSetting);
    }
  }

  /**
   * Balances two tones based on toneSelectSetting.
   *
   * @param toneOne Tone to balance
   * @param toneTwo Tone to balance with
   * @param toneSelectSetting Setting for how to balance the tones
   * @return A tone set to match better with toneTwo
   */
  private int balanceTones(int toneOne, int toneTwo, ToneSelectSetting toneSelectSetting) {

    int selectedTone;

    if (toneSelectSetting == ToneSelectSetting.FAVOR_HIGH) {
      selectedTone = toneOne > toneTwo ? toneOne : toneTwo;
    } else if (toneSelectSetting == ToneSelectSetting.FAVOR_LOW) {
      selectedTone = toneOne < toneTwo ? toneOne : toneTwo;
    } else {
      selectedTone = Math.round((toneOne + toneTwo) / 2);
    }

    return selectedTone;

  }

  /*
   * Balance the tone of the current phone with the phone that comes before it
   *
   * @param index Index of the phone to balance
   * @param toneSelectSetting Setting for how to balance the tones
   */
  private void balanceBackwards(int index, ToneSelectSetting toneSelectSetting) {
    if (index == 0) { // Don't try to balance backwards if there is nothing there
      return;
    }
    int currentPhoneTone = dectalkPhones.get(index).getToneNumber();
    int previousPhoneTone = dectalkPhones.get(index - 1).getToneNumber();
    int balancedTone = balanceTones(currentPhoneTone, previousPhoneTone, toneSelectSetting);

    dectalkPhones.get(index).setToneNumber(balancedTone);
  }

  /*
   * Balance the tone of the current phone with the phone that comes after it
   *
   * @param index Index of the phone to balance
   * @param toneSelectSetting Setting for how to balance the tones
   */
  private void balanceForwards(int index, ToneSelectSetting toneSelectSetting) {
    int currentPhoneTone = dectalkPhones.get(index).getToneNumber();
    int nextPhoneTone = dectalkPhones.get(index - 1).getToneNumber();
    int balancedTone = balanceTones(currentPhoneTone, nextPhoneTone, toneSelectSetting);

    dectalkPhones.get(index).setToneNumber(balancedTone);
  }

  ///// Output /////

  /**
   * Writes all the DECtalk information to the specified output file
   *
   * @throws IOException If the output file cannot properly be accessed
   */
  public void writeDECtalkFile() throws IOException {
    writeDECtalkFile(false);
  }

  /*
   * Writes all the DECtalk information to the specified output file
   *
   * @param saveState If the state of the phone should also be written for loading later
   * @throws IOException If the output file cannot properly be accessed
   */
  private void writeDECtalkFile(boolean saveState) throws IOException {

    System.out.println("Writing output to file");

    DECtalkFile.append("[:phoneme on]\n");

    DECtalkFile.append("["); // Open bracket that DECtalk needs for each line of phones

    for (DECtalkPhone phone : dectalkPhones) {
      DECtalkFile.append(saveState ? phone.saveState() : phone.toString());
    }

    DECtalkFile.append("]\n"); // Closes the "[" from the first print statement

  }

  /**
   * Save the editing progress to a file to be continued later
   *
   * @throws IOException If the file can not be written
   */
  public void saveProgress() throws IOException {
    // The last phone edited (therefore approved) by user
    DECtalkFile.append(String.valueOf(lastEditedPhone)).append("\n");
    DECtalkFile.append(String.valueOf(removedPhoneCount)).append("\n");

    writeDECtalkFile(true);

  }

  /**
   * Load the progress of a previous editing session
   *
   * @param saveFilePath Path to the save file to load
   */
  public void loadProgress(String saveFilePath) {

    try {
      BufferedReader br = new BufferedReader(new FileReader(saveFilePath));

      lastEditedPhone = Integer.parseInt(br.readLine());
      currentPhoneIndex = lastEditedPhone;

      removedPhoneCount = Integer.parseInt(br.readLine());
      // Delete all phones
      dectalkPhones.clear();

      br.skip(14); // Skip the 13 characters of "[:phoneme on]" and the newline

      StringBuilder DECSyntax = new StringBuilder();
      String line;
      while ((line = br.readLine()) != null) {
        DECSyntax.append(line);
      }

      String builtDECSyntax = DECSyntax.toString();
      builtDECSyntax = builtDECSyntax.replace("[", ""); // Remove non-phone syntax characters
      builtDECSyntax = builtDECSyntax.replace("]", "");

      String[] splitSyntax = builtDECSyntax.split("[>]"); // Split at the end of each phone

      for (String preparedSyntax : splitSyntax) {
        DECtalkPhone builtPhone = new DECtalkPhone(preparedSyntax);
        addPhone(builtPhone, dectalkPhones.size()); // Add the new phone to the beginning
      }

    } catch (FileNotFoundException e) {
      System.err.println("The specified save file could not be found");
      e.printStackTrace();
    } catch (IOException e) {
      System.err.println("An error occurred while reading the save file");
      e.printStackTrace();
    }

  }

  /**
   * Recreates the output file so there is no content in it.
   */
  public void clearOutputFile() {
    try {
      DECtalkFile.close();
      DECtalkFile = new BufferedWriter(new FileWriter(outputFileName, true));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Flushes the output to the file
   */
  public void flushOutput() {
    try {
      DECtalkFile.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Closes file output
   */
  public void closeOutput() {
    try {
      DECtalkFile.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  ///// ArrayList operations: Handle adding, removing, or combining parts of the collection /////

  /**
   * Creates an audio clip for a phone then adds it to the end of this collection.
   *
   * @param dectalkPhone Phone to add
   */
  public void addPhone(DECtalkPhone dectalkPhone) {
    addPhone(dectalkPhone, dectalkPhones.size());
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
   * @param currentPhoneIndex Index to set the current tone to
   * @throws IllegalArgumentException If the given currentPhoneIndex does not exist in the array
   */
  public void setCurrentPhoneIndex(int currentPhoneIndex) throws IllegalArgumentException {
    if (currentPhoneIndex >= dectalkPhones.size() || currentPhoneIndex < 0) {
      throw new IllegalArgumentException("Specified index is not in the array");
    }
    this.currentPhoneIndex = currentPhoneIndex;
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

  /*
   * Creates an audio clip for a phone then adds it to the end of this collection.
   *
   * @param dectalkPhone Phone to add
   */
  private void addPhone(DECtalkPhone dectalkPhone, int position) {
    try {
      sourceAudioPlayer.createPhoneClip(dectalkPhone);
    } catch (LineUnavailableException e) {
      System.err.println("An exception occurred when trying to use the source audio line.");
      e.printStackTrace();
    } catch (IOException e) {
      System.err.println("An exception occurred when reading the source audio file");
      e.printStackTrace();
    }
    dectalkPhones.add(position, dectalkPhone);
  }

  /**
   * Ensures that lastEditedPhone stays set to the furthest edit made by the user.
   *
   * @param currentEditIndex Index of the phone that was just edited
   */
  private void checkLastEditedPhone(int currentEditIndex) {
    lastEditedPhone = lastEditedPhone > currentEditIndex ? lastEditedPhone : currentEditIndex;
  }
}
