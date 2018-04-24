import wavetodec.DECtalkPhone;

/**
 * Handles what the volume should be for various pitches
 * DECtalk changes pitch based on what comes next(?)
 */
public class PitchVolumeController {

  private enum VOLUME_SETTING {
    STATIC
  }

  private final static VOLUME_SETTING SETTING = VOLUME_SETTING.STATIC;
  private final static int MAX_VOLUME_DELTA = 1000;
  private final static int DEC_VOLUME_DELTA_LIMIT = 99;

  public static String volumeShift(DECtalkPhone lastPhone, DECtalkPhone currentPhone) {
    String shift = "";

    if (lastPhone.getToneNumber() == currentPhone.getToneNumber()) {
      return shift;
    }

    if (currentPhone.getPhone().equals("_")) {
      return shift;
    }

    if (SETTING == VOLUME_SETTING.STATIC) {
      try {
        shift = staticVolumeShift(lastPhone, currentPhone);
      } catch (ArithmeticException e) { // TODO instead of this, find next good phone
        if (currentPhone.getToneNumber() != 0) {
          int desiredVolume = staticVolume(currentPhone.getToneNumber());
          shift = generateChangeCommand(MAX_VOLUME_DELTA / 2, desiredVolume); // I just made up 50
        }
      }
    }

    return shift;

  }

  /**
   * Replaces the given key with a tone that fits within all possible tones
   */
  public int replaceToneToFit(int key) {
    return 0;
  }

  private static String staticVolumeShift(DECtalkPhone lastPhone, DECtalkPhone currentPhone) {
    int currentVolume = staticVolume(lastPhone.getToneNumber());
    int desiredVolume = staticVolume(currentPhone.getToneNumber());
    return generateChangeCommand(currentVolume, desiredVolume);
  }

  /*
   * Only works for tones 1-37 (DECtones, no Hz)
   *
   * @param phone
   * @return
   */
  private static int staticVolume(int tone) {
    if (tone == 0) {
      return MAX_VOLUME_DELTA / 2;
    }
    int calculatedVolume = MAX_VOLUME_DELTA / 2;
    double maxPitchDiff = tone / 37.0;
    double minPitchDiff = 1.0 / tone;
    calculatedVolume += (MAX_VOLUME_DELTA / 2) * maxPitchDiff; // max volume for high pitches
    calculatedVolume -= (MAX_VOLUME_DELTA / 2) * minPitchDiff; // min volume for low pitches
    return calculatedVolume;
  }

  /*
   * Makes as many volume changing commands as necessary
   *
   * @param currentVolume
   * @param newVolume
   * @return
   */
  private static String generateChangeCommand(int currentVolume, int newVolume) {
    int volumeChangeRemaining = Math.abs(newVolume - currentVolume);
    String movement = currentVolume < newVolume ? "up " : "down ";

    StringBuilder command = new StringBuilder();
    for (int i = volumeChangeRemaining / DEC_VOLUME_DELTA_LIMIT; i > 0;
        --i, volumeChangeRemaining -= DEC_VOLUME_DELTA_LIMIT) {
      command.append("][:volume ").append(movement).append(DEC_VOLUME_DELTA_LIMIT).append("][");
    }

    command.append("][:volume ").append(movement).append(volumeChangeRemaining).append("][");

    return command.toString();
  }

}
