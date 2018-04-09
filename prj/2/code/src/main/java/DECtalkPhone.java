import edu.cmu.sphinx.util.TimeFrame;
import java.sql.Time;
import java.util.concurrent.TimeUnit;
import javax.sound.sampled.Clip;

/**
 * Holds information about a DECtalk style phone
 */
public class DECtalkPhone {
  /** */
  private int track;
  /* Phone stored by this class */
  private String phone;
  /* Pitch of this phone */
  private int toneNumber;
  /* Time frame the phone took place in */
  private TimeFrame timeFrame;
  /* Audio clip of this phone */
  private Clip clip;
  private final static int MIN_LENGTH = 75;

  /**
   * Creates an instance of DECtalkPhone using the information from phone recognition
   *
   * @param CMUPhone Pronunciation of a CMU phone
   * @param timeFrame Time frame the phone takes place in
   */
  public DECtalkPhone(String CMUPhone, TimeFrame timeFrame) {
    this.phone = PhoneConversion.convertCMUPhone(CMUPhone);
    this.timeFrame = timeFrame;
  }

  /**
   * Creates an instance of DECtalkPhone using the information from phone recognition
   *
   * @param timeFrame Time frame the phone takes place in
   * @param DECPhone Phone in DECtalk
   */
  public DECtalkPhone(TimeFrame timeFrame, String DECPhone) {
    this.phone = DECPhone;
    this.timeFrame = timeFrame;
  }

  /**
   * Creates an instance of DECtalkPhone with only a time frame, sets the phone to be silence
   *
   * @param timeFrame Time frame the phone takes place in
   */
  public DECtalkPhone(TimeFrame timeFrame) {
    this.phone = "_";
    this.timeFrame = timeFrame;
  }

  /**
   * Creates a DECtalkPhone from information used when reading a save file
   *
   * @param syntax DECtalk syntax associated with this phone
   */
  public DECtalkPhone(String syntax) {
    String[] syntaxPieces = syntax.split("[<,]"); // Split on < or , (remaining pieces after split)
    // Because of this we get to ignore the duration (syntaxPieces[3]), can be removed from saves
    timeFrame = new TimeFrame(Long.parseLong(syntaxPieces[0]), Long.parseLong(syntaxPieces[1]));
    phone = syntaxPieces[2];
    // Just take the end (in case it is a pause)
    toneNumber = Integer.parseInt(syntaxPieces[syntaxPieces.length - 1]);
  }

  /**
   * Pushes the end of this object's time frame to the end of a specified time frame
   *
   * @param timeFrame Time frame to extend to
   */
  public void extendTimeframeTo(TimeFrame timeFrame) {
    this.timeFrame = new TimeFrame(this.timeFrame.getStart(), timeFrame.getEnd());
  }

  /**
   * Extends this phone's time frame to the time frame of the given phone. The max of the time
   * frames is what will be selected as the new time frame.
   *
   * @param phone Phone to absorb the time frame of
   */
  public void absorbPhone(DECtalkPhone phone) {
    TimeFrame otherPhoneTimeFrame = phone.getTimeFrame();
    long newStart = timeFrame.getStart() < otherPhoneTimeFrame.getStart() ? timeFrame.getStart() :
        otherPhoneTimeFrame.getStart();
    long newEnd = timeFrame.getEnd() > otherPhoneTimeFrame.getEnd() ? timeFrame.getEnd() :
        otherPhoneTimeFrame.getEnd();

    this.timeFrame = new TimeFrame(newStart, newEnd);
  }

  /**
   * Sets this phone's tone number to be equal to another phones tone number
   *
   * @param dectalkPhone DECtalkPhone to match the tone number of
   */
  public void matchTone(DECtalkPhone dectalkPhone) {
    this.toneNumber = dectalkPhone.toneNumber;
  }

  /**
   * Plays the phone's source audio once.
   */
  public void playClip() {

    clip.loop(1);
    try {
      TimeUnit.MILLISECONDS.sleep(timeFrame.length());
    } catch (InterruptedException e) {
      System.err.println("Program was interrupted while playing a phone clip.");
      e.printStackTrace();
    }
    clip.stop();

  }

  /**
   * @return DECtalk format of this phone
   */
  @Override
  public String toString() {
    StringBuilder DECformat = new StringBuilder(phone);
    long calculatedLength = timeFrame.length() < MIN_LENGTH ? MIN_LENGTH : timeFrame.length();
    DECformat.append("<").append(calculatedLength);

    if (!phone.equals("_")) { // If the phone is not a wait (needs tone number added)
      DECformat.append(",").append(toneNumber);
    }

    DECformat.append(">");

    return DECformat.toString();
  }

  public String pauseCommand() {
    return "[:pp " + timeFrame.length() + "]";
  }

  /**
   * @return String representing the state of the phone including its start and end times
   */
  public String saveState() {
    return timeFrame.getStart() + "," + timeFrame.getEnd() + "," + toString();
  }

  /**
   * @return Value of this object's phone
   */
  public String getPhone() {
    return phone;
  }

  /**
   * Sets the phone to a specified DECtalk phone
   *
   * @param DECtalkPhone Phone to set to
   */
  public void setPhone(String DECtalkPhone) {
    this.phone = DECtalkPhone;
  }

  /**
   * @return Tone number of this phone
   */
  public int getToneNumber() {
    return toneNumber;
  }

  /**
   * @param toneNumber DECtalk tone number to set this phone to
   */
  public void setToneNumber(int toneNumber) {
    this.toneNumber = toneNumber;
  }

  /**
   * @return Time frame this phone happens in
   */
  public TimeFrame getTimeFrame() {
    return timeFrame;
  }

  /**
   * @param clip Clip of source audio that this phone represents
   */
  public void setClip(Clip clip) {
    this.clip = clip;
  }

  public int getTrack() {
    return track;
  }

  public void setTrack(int track) {
    this.track = track;
  }
}
