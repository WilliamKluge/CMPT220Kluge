import edu.cmu.sphinx.util.TimeFrame;
import java.sql.Time;

/**
 * Holds information about a DECtalk style phone
 */
public class DECtalkPhone {

  /* Phone stored by this class */
  private String phone;
  /* Pitch of this phone */
  private Double toneNumber;
  /* Time frame the phone took place in */
  private TimeFrame timeFrame;

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
   * Creates an instance of DECtalkPhone with only a time frame, sets the phone to be silence
   * @param timeFrame Time frame the phone takes place in
   */
  public DECtalkPhone(TimeFrame timeFrame) {
    this.phone = "_";
    this.timeFrame = timeFrame;
  }

  /**
   * Pushes the end of this object's time frame to the end of a specified time frame
   * @param timeFrame Time frame to extend to
   */
  public void extendTimeframeTo(TimeFrame timeFrame) {
    this.timeFrame = new TimeFrame(this.timeFrame.getStart(), timeFrame.getEnd());
  }

  /**
   * Sets this phone's tone number to be equal to another phones tone number
   * @param dectalkPhone DECtalkPhone to match the tone number of
   */
  public void matchTone(DECtalkPhone dectalkPhone) {
    this.toneNumber = dectalkPhone.toneNumber;
  }

  /**
   * @return DECtalk format of this phone
   */
  @Override
  public String toString() {
    StringBuilder DECformat = new StringBuilder(phone);

    DECformat.append("<").append(timeFrame.length());

    if (!phone.equals("_")) { // If the phone is not a wait (needs tone number added)
      DECformat.append(",").append(toneNumber);
    }

    DECformat.append(">");

    return DECformat.toString();
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
  public Double getToneNumber() {
    return toneNumber;
  }

  /**
   * @param toneNumber DECtalk tone number to set this phone to
   */
  public void setToneNumber(Double toneNumber) {
    this.toneNumber = toneNumber;
  }

}
