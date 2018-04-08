import edu.cmu.sphinx.util.TimeFrame;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import MIDIFileHandling.*;
import sun.security.krb5.internal.crypto.Aes128;

/**
 * Shout out to my boi Sami Koivu from this SO page for the base code here:
 * https://stackoverflow.com/questions/3850688/reading-midi-files-in-java?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
 */
public class MIDIToDEC {

  /* Maximum length of a pause before it gets broken up */
  public final static int MAX_WAIT_LENGTH = 16000;

  public static void main(String[] args) throws Exception {
    Sequence sequence = MidiSystem.getSequence(new File(args[0]));
    MidiLoader midiLoader = new MidiLoader(args[0]);

    int PPQ = midiLoader.mySeq.getResolution();
    int BPM = 120; // This can change a lot...time to calculate it!
    int ticksPerMinute = BPM * PPQ;
    double millisPerTick = (midiLoader.mySeq.getMicrosecondLength() / 1000.0)
        / midiLoader.mySeq.getTickLength();
    double ticksPerMillis = ticksPerMinute * (1.0 / 60000);

    /* Array of phones that take place in the source audio in sequential order */
    ArrayList<ArrayList<DECtalkPhone>> dectalkPhones = new ArrayList<>();
    ArrayList<DECtalkPhone> allPhones = new ArrayList<>();

    for (int i = 0; i < midiLoader.tracks.size(); ++i) {
      ArrayList<DECtalkPhone> trackDECList = new ArrayList<>();
      // Tracks for phones that TODO problem is that the pause from the last phone is not mattering
      ArrayList<ArrayList<DECtalkPhone>> extraTracks = new ArrayList<>();
      trackDECList.add(new DECtalkPhone(new TimeFrame(0, 0))); // Adds a blank phone

      for (Note note : midiLoader.trackAsArrayList(i)) {
        DECtalkPhone phone = new DECtalkPhone(new TimeFrame((long) (note.start / ticksPerMillis),
            (long) ((note.start + note.duration) / ticksPerMillis)), "duw");
        phone.setToneNumber(PitchAnalysis.pianoKeyToToneNumber(note.pitch));
        allPhones.add(phone);

        // Add the phone to the first track with space for it
//        boolean added = false;
//        for (ArrayList<DECtalkPhone> track : dectalkPhones) {
//          if (canFitInTrack(track, phone)) {
//            addWithPauses(track, phone);
//            added = true;
//            break;
//          }
//        }
//
//        if (!added) {
//          ArrayList<DECtalkPhone> newTrack = new ArrayList<>();
//          newTrack.add(new DECtalkPhone(new TimeFrame(0, 0))); // Adds a blank phone
//          addWithPauses(newTrack, phone);
//          dectalkPhones.add(newTrack);
//        }
      }

      dectalkPhones.add(trackDECList);
    }

    // Remove empty phones TODO bruh get rid of these...too simple
//    for (ArrayList<DECtalkPhone> track : dectalkPhones) {
//      track.remove(0);
//    }
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

    ArrayList<ArrayList<DECtalkPhone>> builtTracks = new ArrayList<>();

    ArrayList<DECtalkPhone> buildingTrack = new ArrayList<>();
    buildingTrack.add(allPhones.get(0));

    phoneLoop:
    for (int i = 1; i < allPhones.size(); ++i) {
      DECtalkPhone phone = allPhones.get(i);

      for (ArrayList<DECtalkPhone> track : builtTracks) {
        DECtalkPhone lastTrackPhone = track.get(track.size() - 1);

        if (phone.getTimeFrame().getStart() > lastTrackPhone.getTimeFrame().getEnd()) {
          addWithPauses(track, phone);
          continue phoneLoop; // Eyyyy it's good man :ok_hand:
        }
      }

      DECtalkPhone lastBuildingTrackPhone = buildingTrack.get(buildingTrack.size() - 1);
      if (phone.getTimeFrame().getStart() > lastBuildingTrackPhone.getTimeFrame().getEnd()) {
        addWithPauses(buildingTrack, phone);
      } else {
        // Phone does not fit into any possible tracks
        builtTracks.add(new ArrayList<>(buildingTrack));
        buildingTrack.clear();
        addWithPauses(buildingTrack, phone);
      }

    }

    // Print DEC syntax
//    System.out.println("[:phoneme on]");
//
//    for (ArrayList<DECtalkPhone> decTrack : builtTracks) {
//      if (decTrack.isEmpty()) {
//        continue;
//      }
//      System.out.print("[");
//      DECtalkPhone lastPhone = decTrack.get(0);
//      for (DECtalkPhone phone : decTrack) {
//        System.out.print(phone.generatePauseFromLast(lastPhone.getTimeFrame().getEnd()));
//        System.out.print(phone.toString());
//        lastPhone = phone;
//      }
//      System.out.println("]");
//    }

    System.out.println("Generating wav files");
    int wavName = 0;
    for (ArrayList<DECtalkPhone> decTrack : builtTracks) {
      if (decTrack.isEmpty()) {
        continue;
      }
      StringBuilder command = new StringBuilder("[");
      for (int i = 0; i < decTrack.size(); ++i) { // Problem was here
        DECtalkPhone phone = decTrack.get(i);
        command.append(phone.toString());
      }
      command.append("]");

//      {
//        File decFile = new File("dectalk/generated/" + wavName + ".txt");
//        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(decFile));
//        fileWriter.write(command.toString());
//        fileWriter.close();
//      }
      File exe = new File("dectalk\\say.exe");
      if (!exe.exists()) {
        System.out.println("Exe doesn't exist");
      }

      // -w generated\0.wav < generated\0.txt
      System.out.println(command.toString());
      ProcessBuilder pb = new ProcessBuilder("dectalk\\say.exe",
          "-w", "generated\\" + wavName + ".wav",
          "-pre",
          "\"[:phoneme on]\"",
          command.toString());
      pb.directory(new File("dectalk\\"));
      System.out.println("Command: " + pb.command());
      Process p = pb.start();
      // To capture output from the shell
//      InputStream shellIn = p.getInputStream();
//
//      // Wait for the shell to finish and get the return code
//      int shellExitStatus = p.waitFor();
//      System.out.println("Exit status" + shellExitStatus);
//
//      String response = convertStreamToStr(shellIn);
//      System.out.print(response);
//
//      shellIn.close();

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
          track.add(new DECtalkPhone(new TimeFrame(MAX_WAIT_LENGTH * i, MAX_WAIT_LENGTH * i)));
        }
      }
      track.add(phone);
    }
  }
}