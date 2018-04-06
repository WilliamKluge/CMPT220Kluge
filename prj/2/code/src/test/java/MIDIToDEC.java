import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

/**
 * Shout out to my boi Sami Koivu from this SO page for the base code here:
 * https://stackoverflow.com/questions/3850688/reading-midi-files-in-java?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
 */
public class MIDIToDEC {

  public static final int NOTE_ON = 0x90;
  public static final int NOTE_OFF = 0x80;
  public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A",
      "A#", "B"};

  private static class MIDINote {

    public MIDINote(String note, int key, int velocity, long startTick) {
      this.note = note;
      this.key = key;
      this.velocity = velocity;
      this.startTick = startTick;
    }

    public String note;
    public int key;
    public int velocity;
    public long startTick;
    public long endTick = -1;

    @Override
    public String toString() {
      return "Note: " + note + " Key: " + key + " Velocity: " + velocity + " Start Tick: "
          + startTick + " End tick: " + endTick;
    }
  }

  public static void main(String[] args) throws Exception {
    Sequence sequence = MidiSystem.getSequence(new File(args[0]));
    ArrayList<MIDINote> midiNotes = new ArrayList<>();

    int trackNumber = 0;
    for (Track track : sequence.getTracks()) {
      trackNumber++;
      System.out.println("Track " + trackNumber + ": size = " + track.size());
      System.out.println();
      for (int i = 0; i < track.size(); i++) {
        MidiEvent event = track.get(i);
        System.out.print("@" + event.getTick() + " ");
        MidiMessage message = event.getMessage();
        if (message instanceof ShortMessage) {
          ShortMessage sm = (ShortMessage) message;
          System.out.print("Channel: " + sm.getChannel() + " ");
          if (sm.getCommand() == NOTE_ON) {
            int key = sm.getData1();
            int octave = (key / 12) - 1;
            int note = key % 12;
            String noteName = NOTE_NAMES[note];
            int velocity = sm.getData2();
            System.out.println(
                "Note on, " + noteName + octave + " key=" + key + " velocity: " + velocity);
            // Add MIDI event as a note
            midiNotes.add(new MIDINote(noteName, key, velocity, event.getTick()));
          } else if (sm.getCommand() == NOTE_OFF) {
            int key = sm.getData1();
            int octave = (key / 12) - 1;
            int note = key % 12;
            String noteName = NOTE_NAMES[note];
            int velocity = sm.getData2();
            System.out.println(
                "Note off, " + noteName + octave + " key=" + key + " velocity: " + velocity);
            // Yeah, this is terrible, just testing some shit
            for (MIDINote midiNote : midiNotes) {
              if ((midiNote.key == key) && midiNote.endTick == -1) {
              midiNote.endTick = event.getTick();
              }
            }
          } else {
            System.out.println("Command:" + sm.getCommand());
          }
        } else {
          System.out.println("Other message: " + message.getClass());
        }
      }

      System.out.println("Here are the notes I got");

      for (MIDINote midiNote : midiNotes) {
        System.out.println(midiNote.toString());
      }

      System.out.println();
    }

  }
}