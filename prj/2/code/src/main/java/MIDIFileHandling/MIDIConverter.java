package MIDIFileHandling;

import Notes.DECNote;
import Notes.MIDINote;
import SoundHandling.NoteRange;
import java.util.ArrayList;

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
  /* If printing of debugging information should be done */
  private final static boolean PRINT_DEBUG = false;
  /* Sound to use for all instrumental parts */
  private final static String INSTRUMENT_SOUND = "ah";
  /* Loads MIDI file */
  private MidiLoader midiLoader;
  /* Number of MIDI ticks that occur per millisecond */
  private double ticksPerMillis;
  /* NoteRanges to use while converting MIDIs */
  private ArrayList<NoteRange> ranges;

  /**
   *
   * @param filePath Path to the MIDI file to convert
   * @param ranges NoteRanges to use for separating notes
   */
  public MIDIConverter(String filePath, ArrayList<NoteRange> ranges) {

    midiLoader = new MidiLoader(filePath);

    int PPQ = midiLoader.mySeq.getResolution();
    int BPM = 80; // This can change a lot...time to calculate it!
    int ticksPerMinute = BPM * PPQ;
    ticksPerMillis = ticksPerMinute * (1.0 / 60000);

    this.ranges = ranges;

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

    if (PRINT_DEBUG) {
      System.out.println("Sum of MIDI track: " + sumOfTracks(midiLoader.tracks));
    }

    // For every MIDI track (tracks copied so that notes can be taken out without damaging the data)
    for (ArrayList<MIDINote> midiTrack : new ArrayList<>(midiLoader.tracks)) {
      // While there are still notes to create from
      while (midiTrack.size() > 0) {
        DECTracks.add(createDECTrack(midiTrack, channelID));
        ++channelID;
      }

      if (PRINT_DEBUG) {
        System.out.println("Done with track");
      }
    }

    if (PRINT_DEBUG) {
      System.out.println("Sum of DEC track: " + sumOfTracks(DECTracks));
    }

    return DECTracks;

  }

  private<T> long sumOfTracks(ArrayList<ArrayList<T>> tracks) {
    long sum = 0;
    for (ArrayList<T> track : tracks) {
      sum += track.size();
    }
    return sum;
  }

  /**
   * Creates a track of DECNotes that belong together (no overlapping, no large jumps in pitch).
   * Whenever this adds a note to the track it creates, that note get removed from MIDITrack. When
   * all notes have been added, the size of MIDITrack will be 0.
   *
   *
   * @param MIDITrack Track of DECNotes in the format of a MIDITrack (noOverlap allowed) to create
   * out of.
   * @return A track of DECNotes with their pitch defined by a piano key
   * TODO fix: some notes are getting lost somehow
   */
  private ArrayList<DECNote> createDECTrack(ArrayList<MIDINote> MIDITrack, int channelID) {

    ArrayList<DECNote> DECTrack = new ArrayList<>();

    // Get the range of the first note, which will be the only range to allow in this iteration
    NoteRange trackRange = null;
    for (int i = 0; trackRange == null && i < ranges.size(); ++i) {
      MIDINote firstNote = MIDITrack.get(0);
      if (ranges.get(i).isInRange(firstNote)) {
        trackRange = ranges.get(i);
      }
    }

    // If the note was a pitch not in any NoteRange added to the program
    if (trackRange == null) {
      throw new UnsupportedOperationException("An unsupported note, "
          + MIDITrack.get(0).getPitch() + ", was found. Contact developer about support options");
    }

    // Actually make the track here by adding any notes from MIDI that fit
    for (int i = 0; i < MIDITrack.size(); ++i) {
      MIDINote midiNote = MIDITrack.get(i);
      // If the note does not overlap with the last note of the track and is in the trackRange
      if (DECTrack.size() == 0 || noOverlap(midiNote, DECTrack.get(DECTrack.size() - 1))
          && trackRange.isInRange(midiNote)) {
        DECNote decNote = new DECNote(midiNote, ticksPerMillis, trackRange, INSTRUMENT_SOUND);
        decNote.setChannel(channelID);
        DECTrack.add(decNote);
        MIDITrack.remove(midiNote);
        --i; // Take into account the fact that we removed the current one
      }
    }

    return DECTrack;

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

}
