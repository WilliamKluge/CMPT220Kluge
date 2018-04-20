package SoundHandling;

import Notes.DECNote;
import Notes.MIDINote;
import java.util.ArrayList;

public class NoteRange {

  /* DECtalk voice commands to be used on notes in this range */
  private String[] voiceCommands;
  /* Lowest note (in terms of piano keys) regularly allowed in this range */
  private int highestNote;
  /* Highest note (in terms of piano keys) regularly allowed in this range */
  private int lowestNote;
  /* Amount of notes (in terms of piano keys) that this range allows above its highestNote */
  private int rangeBufferUp;
  /* Amount of notes (in terms of piano keys) that this range allows below its lowestNote */
  private int rangeBufferDown;

  // Add method for DEC tone scaling here? that would scale by a range, which would be :ok_hand:
  // If scaling was done here I could use one object for all notes within one range, change one
  // and all scaling would be changed for that object, which would be sweet


  /**
   * Creates a NoteRange with the default range buffers.
   *
   * @param voiceCommands DECtalk voice commands that can be used by this class
   * @param lowestNote Lowest note (in terms of piano keys) that is allowed in this range
   * @param highestNote Highest note (in terms of piano keys) that is allowed in this range
   */
  public NoteRange(int lowestNote, int highestNote, String... voiceCommands) {
    this.voiceCommands = voiceCommands;
    this.highestNote = highestNote;
    this.lowestNote = lowestNote;
    rangeBufferUp = 0; // Default values for range buffer
    rangeBufferDown = 0;
  }

  /**
   * Creates a NoteRange with non-default values for its range buffers.
   *
   * @param voiceCommands DECtalk voice commands that can be used by this class
   * @param highestNote Highest note (in terms of piano keys) that is allowed in this range
   * @param lowestNote Lowest note (in terms of piano keys) that is allowed in this range
   * @param rangeBufferUp Amount of keys (piano) allowed above the highest note
   * @param rangeBufferDown Amount of keys (piano) allowed below the lowest note
   * @return A NoteRange with the specified qualities
   */
  public static NoteRange createWithSpecificRange(int highestNote,
      int lowestNote, int rangeBufferUp, int rangeBufferDown, String... voiceCommands) {
    NoteRange range = new NoteRange(highestNote, lowestNote, voiceCommands);
    range.rangeBufferUp = rangeBufferUp;
    range.rangeBufferDown = rangeBufferDown;
    return range;
  }

  /**
   * Checks if a given DECNote fits into this range. Does not check if their times overlap.
   *
   * @param note Note to check with
   * @return If the note fits in this range
   */
  public boolean isInRange(DECNote note) {
    return fitsInRange(note.getPianoKey());
  }

  /**
   * Checks if a given DECNote fits into a NoteRange (presumably of another DECNote). Does not check
   * if their times overlap.
   *
   * @param range Range to check if the note exists in
   * @param note Note to check with
   * @return If the note fits in the given range
   */
  public static boolean isInRange(NoteRange range, DECNote note) {
    return range.isInRange(note);
  }

  /**
   * Checks if a given MIDINote fits into this range.
   *
   * @param note Note to check with
   * @return If the note fits in this range.
   */
  public boolean isInRange(MIDINote note) {
    return fitsInRange(note.getPitch());
  }

  /**
   * Checks if a given MIDINote fits into a NoteRange.
   *
   * @param range Range to check if the note exists in
   * @param note Note to check with
   * @return If the note fits in the given range
   */
  public static boolean isInRange(NoteRange range, MIDINote note) {
    return range.fitsInRange(note.getPitch());
  }

  private boolean fitsInRange(int pitch) {
    boolean fits = false;

    if (pitch >= lowestNote - rangeBufferDown && pitch <= highestNote + rangeBufferUp) {
      fits = true;
    }

    return fits;
  }

  public String[] getVoiceCommands() {
    return voiceCommands;
  }
}
