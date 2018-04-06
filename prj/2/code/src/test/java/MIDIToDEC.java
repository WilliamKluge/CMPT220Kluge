import edu.cmu.sphinx.util.TimeFrame;
import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import MIDIFileHandling.*;

/**
 * Shout out to my boi Sami Koivu from this SO page for the base code here:
 * https://stackoverflow.com/questions/3850688/reading-midi-files-in-java?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
 */
public class MIDIToDEC {

  public static final int NOTE_ON = 0x90;
  public static final int NOTE_OFF = 0x80;
  public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A",
      "A#", "B"};


  public static void main(String[] args) throws Exception {
    Sequence sequence = MidiSystem.getSequence(new File(args[0]));
    MidiLoader midiLoader = new MidiLoader(args[0]);

    double millisPerTick = (midiLoader.mySeq.getMicrosecondLength() / 1000.0)
        / midiLoader.mySeq.getTickLength();

    /* Array of phones that take place in the source audio in sequential order */
    ArrayList<ArrayList<DECtalkPhone>> dectalkPhones = new ArrayList<>();

    for (int i = 0; i < midiLoader.tracks.size(); ++i) {
      ArrayList<DECtalkPhone> trackDECList = new ArrayList<>();
      // Tracks for phones that TODO problem is that the pause from the last phone is not mattering
      ArrayList<ArrayList<DECtalkPhone>> extraTracks = new ArrayList<>();
      trackDECList.add(new DECtalkPhone(new TimeFrame(0, 0))); // Adds a blank phone

      for (Note note : midiLoader.trackAsArrayList(i)) {
        DECtalkPhone phone = new DECtalkPhone(new TimeFrame((long) (note.start * millisPerTick),
            (long)((note.start + note.duration) * millisPerTick)), "duw");
        phone.setToneNumber(PitchAnalysis.pianoKeyToToneNumber(note.pitch));

        // Add the phone to the first track with space for it
        boolean added = false;
        for (ArrayList<DECtalkPhone> track : dectalkPhones) {
          if (canFitInTrack(track, phone)) {
            addWithPauses(track, phone);
            added = true;
            break;
          }
        }

        if (!added) {
          ArrayList<DECtalkPhone> newTrack = new ArrayList<>();
          newTrack.add(new DECtalkPhone(new TimeFrame(0, 0))); // Adds a blank phone
          addWithPauses(newTrack, phone);
          dectalkPhones.add(newTrack);
        }
      }

      dectalkPhones.add(trackDECList);
    }

    // Remove empty phones TODO bruh get rid of these...too simple
    for (ArrayList<DECtalkPhone> track : dectalkPhones) {
      track.remove(0);
    }
    // Remove empty tracks
    dectalkPhones.removeIf(ArrayList::isEmpty);

    // Find earliest start time TODO honestly these can all be smash together if you think about it
    long earliestStartTime = dectalkPhones.get(0).get(0).getTimeFrame().getStart();
    for (ArrayList<DECtalkPhone> track : dectalkPhones) {
      long trackStart = track.get(0).getTimeFrame().getStart();
      if (trackStart < earliestStartTime) {
        earliestStartTime = trackStart;
      }
    }

    // Add pauses
    for (ArrayList<DECtalkPhone> track : dectalkPhones) {
      long trackStart = track.get(0).getTimeFrame().getStart();
      if (trackStart > earliestStartTime) {
        track.add(0, new DECtalkPhone(new TimeFrame(earliestStartTime, trackStart)));
      }
    }

    // Print DEC syntax
    System.out.println("[:phoneme on]");

    for (ArrayList<DECtalkPhone> decTrack : dectalkPhones) {
      if (decTrack.isEmpty()) {
        continue;
      }
      System.out.print("[");
      DECtalkPhone lastPhone = decTrack.get(0);
      for (DECtalkPhone phone : decTrack) {
        System.out.print(phone.generatePauseFromLast(lastPhone.getTimeFrame().getEnd()));
        System.out.print(phone.toString());
        lastPhone = phone;
      }
      System.out.println("]");
    }
  }

  /* TODO get this to ignore the vocal track
   *
   * @param track
   * @param newPhone
   * @return
   */
  private static boolean canFitInTrack(ArrayList<DECtalkPhone> track, DECtalkPhone newPhone) {
    TimeFrame lastPhoneFrame = track.get(track.size() - 1).getTimeFrame();
    TimeFrame thisFrame = newPhone.getTimeFrame();
    return  !(thisFrame.getStart() <= lastPhoneFrame.getEnd());
  }

  private static void addWithPauses(ArrayList<DECtalkPhone> track, DECtalkPhone phone) {
    TimeFrame lastPhoneTimeFrame = track.get(track.size() - 1).getTimeFrame();
    if (phone.getTimeFrame().getStart() - 1 > lastPhoneTimeFrame.getEnd() + 1) {
      track.add(new DECtalkPhone(new TimeFrame(lastPhoneTimeFrame.getEnd() + 1,
          phone.getTimeFrame().getStart() - 1)));
    }
    track.add(phone);
  }
}