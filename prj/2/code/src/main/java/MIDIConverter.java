import MIDIFileHandling.MidiLoader;
import Notes.DECNote;
import java.util.ArrayList;

/**
 * Converts a MIDI file into a DECtalk file
 */
public class MIDIConverter {
  /* Loads MIDI file */
  private MidiLoader midiLoader;

  public MIDIConverter(String filePath) {

    midiLoader = new MidiLoader(filePath);

  }



}
