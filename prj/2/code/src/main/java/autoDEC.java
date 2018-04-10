import Notes.DECNote;
import edu.cmu.sphinx.util.TimeFrame;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Main class for autoDEC
 */
public class autoDEC {
  /* Name commands to use for different tracks */
  private final static String[] VOICE_SETTINGS = new String[] {"[:np]", "[:nh]", "[:nf]", "[:nd]"};

  public static void main(String[] args) {

    String inputFilePath = args[0];

    ArrayList<ArrayList<DECNote>> DECTracks = new ArrayList<>();

    if (inputFilePath.contains(".mid")) {
      DECTracks = new MIDIConverter(inputFilePath).getDECTracks();
    }

    System.out.println("Generating wav files");
    int wavName = 0;
    for (ArrayList<DECNote> decTrack : DECTracks) {
      StringBuilder command = new StringBuilder("[");
      int track = 0;
      for (DECNote phone : decTrack) {
        command.append("]").append(VOICE_SETTINGS[phone.getChannel()]).append("["); // TODO dangerous
        if (!phone.getPhone().equals("_")) {
          // Keep this so that it's easier to compare the sounds of phones
          track = phone.getChannel();
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
      try {
        pb.start();
      } catch (IOException e) {
        System.err.println("Could not start say.exe");
        e.printStackTrace();
      }

      ++wavName; //47
    }

  }

}
