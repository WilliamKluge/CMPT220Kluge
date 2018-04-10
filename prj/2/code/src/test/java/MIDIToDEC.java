import edu.cmu.sphinx.util.TimeFrame;
import java.io.File;

import java.util.ArrayList;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import MIDIFileHandling.*;

/**
 * Shout out to my boi Sami Koivu from this SO page for the base code here:
 * https://stackoverflow.com/questions/3850688/reading-midi-files-in-java?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
 */
public class MIDIToDEC {

  /* Maximum length of a pause before it gets broken up */
  public final static int MAX_WAIT_LENGTH = 16000;
  /* Where base turns to treble */
  public final static int MIDI_MIDDLE_C = 60;
  /**/
  private final static String[] VOICE_SETTINGS = new String[] {"[:np]", "[:nh]", "[:nf]", "[:nd]"};

  public static void main(String[] args) throws Exception {
    Sequence sequence = MidiSystem.getSequence(new File(args[0]));
    MidiLoader midiLoader = new MidiLoader(args[0]);

    int PPQ = midiLoader.mySeq.getResolution();
    int BPM = 100; // This can change a lot...time to calculate it!
    int ticksPerMinute = BPM * PPQ;
    double ticksPerMillis = ticksPerMinute * (1.0 / 60000);

    /* Array of phones that take place in the source audio in sequential order */
    ArrayList<ArrayList<DECtalkPhone>> dectalkPhones = new ArrayList<>();
    ArrayList<DECtalkPhone> allPhones = new ArrayList<>();
    ArrayList<ArrayList<Note>> clefSeparation = new ArrayList<>();

    int soundTrack = 0;
    for (int i = 0; i < midiLoader.tracks.size(); ++i) {
      ArrayList<DECtalkPhone> trackDECList = new ArrayList<>();
//      trackDECList.add(new DECtalkPhone(new TimeFrame(0, 0))); // Adds a blank phone
      ArrayList<Note> separateClefs = new ArrayList<>();

      int lastNote = -1;

      for (Note note : midiLoader.trackAsArrayList(i)) {

        if (lastNote != -1 && (lastNote < MIDI_MIDDLE_C && note.pitch >= MIDI_MIDDLE_C)
            || (lastNote >= MIDI_MIDDLE_C && note.pitch < MIDI_MIDDLE_C)) {
          separateClefs.add(note);
        } else {
          lastNote = note.pitch;
          DECtalkPhone phone = new DECtalkPhone(new TimeFrame((long) (note.start / ticksPerMillis),
              (long) ((note.start + note.duration) / ticksPerMillis)), "uw");
          phone.setToneNumber(PitchAnalysis.pianoKeyToToneNumber(note.pitch));
          phone.setTrack(soundTrack);
          allPhones.add(phone);
        }

      }

      if (midiLoader.trackAsArrayList(i).size() > 0) {
        ++soundTrack;
      }

      if (separateClefs.size() > 0) {
        clefSeparation.add(separateClefs);
      }

      if (dectalkPhones.size() > 0) {
        dectalkPhones.add(trackDECList);
      }
    }

    // Get the notes that were to different from other notes in allPhones as well
    for (int i = 0; i < clefSeparation.size(); ++i) {

      for (Note note : clefSeparation.get(i)) {
        DECtalkPhone phone = new DECtalkPhone(new TimeFrame((long) (note.start / ticksPerMillis),
            (long) ((note.start + note.duration) / ticksPerMillis)), "uw");
        phone.setToneNumber(PitchAnalysis.pianoKeyToToneNumber(note.pitch));
        phone.setTrack(soundTrack + i); // Sound track is the last one + whichever thing we're on
        allPhones.add(phone);
      }

    }

    // Remove empty phones TODO bruh get rid of these...too simple
//    for (ArrayList<DECtalkPhone> track : dectalkPhones) {
//      track.remove(0);
//    }
    // Remove empty tracks
    dectalkPhones.removeIf(ArrayList::isEmpty);

    // Find earliest start time TODO honestly these can all be smash together if you think about it
//    long earliestStartTime = dectalkPhones.get(0).get(0).getTimeFrame().getStart();
//    for (ArrayList<DECtalkPhone> track : dectalkPhones) {
//      long trackStart = track.get(0).getTimeFrame().getStart();
//      if (trackStart < earliestStartTime) {
//        earliestStartTime = trackStart;
//      }
//    }

    // Add pauses
    for (ArrayList<DECtalkPhone> track : dectalkPhones) {
      long trackStart = track.get(0).getTimeFrame().getStart();
      if (trackStart > 0) {
        track.add(0, new DECtalkPhone(new TimeFrame(0, trackStart)));
      }
    }

    ArrayList<ArrayList<DECtalkPhone>> builtTracks = new ArrayList<>();

    ArrayList<DECtalkPhone> buildingTrack = new ArrayList<>();
    buildingTrack.add(allPhones.get(0));

    phoneLoop:
    for (int i = 1; i < allPhones.size(); ++i) {
      DECtalkPhone phone = allPhones.get(i);

//      for (ArrayList<DECtalkPhone> track : builtTracks) {
//        DECtalkPhone lastTrackPhone = track.get(track.size() - 1);
//
//        if (phone.getTimeFrame().getStart() > lastTrackPhone.getTimeFrame().getEnd()) {
//          addWithPauses(track, phone);
//          continue phoneLoop; // Eyyyy it's good man :ok_hand:
//        }
//      }

      DECtalkPhone lastBuildingTrackPhone = buildingTrack.get(buildingTrack.size() - 1);
      if (phone.getTimeFrame().getStart() > lastBuildingTrackPhone.getTimeFrame().getEnd()
          && phone.getTrack() == lastBuildingTrackPhone.getTrack()) {
        addWithPauses(buildingTrack, phone);
      } else {
        // Phone does not fit into any possible tracks
        builtTracks.add(new ArrayList<>(buildingTrack));
        buildingTrack.clear();
        addWithPauses(buildingTrack, phone);
      }

    }

    int i = 0;
    for (ArrayList<DECtalkPhone> t : builtTracks) {
      i += t.size();
    }

    System.out.println("Generating wav files");
    int wavName = 0;
    for (ArrayList<DECtalkPhone> decTrack : builtTracks) {
      if (decTrack.isEmpty()) {
        continue;
      }
      StringBuilder command = new StringBuilder("[");
      DECtalkPhone lastSoundPhone = new DECtalkPhone(new TimeFrame(0, 0));
      int track = 0;
      for (DECtalkPhone phone : decTrack) {
        // TODO Separate notes by tone , only put highs together, lows together, user adjusts volume
        command.append("]").append(VOICE_SETTINGS[phone.getTrack()]).append("["); // TODO dangerous
        command.append(PitchVolumeController.volumeShift(lastSoundPhone, phone));
        if (!phone.getPhone().equals("_")) {
          // Keep this so that it's easier to compare the sounds of phones
          lastSoundPhone = phone;
          track = phone.getTrack();
        }
        command.append(phone.toString());
      }
      command.append("]");

      File exe = new File("dectalk\\say.exe");
      if (!exe.exists()) {
        System.out.println("Exe doesn't exist");
      }

      // -w generated\0.wav < generated\0.txt
      System.out.println(command.toString());
      ProcessBuilder pb = new ProcessBuilder("dectalk\\say.exe",
          "-w", "generated\\" + track + "t" + wavName + ".wav",
          "-pre",
          "\"[:phoneme on]\"",
          command.toString());
      pb.directory(new File("dectalk\\"));
      pb.start();

      ++wavName; //47
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
    return !(thisFrame.getStart() <= lastPhoneFrame.getEnd());
  }

  private static void addWithPauses(ArrayList<DECtalkPhone> track, DECtalkPhone phone) {
    if (track.size() > 0) {
      TimeFrame lastPhoneTimeFrame = track.get(track.size() - 1).getTimeFrame();
      if (phone.getTimeFrame().getStart() - 1 > lastPhoneTimeFrame.getEnd() + 1) {
        track.add(new DECtalkPhone(new TimeFrame(lastPhoneTimeFrame.getEnd() + 1,
            phone.getTimeFrame().getStart() - 1)));
      }
      track.add(phone);
    } else {
      long phoneStart = phone.getTimeFrame().getStart();
      if (phoneStart > 0) {
        long pauseTime = phoneStart - 1;
        int i = 1;
        while (pauseTime > MAX_WAIT_LENGTH) {
          track.add(new DECtalkPhone(new TimeFrame(MAX_WAIT_LENGTH * i,
              MAX_WAIT_LENGTH * (i + 1) - 1)));
          ++i;
          pauseTime -= MAX_WAIT_LENGTH;
        }
        if (pauseTime > 0) {
          track.add(new DECtalkPhone(new TimeFrame(MAX_WAIT_LENGTH * i,
              MAX_WAIT_LENGTH * i + pauseTime)));
        }
      }
      track.add(phone);
    }
  }
}