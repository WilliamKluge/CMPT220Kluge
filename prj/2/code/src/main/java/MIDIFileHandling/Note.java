package MIDIFileHandling;

/**
 * Helper Class Note, stores start time, end time, channel, pitch and duration
 */
public class Note {

  public long start;
  public long duration;

  public int channel;
  public int velocity;
  public int pitch;

  Note(long theStart, long theDuration, int theChannel, int theVelocity, int thePitch) {
    start = theStart;
    channel = theChannel;
    pitch = thePitch;
    velocity = theVelocity;
    duration = theDuration;
  }
}
