package notes;

public class MIDINote extends Note {

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
  public MIDINote(long start, long end, int channel, int velocity, int pitch) {
    super(start, end, channel, velocity, pitch);
  }

  @Override
  public TimeUnit getTimeUnit() {
    return TimeUnit.MIDI_TICK;
  }

  @Override
  public PitchUnit getPitchUnit() {
    return PitchUnit.PIANO_KEY;
  }
}
