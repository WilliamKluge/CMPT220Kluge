package notes;

import soundhandling.PitchAnalysis;

public abstract class Note {

  /**
   * Defines what unit of time a note uses
   */
  public enum TimeUnit {
    MIDI_TICK,
    MILLISECOND
  }

  /**
   * Defines how a note handles pitch
   */
  public enum PitchUnit {
    PIANO_KEY,
    TONE_NUMBER,
    HZ
  }

  /* Time the note starts */
  protected long start;
  /* Time the note ends */
  protected long end;
  /* Channel of the note */
  protected int channel;
  /* Velocity of the note */
  protected int velocity;
  /* Pitch of the note in PitchUnit */
  protected int pitch;
  /* The word associated with this note */
  protected String word;

  /**
   * Default constructor for Note
   */
  public Note() {

  }

  /**
   * Constructs the Note
   * @param start Time the note starts at (in TimeUnit)
   * @param end Time the note ends at (in TimeUnit)
   * @param channel Channel of the note. This means different things for different derived classes,
   * but this is a determining factor of which voice autodec.autoDEC assigns to the note.
   * @param velocity Velocity of the note (basically how hard was the key smashed)
   * @param pitch Pitch of the note. Again, this means different things for different derived
   * classes, b
   */
  public Note(long start, long end, int channel, int velocity, int pitch) {
    this.start = start;
    this.end = end;
    this.channel = channel;
    this.velocity = velocity;
    this.pitch = pitch;
    this.word = null;
  }

  /**
   * Constructs the Note
   * @param start Time the note starts at (in TimeUnit)
   * @param end Time the note ends at (in TimeUnit)
   * @param channel Channel of the note. This means different things for different derived classes,
   * but this is a determining factor of which voice autodec.autoDEC assigns to the note.
   * @param velocity Velocity of the note (basically how hard was the key smashed)
   * @param pitch Pitch of the note. Again, this means different things for different derived
   * classes, b
   * @param word Word/syllable/phone of this note
   */
  public Note(long start, long end, int channel, int velocity, int pitch, String word) {
    this(start, end, channel, velocity, pitch);
    this.word = word;
  }

  /**
   * @return Returns the format of time unit used for this note's start, end, and duration
   */
  public abstract TimeUnit getTimeUnit();

  /**
   * @return Returns the format of the pitch of this note.
   */
  public abstract PitchUnit getPitchUnit();

  /**
   * @return Start time of the note in terms of MIDI ticks
   */
  public long getStartAsTicks() {
    if (getTimeUnit() == TimeUnit.MIDI_TICK) {
      return start;
    }
    throw new UnsupportedOperationException("Conversion from millis to ticks is not supported");
  }

  /**
   * @return End time of the note in terms of MIDI ticks
   */
  public long getEndAsTicks() {
    if (getTimeUnit() == TimeUnit.MIDI_TICK) {
      return end;
    }
    throw new UnsupportedOperationException("Conversion from millis to ticks is not supported");
  }

  /**
   * @return Duration of the note in terms of MIDI ticks
   */
  public long getDurationAsTicks() {
    if (getTimeUnit() == TimeUnit.MIDI_TICK) {
      return start - end;
    }
    throw new UnsupportedOperationException("Conversion from millis to ticks is not supported");
  }

  /**
   * @return Start of the note in terms of milliseconds
   */
  public long getStartAsMillis(double ticksPerMillis) {
    if (getTimeUnit() == TimeUnit.MILLISECOND) {
      return start;
    }
    return (long) (start / ticksPerMillis);
  }

  /**
   * @return End of the note in terms of milliseconds
   */
  public long getEndAsMillis(double ticksPerMillis) {
    if (getTimeUnit() == TimeUnit.MILLISECOND) {
      return end;
    }
    return (long) (end / ticksPerMillis);
  }

  /**
   * @return Duration of the note in terms of milliseconds
   */
  public long getDurationAsMillis(double ticksPerMillis) {
    if (getTimeUnit() == TimeUnit.MILLISECOND) {
      return start - end;
    }
    return getEndAsMillis(ticksPerMillis) - getStartAsMillis(ticksPerMillis);
  }

  /**
   * @return This note's pitch represented by a DECtalk tone number
   */
  public int getToneNumber() {
    int toneNumber;
    switch (getPitchUnit()) {
      case TONE_NUMBER:
        toneNumber = pitch;
        break;
      case PIANO_KEY:
        toneNumber = PitchAnalysis.pianoKeyToToneNumber(pitch);
        break;
      case HZ:
        toneNumber = PitchAnalysis.pitchToToneNumber(pitch);
        break;
      default:
        throw new UnsupportedOperationException("Unable to convert a notes pitch to a tone number");
    }

    return toneNumber;
  }

  /**
   * Sets the start and end time of the note. Units must match the classes TimeUnit
   * @param start Time the note starts at
   * @param end Time the note ends at
   */
  public void setTimeframe(long start, long end) {
    this.start = start;
    this.end = end;
  }

  /**
   * @return Channel of this note
   */
  public int getChannel() {
    return channel;
  }

  /**
   * @param channel New value to use as this note's channel
   */
  public void setChannel(int channel) {
    this.channel = channel;
  }

  /**
   * @return Velocity of this note. Velocity comes from MIDI files.
   */
  public int getVelocity() {
    return velocity;
  }

  /**
   * @param velocity New value to use for this note's velocity.
   */
  public void setVelocity(int velocity) {
    this.velocity = velocity;
  }

  /**
   * @return Pitch of this note
   */
  public int getPitch() {
    return pitch;
  }

  /**
   * @param pitch New value to use for this note's pitch
   */
  public void setPitch(int pitch) {
    this.pitch = pitch;
  }

  /**
   * This is protected so that only children of Note that specifically deal with words will handle
   * them.
   *
   * @return Word/syllable/phone associated with this note
   */
  protected String getWord() {
    return word;
  }

  /**
   * @return Information on this note's start, end, pitch, and channel
   */
  @Override
  public String toString() {
    return "{start: " + start + " end: " + end + " pitch: " + pitch + " channel: " + channel + "}";
  }
}
