package Notes;

import SoundHandling.PitchAnalysis;

public class DECNote extends Note {
  /* Minimum length a DECNote is allowed to be in milliseconds */
  private final static int MIN_LENGTH = 75;
  /* Duration of the DECNote in milliseconds */
  private long duration;
  /* Piano key of this note */
  private int pianoKey;

  public DECNote(MIDINote note, double ticksPerMillis) {
    this.start = note.getStartAsMillis(ticksPerMillis);
    this.end = note.getEndAsMillis(ticksPerMillis);
    this.duration = note.getDurationAsMillis(ticksPerMillis);
    if (duration < MIN_LENGTH) {
      duration = MIN_LENGTH;
    }
    pianoKey = note.getPitch();
    this.pitch = PitchAnalysis.pianoKeyToToneNumber(pianoKey);
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

  /**
   * @return Start of the note in terms of milliseconds
   */
  public long getStartAsMillis() {
    return start;
  }

  /**
   * @return End of the note in terms of milliseconds
   */
  public long getEndAsMillis() {
    return end;
  }

  /**
   * @return Duration of the note in terms of milliseconds
   */
  public long getDurationAsMillis() {
    return duration;
  }

  public int getPianoKey() {
    return pianoKey;
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
