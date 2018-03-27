import edu.cmu.sphinx.util.TimeFrame;

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
   * @param CMUPhone Pronunciation of a CMU phone
   * @param timeFrame Time frame the phone took place in
   */
  public DECtalkPhone(String CMUPhone, TimeFrame timeFrame) {
    phone = PhoneConversion.convertCMUPhone(CMUPhone);
    this.timeFrame = timeFrame;
  }

  /**
   * @return Value of this object's phone
   */
  public String getPhone() {
    return phone;
  }

  /**
   * Sets the phone to a specified DECtalk phone
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
}
