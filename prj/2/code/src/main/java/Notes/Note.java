package Notes;

public abstract class Note {

  public enum TimeUnit {
    MIDI_TICK,
    MILLISECOND
  }

  public enum PitchUnit {
    PIANO_KEY,
    TONE_NUMBER,
    HZ
  }

  /* Time the note starts */
  private long start;
  /* Time the note ends */
  private long end;
  /* Channel of the note */
  private int channel;
  /* Velocity of the note */
  private int velocity;
  /* Pitch of the note in PitchUnit */
  private int pitch;

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
   * but this is a determining factor of which voice autoDEC assigns to the note.
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
    return getStartAsMillis(ticksPerMillis) - getEndAsMillis(ticksPerMillis);
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

  public int getChannel() {
    return channel;
  }

  public void setChannel(int channel) {
    this.channel = channel;
  }

  public int getVelocity() {
    return velocity;
  }

  public void setVelocity(int velocity) {
    this.velocity = velocity;
  }

  public int getPitch() {
    return pitch;
  }

  public void setPitch(int pitch) {
    this.pitch = pitch;
  }
}
