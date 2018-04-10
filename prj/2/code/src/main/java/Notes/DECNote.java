package Notes;

import SoundHandling.PitchAnalysis;

public class DECNote extends Note {
  /* Minimum length a DECNote is allowed to be in milliseconds */
  private final static int MIN_LENGTH = 75;
  /* Duration of the DECNote in milliseconds */
  private long duration;

  public DECNote(MIDINote note, double ticksPerMillis) {
    this.start = note.getStartAsMillis(ticksPerMillis);
    this.end = note.getEndAsMillis(ticksPerMillis);
    this.duration = note.getDurationAsMillis(ticksPerMillis);
    this.pitch = PitchAnalysis.pianoKeyToToneNumber(note.getPitch());
  }

  public DECNote(MIDINote note, double ticksPerMillis, String phone) {
    this(note, ticksPerMillis);
    this.word = phone;
  }

  /**
   * @return Phone associated with this note
   */
  public String getPhone() {
    return word;
  }

  @Override
  public TimeUnit getTimeUnit() {
    return TimeUnit.MILLISECOND;
  }

  @Override
  public PitchUnit getPitchUnit() {
    return PitchUnit.TONE_NUMBER;
  }

  @Override
  public String toString() {
    StringBuilder DECformat = new StringBuilder(word);
    long calculatedLength = duration < MIN_LENGTH ? MIN_LENGTH : duration;
    DECformat.append("<").append(calculatedLength);

    if (!word.equals("_")) { // If the phone is not a wait (needs tone number added)
      DECformat.append(",").append(pitch);
    }

    DECformat.append(">");

    return DECformat.toString();
  }
}
