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
  /* If the program should print the DECtalk commands */
  private final static boolean PRINT_DEC = true;

  public static void main(String[] args) {

    if (args.length == 0) {
      throw new IllegalArgumentException("No filepath was given");
    }

    String inputFilePath = args[0];

    ArrayList<ArrayList<DECNote>> DECTracks = new ArrayList<>();

    if (inputFilePath.contains(".mid")) {
      DECTracks = new MIDIConverter(inputFilePath).getDECTracks();
    }

    // Add pauses to all tracks
    for (ArrayList<DECNote> track : DECTracks) {
      addPauses(track);
    }

    System.out.println("Generating wav files");
    ArrayList<String> commandList = new ArrayList<>();
    for (ArrayList<DECNote> decTrack : DECTracks) {
      int track = -1;

      // Find what track this note is on
      int i = 0;
      while (track == -1) {
        if (!decTrack.get(i).getPhone().equals("_")) {
          track = decTrack.get(i).getChannel();
        }
        ++i;
      }

      // Build the command
      StringBuilder command = new StringBuilder(getVoiceCommand(track));
      for (DECNote phone : decTrack) {
        command.append(phone.toString());
      }
      command.append("]");

      commandList.add(command.toString());

      if (PRINT_DEC) {
        // Prints the DECtalk command if the program is configured to do so
        System.out.println(command.toString());
      }
    }

    ///// WAVE file output. Requires a Windows OS (I know, ew) and DECtalk's speak.exe /////

    if (!System.getProperty("os.name").equals("win")) {
      System.out.println("DECtalk is not compatible with any non-windows system. Cannot output"
          + "WAVE files.");
      return;
    }

    // Make sure the executable file exists
    File exe = new File("dectalk\\say.exe");
    if (!exe.exists()) {
      System.out.println("Exe doesn't exist, cannot output WAVE files without speak.exe");
      return;
    }

    int wavName = 0;
    for (String command : commandList) {
      ProcessBuilder pb = new ProcessBuilder("dectalk\\say.exe",
          "-w", "generated\\" + command.substring(0, 5) + "t" + wavName + ".wav",
          "-pre",
          "\"[:phoneme on]\"",
          command);
      pb.directory(new File("dectalk\\"));
      try {
        pb.start();
      } catch (IOException e) {
        System.err.println("Could not start say.exe");
        e.printStackTrace();
      }

      ++wavName; // New wavName for every track
    }

  }

  /*
   * Adds all the pauses between phones
   *
   * @param DECTrack Track to work with
   */
  private static void addPauses(ArrayList<DECNote> DECTrack) {
    if (DECTrack.size() < 2) {
      // Nothing to add pauses with
      return;
    }

    for (int i = 0; i < DECTrack.size() - 1; ++i) {
      long iStart = DECTrack.get(i).getStartAsMillis();
      long lastEnd = (i == 0) ? 0 : DECTrack.get(i - 1).getEndAsMillis();

      if (Math.abs(iStart - lastEnd) > 1) {
        // If the difference in their times is larger than 1 (minimum separator of notes)
        DECTrack.add(i, new DECNote(lastEnd + 1, iStart - 1));
        ++i; // Skip the pause that was just added
      }
    }
  }

  /*
   * @param channelID ID of the channel to get a voice command for
   * @return Voice command associated with the channel
   */
  private static String getVoiceCommand(int channelID) {
    return VOICE_SETTINGS[channelID % VOICE_SETTINGS.length];
  }

}
