package Notes;

import SoundHandling.NoteRange;
import SoundHandling.PitchAnalysis;

public class DECNote extends Note {

  /* Minimum length a DECNote is allowed to be in milliseconds */
  private final static int MIN_LENGTH = 0;
  /* Duration of the DECNote in milliseconds */
  private long duration;
  /* Piano key of this note */
  private int pianoKey;
  /* Range that this note exists in */
  private NoteRange range;

  /*
   * Creates a DECNote
   *
   * @param note MIDINote to create a DECNote from
   * @param ticksPerMillis ticksPerMillis of the track
   */
  private DECNote(MIDINote note, double ticksPerMillis, NoteRange range) {
    this.start = note.getStartAsMillis(ticksPerMillis);
    this.end = note.getEndAsMillis(ticksPerMillis);
    this.duration = note.getDurationAsMillis(ticksPerMillis);
    if (duration < MIN_LENGTH) {
      duration = MIN_LENGTH;
    }
    pianoKey = note.getPitch();
    this.pitch = PitchAnalysis.pianoKeyToToneNumber(pianoKey);
    this.range = range;
  }

  /**
   * Creates a DECNote with a phone
   *
   * @param note MIDINote to create a DECNote from
   * @param ticksPerMillis ticksPerMillis of the track
   * @param phone Phone to use for this note
   */
  public DECNote(MIDINote note, double ticksPerMillis, NoteRange range, String phone) {
    this(note, ticksPerMillis, range);
    this.word = phone;
  }

  /**
   * Creates a new note that represents a pause
   *
   * @param startTime Time the pause starts
   * @param endTime Time the pause ends
   */
  public DECNote(long startTime, long endTime, NoteRange range) {
    this.start = startTime;
    this.end = endTime;
    this.duration = endTime - startTime;
    this.word = "_";
    this.range = range;
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
    long calculatedLength = duration < MIN_LENGTH && !word.equals("_") ? MIN_LENGTH : duration;
    DECformat.append("<").append(calculatedLength);

    if (!word.equals("_")) { // If the phone is not a wait (needs tone number added)
      DECformat.append(",").append(pitch);
    }

    DECformat.append(">");

    return DECformat.toString();
  }
}
