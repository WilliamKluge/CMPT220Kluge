package midifilehandling;

import notes.DECNote;
import notes.MIDINote;
import soundhandling.NoteRange;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Converts a MIDI file into a DECtalk file
 */
public class MIDIConverter {

  /* If printing of debugging information should be done */
  private final static boolean PRINT_DEBUG = false;
  /* Sound to use for all instrumental parts */
  private final static String INSTRUMENT_SOUND = "ah";
  /* Loads MIDI file */
  private MidiLoader midiLoader;
  /* Number of MIDI ticks that occur per millisecond */
  private double ticksPerMillis;
  /* NoteRanges for converting MIDIs mapped to the number of times that range has been used */
  private Map<NoteRange, Integer> rangeMap;
  private ArrayList<NoteRange> ranges;
  /**
   * BPM to use for all MIDI files. Files must use this at all times for the program to work.
   */
  public final static int BPM = 110;



  /**
   * @param filePath Path to the MIDI file to convert
   * @param ranges NoteRanges to use for separating notes
   */
  public MIDIConverter(String filePath, ArrayList<NoteRange> ranges) {

    midiLoader = new MidiLoader(filePath);

    int PPQ = midiLoader.mySeq.getResolution();
    int ticksPerMinute = BPM * PPQ;
    ticksPerMillis = ticksPerMinute * (1.0 / 60000);

    this.ranges = ranges;
    this.rangeMap = new HashMap<>();
    ranges.forEach(a -> this.rangeMap.put(a, 0));

  }

  /**
   * All DEC tracks made from the MIDI file this class was given. DEC tracks do not have multiple
   * notes that play at the same time and they do not allowed notes in different octaves unless they
   * are within the specified range.
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

  /**
   * Provides a sum of all the elements in the given multidimensional array
   *
   * @param tracks Tracks to sum
   * @param <T> Any type of multidimensional arraylist can be summed
   * @return The sum of all elements in tracks
   */
  private <T> long sumOfTracks(ArrayList<ArrayList<T>> tracks) {
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
   * @param MIDITrack Track of DECNotes in the format of a MIDITrack (noOverlap allowed) to create
   * out of.
   * @return A track of DECNotes with their pitch defined by a piano key
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

    int trackIterationChannel = rangeMap.get(trackRange);

    // Actually make the track here by adding any notes from MIDI that fit
    for (int i = 0; i < MIDITrack.size(); ++i) {
      MIDINote midiNote = MIDITrack.get(i);
      // If the note does not overlap with the last note of the track and is in the trackRange
      if (DECTrack.size() == 0 || noOverlap(midiNote, DECTrack.get(DECTrack.size() - 1))
          && trackRange.isInRange(midiNote)) {
        DECNote decNote = new DECNote(midiNote, ticksPerMillis, trackRange, INSTRUMENT_SOUND);
        decNote.setChannel(trackIterationChannel);
        DECTrack.add(decNote);
        MIDITrack.remove(midiNote);
        --i; // Take into account the fact that we removed the current one
      }
    }

    // Tell the map that we used this range, now it will select the next voice
    rangeMap.put(trackRange, trackIterationChannel + 1);

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
