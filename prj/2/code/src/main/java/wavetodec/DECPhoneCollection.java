package wavetodec;

import edu.cmu.sphinx.util.TimeFrame;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Handles the organization of phones and controls the user's edits to auto-generated results.
 *
 * This class is not responsible for handling user input, however it is responsible for DECtalk file
 * output.
 *
 * TODO insert phone command TODO split phone command TODO phone time management TODO with preview
 * TODO better file output (multiple files so no need to restart) TODO improve information when
 * playing phones and surrounding phones TODO indicate if there should be a slide or higher/lower
 * pitch to surrounding phones, then move tone numbers to fit the model layed out by the user TODO
 * play parts of the original song, not just phone parts
 * @deprecated autoDEC does not currently support a WAVE file as input, which is what this class was
 * made for.
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
   * Constructor for wavetodec.DECPhoneCollection
   */
  public DECPhoneCollection(File sourceFile)
      throws IOException, LineUnavailableException, UnsupportedAudioFileException {
    dectalkPhones = new ArrayList<>();
    currentPhoneIndex = 0;
    removedPhoneCount = 0;
    lastEditedPhone = 0;
    defaultToneSelectSetting = ToneSelectSetting.FAVOR_HIGH;

    sourceAudioPlayer = new SourceAudioPlayer(sourceFile);

    createNewOutputFile("");
  }

  ///// Current phone methods: Handle with the current phone only /////

  /**
   * Plays the audio corresponding to the current phone
   */
  public void playCurrentPhone() {
    playPhone(currentPhoneIndex);
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
    playPhone(currentPhoneIndex + 1);
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

    for (int i = backCount < 0 ? 0 : backCount; i < frontCount && i < dectalkPhones.size(); ++i) {
      playPhone(i);
    }
  }

  /**
   * Extend the previous phone's time frame with the current phone's. The current phone is deleted.
   */
  public void squishWithPrevious() {
    squishPhones(currentPhoneIndex - 1, currentPhoneIndex);
  }

  /**
   * Extend the current phone's time frame with the next phone's. The next phone is deleted.
   */
  public void squishWithNext() {
    squishPhones(currentPhoneIndex, currentPhoneIndex + 1);
  }

  /**
   * Replace every occurrence of a tone with a new tone
   *
   * @param oldTone Tone to replace
   * @param newTone Tone to replace with
   */
  public void toneReplace(int oldTone, int newTone) {
    for (DECtalkPhone phone : dectalkPhones) {
      if (phone.getToneNumber() == oldTone) {
        phone.setToneNumber(newTone);
      }
    }
  }

  /**
   * Replace the tone of the current phone with a new tone
   *
   * @param newTone Tone number to replace the current phone's tone with
   */
  public void toneReplace(int newTone) {
    dectalkPhones.get(currentPhoneIndex).setToneNumber(newTone);
  }

  /**
   * Shifts the tone of all phones by shiftAmount (can be negative or positive)
   *
   * @param shiftAll If all tones should be shifted or only the current one
   * @param shiftAmount Amount to shift tones by in DECtalk tone number format
   */
  public void toneShift(boolean shiftAll, int shiftAmount) {
    if (shiftAll) {
      for (DECtalkPhone phone : dectalkPhones) {
        phone.setToneNumber(safeToneShift(phone.getToneNumber(), shiftAmount));
      }
    } else {
      DECtalkPhone currentPhone = dectalkPhones.get(currentPhoneIndex);
      currentPhone.setToneNumber(safeToneShift(currentPhone.getToneNumber(), shiftAmount));
    }
  }

  /**
   * Balances the tone numbers of phones with the phones surrounding them for less jarring jumps in
   * pitch.
   *
   * TODO balance tones in a range with each other
   */
  public void toneNumberBalance(boolean balanceAll) {
    if (balanceAll) {
      // Balances all tones with their surroundings
      for (int i = 0; i < dectalkPhones.size(); ++i) {
        balanceBackwards(i, defaultToneSelectSetting);
        balanceForwards(i, defaultToneSelectSetting);
      }
    } else {
      // Balances only the current tone
      balanceBackwards(currentPhoneIndex, defaultToneSelectSetting);
      balanceForwards(currentPhoneIndex, defaultToneSelectSetting);
    }
  }

  /*
   * Plays the phone at a specified index
   *
   * @param phoneIndex Index of the phone to play
   */
  private void playPhone(int phoneIndex) {
    DECtalkPhone queuedPhone = dectalkPhones.get(phoneIndex);

    System.out.println("Playing phone " + phoneIndex + ". Registered as sound "
        + queuedPhone.getPhone());

    queuedPhone.playClip();
  }

  /*
   * Squishes the time of one phone into the time of another.
   *
   * Be careful with this, it still does allow for phones not next to each other to be squished.
   * That is why this should remain private. All squishing should be done from individual methods
   * that will protect from that happening.
   *
   * @param absorberIndex Index of the phone that is absorbing the time of the other
   * @param squishedIndex Index of the phone to have its time taken and deleted
   */
  private void squishPhones(int absorberIndex, int squishedIndex) {
    dectalkPhones.get(absorberIndex).absorbPhone(dectalkPhones.get(squishedIndex));
    dectalkPhones.remove(squishedIndex);
  }

  /*
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

  /*
   * Shifts a tone number while keeping it within the boundaries of DECtalk's tones
   *
   * @param currentTone Tone number of the phone that is getting shifted
   * @param shiftAmount Amount to shift currentTone by
   * @return A number >= 1 and <= 37 representing the shifted tone number
   */
  private int safeToneShift(int currentTone, int shiftAmount) {
    int newTone = currentTone + shiftAmount;
    if (newTone < 1) {
      newTone = 1;
    } else if (newTone > 37) {
      newTone = 37;
    }
    return newTone;
  }

  ///// Output /////

  /**
   * Writes all the DECtalk information to the specified output file
   */
  public void writeDECtalkFile() {
    writeDECtalkFile(false);
  }

  /**
   * Save the editing progress to a file to be continued later
   */
  public void saveProgress() {
    // The last phone edited (therefore approved) by user
    try {
      DECtalkFile.append(String.valueOf(lastEditedPhone)).append("\n");
      DECtalkFile.append(String.valueOf(removedPhoneCount)).append("\n");
    } catch (IOException e) {
      System.err.println("An error occurred while writing save information");
      e.printStackTrace();
    }

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
   * Creates a new output file for this phone collection
   *
   * @param newName If this has a value, that name will be used for output, otherwise it will be
   * automatically generated
   */
  public void createNewOutputFile(String newName) {

    if (newName.equals("")) {
      outputFileName = "out/DECFiles/" + new SimpleDateFormat("yyyyMMddHHmm").format(new Date())
          + "_out.txt";
    } else {
      outputFileName = newName;
    }

    { // Make sure the directory for output if it doesn't exist
      File directory = new File("out/DECFiles/");
      if (!directory.exists()) {
        directory.mkdirs();
      }
    }

    closeOutput();

    try {
      DECtalkFile = new BufferedWriter(new FileWriter(outputFileName, true));
    } catch (IOException e) {
      System.err.println("Unable to create output file.");
      e.printStackTrace();
    }

  }

  /**
   * Closes file output
   */
  public void closeOutput() {
    try {
      if (DECtalkFile != null) {
        DECtalkFile.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Prints the DECtalk information to the console
   */
  public void printDECToConsole() {
    for (DECtalkPhone phone : dectalkPhones) {
      // Prints the information of every phone
      System.out.print(phone.toString());
    }
    System.out.println(); // Add a blank line after printing all the phones
  }

  /*
   * Writes all the DECtalk information to the specified output file
   *
   * @param saveState If the state of the phone should also be written for loading later
   * @throws IOException If the output file cannot properly be accessed
   */
  private void writeDECtalkFile(boolean saveState) {

    System.out.println("Writing output to file");
    try {
      DECtalkFile.append("[:phoneme on]\n");

      DECtalkFile.append("["); // Open bracket that DECtalk needs for each line of phones

      for (DECtalkPhone phone : dectalkPhones) {
        DECtalkFile.append(saveState ? phone.saveState() : phone.toString());
      }

      DECtalkFile.append("]\n"); // Closes the "[" from the first print statement
    } catch (IOException e) {
      System.err.println("An error occurred while writing the DECtalk file");
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
    playCurrentPhone();
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
