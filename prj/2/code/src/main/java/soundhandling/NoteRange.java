package soundhandling;

import notes.DECNote;
import notes.MIDINote;
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
  /* Amount that the volume should be shifted for files with audio in this range */
  private float volumeShift;

  // Add method for DEC tone scaling here? that would scale by a range, which would be :ok_hand:
  // If scaling was done here I could use one object for all notes within one range, change one
  // and all scaling would be changed for that object, which would be sweet


  /**
   * Creates a NoteRange with the default range buffers.
   *
   * @param voiceCommands DECtalk voice commands that can be used by this class
   * @param lowestNote Lowest note (in terms of piano keys) that is allowed in this range
   * @param highestNote Highest note (in terms of piano keys) that is allowed in this range
   * @param volumeShift Amount of decibels to shift the volume by
   */
  public NoteRange(int lowestNote, int highestNote, float volumeShift, String... voiceCommands) {
    this.voiceCommands = voiceCommands;
    this.highestNote = highestNote;
    this.lowestNote = lowestNote;
    this.volumeShift = volumeShift;
    rangeBufferUp = 5; // Default values for range buffer
    rangeBufferDown = 5;
  }

  /**
   * Creates a NoteRange with non-default values for its range buffers.
   *
   * @param voiceCommands DECtalk voice commands that can be used by this class
   * @param highestNote Highest note (in terms of piano keys) that is allowed in this range
   * @param lowestNote Lowest note (in terms of piano keys) that is allowed in this range
   * @param rangeBufferUp Amount of keys (piano) allowed above the highest note
   * @param rangeBufferDown Amount of keys (piano) allowed below the lowest note
   * @param volumeShift Amount of decibels to shift the volume by
   * @return A NoteRange with the specified qualities
   */
  public static NoteRange createWithSpecificRange(int highestNote,
      int lowestNote, int rangeBufferUp, int rangeBufferDown, float volumeShift,
      String... voiceCommands) {
    NoteRange range = new NoteRange(highestNote, lowestNote, volumeShift, voiceCommands);
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

  /**
   * Calculates if a given pitch is within the range of this object
   *
   * @param pitch Pitch to check
   * @return If the pitch fits in this range
   */
  private boolean fitsInRange(int pitch) {
    boolean fits = false;

    if (pitch >= lowestNote - rangeBufferDown && pitch <= highestNote + rangeBufferUp) {
      fits = true;
    }

    return fits;
  }

  /**
   * @return DECtalk voice commands associated with this range
   */
  public String[] getVoiceCommands() {
    return voiceCommands;
  }

  /**
   * Update the highest and lowest notes of this range using information from a track of DECNotes.
   * @param DECTrack Track to update data
   * @param useKeys If the method should use piano keys for the note's value instead of it's pitch.
   */
  public void updateRange(ArrayList<DECNote> DECTrack, boolean useKeys) {
    for (DECNote note : DECTrack) {
      int notePitch = useKeys ? note.getPianoKey() : note.getPitch();
      if (notePitch > highestNote) {
        highestNote = notePitch;
      }
      if (notePitch < lowestNote) {
        lowestNote = notePitch;
      }
    }
  }

  /**
   * @return Highest note in this range
   */
  public int getHighestNote() {
    return highestNote;
  }

  /**
   * @return Lowest note in this range
   */
  public int getLowestNote() {
    return lowestNote;
  }

  /**
   * @return Amount of decibles to shift the volume of clips in this range by
   */
  public float getVolumeShift() {
    return volumeShift;
  }

  /**
   * @return The range of notes in this range formatted in a constant way
   */
  @Override
  public String toString() {
    return String.format("%03d", lowestNote) + " " + String.format("%03d", highestNote);
  }
}
