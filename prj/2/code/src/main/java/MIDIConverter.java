import MIDIFileHandling.MidiLoader;
import Notes.DECNote;
import Notes.MIDINote;
import java.util.ArrayList;
import javafx.util.Pair;

/**
 * Converts a MIDI file into a DECtalk file
 */
public class MIDIConverter {

  /* Number that represents middle c (base/treable split) in MIDI files */
  private final static int MIDI_MIDDLE_C = 60;
  /* Regions for splitting up keys in a MIDI file into DECtalk track */
  private final static int TRACK_NOTE_SEPARATION = 60;
  /* Maximum number of keys into a different octave a note is allowed to be to still be part of
   * the same DEC track */
  private final static int DIFFERENT_OCTAVE_BUFFER = 10;
  /* Sound to use for all instrumental parts */
  private final static String INSTRUMENT_SOUND = "ah";
  /* Loads MIDI file */
  private MidiLoader midiLoader;
  /* Number of MIDI ticks that occur per millisecond */
  private double ticksPerMillis;

  public MIDIConverter(String filePath) {

    midiLoader = new MidiLoader(filePath);

    int PPQ = midiLoader.mySeq.getResolution();
    int BPM = 120; // This can change a lot...time to calculate it!
    int ticksPerMinute = BPM * PPQ;
    ticksPerMillis = ticksPerMinute * (1.0 / 60000);

  }

  /**
   * All DEC tracks made from the MIDI file this class was given. DEC tracks do not have
   * multiple notes that play at the same time and they do not allowed notes in different octaves
   * unless they are within the specified range.
   *
   * @return All DEC tracks made from the MIDI file this class was given.
   */
  public ArrayList<ArrayList<DECNote>> getDECTracks() {
    ArrayList<ArrayList<DECNote>> DECTracks = new ArrayList<>();
    int channelID = 0;

    // For every MIDI track (tracks copied so that notes can be taken out without damaging the data)
    for (ArrayList<MIDINote> midiTrack : new ArrayList<>(midiLoader.tracks)) {
      while (midiTrack.size() > 0) {
        DECTracks.add(createDECTrack(midiTrack, channelID));
        ++channelID;
      }
    }

    return DECTracks;

  }

  /**
   * Creates a track of DECNotes that belong together (no overlapping, no large jumps in pitch).
   * Whenever this adds a note to the track it creates, that note get removed from MIDITrack. When
   * all notes have been added the size of MIDITrack will be 0.
   *
   * @param MIDITrack Track of DECNotes in the format of a MIDITrack (noOverlap allowed) to create
   * out of.
   * @return A track of DECNotes.
   */
  private ArrayList<DECNote> createDECTrack(ArrayList<MIDINote> MIDITrack, int channelID) {

    ArrayList<DECNote> DECTrack = new ArrayList<>();

    for (int i = 0; i < MIDITrack.size(); ++i) {
      MIDINote note = MIDITrack.get(i);
      DECNote lastNote;

      if (DECTrack.size() == 0 || noOverlap(note, lastNote = DECTrack.get(DECTrack.size() - 1))
          && (isSameOctave(note.getPitch(), lastNote.getPitch())
          || isWithinDifferentOctaveRange(note.getPitch(), lastNote.getPianoKey()))) {
        // If the notes do not overlap and they are in the same octave or in the allowed range
        DECNote decNote = new DECNote(note, ticksPerMillis, INSTRUMENT_SOUND);
        decNote.setChannel(channelID);
        DECTrack.add(decNote);
        MIDITrack.remove(note);
        --i; // Take into account the fact that we removed the current one
      }
    }

    return DECTrack;

  }

  /**
   * Finds if two notes are in the same octave (both clef/both treble)
   *
   * @param firstNote First note to check (as piano key)
   * @param secondNote Second note to check (as piano key)
   * @return If the notes are in the same octave
   */
  private boolean isSameOctave(int firstNote, int secondNote) {
    boolean differentOctave = firstNote >= MIDI_MIDDLE_C && secondNote >= MIDI_MIDDLE_C;
    differentOctave = differentOctave || firstNote < MIDI_MIDDLE_C && secondNote < MIDI_MIDDLE_C;
    return differentOctave;
  }

  /**
   * Checks if the first note starts before the second note ends
   *
   * @param firstNote New note to check compatibility for
   * @param secondNote Note already in the track
   * @return If the notes noOverlap
   */
  private boolean noOverlap(MIDINote firstNote, DECNote secondNote) {
    return firstNote.getStartAsMillis(ticksPerMillis) > secondNote.getEndAsMillis();
  }

  /**
   * Checks if two notes (represented as piano keys) are within the range allowed for notes to be in
   * different octaves.
   *
   * @param firstPitch First note to check
   * @param secondPitch Second note to check
   * @return If the two pitches are allowed to be in the same track according to the space allowed
   * for notes to be in different octaves.
   */
  private boolean isWithinDifferentOctaveRange(int firstPitch, int secondPitch) {
    boolean withinRange = isSameOctave(firstPitch + DIFFERENT_OCTAVE_BUFFER,
        secondPitch);
    withinRange = withinRange || isSameOctave(firstPitch - DIFFERENT_OCTAVE_BUFFER, secondPitch);

    return withinRange;
  }

  /**
   * Checks if a pitch is within the same range as another based on TRACK_NOTE_SEPARATION
   * @param checkPitch Pitch to check
   * @param trackPitch Pitch to check against
   * @return If checkPitch and trackPitch are in the same range
   */
  private boolean inNoteSeparationRange(int checkPitch, int trackPitch) {
    int rangeLow, rangeHigh; // rangeHigh could go > 108, does that matter though?
    for (rangeLow = 1, rangeHigh = TRACK_NOTE_SEPARATION; rangeLow < TRACK_NOTE_SEPARATION;
        rangeLow += TRACK_NOTE_SEPARATION, rangeHigh += TRACK_NOTE_SEPARATION) {
      if (rangeLow <= trackPitch && trackPitch <= rangeHigh) {
        break;
      }
    }

    return rangeLow - DIFFERENT_OCTAVE_BUFFER <= checkPitch
        && checkPitch <= rangeHigh + DIFFERENT_OCTAVE_BUFFER;
  }

}
