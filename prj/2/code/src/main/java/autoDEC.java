import MIDIFileHandling.MIDIConverter;
import Notes.DECNote;
import SoundHandling.NoteRange;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Main class for autoDEC
 */
public class autoDEC {

  /* Name commands to use for different tracks */
  private final static String[] VOICE_SETTINGS = new String[]{"[:np]", "[:nh]", "[:nf]", "[:nd]"};
  /* Maximum length of a pause before it gets broken up */
  public final static int MAX_WAIT_LENGTH = 16000;
  /* If the program should print the DECtalk commands */
  private final static boolean PRINT_DEC = true;
  /* Highest tone that the program will allow usage of */
  private final static int CONFIG_HIGHEST_TONE = 37;
  /* Lowest tone that the program will allow usage of */
  private final static int CONFIG_LOWEST_TONE = 1;
  /* NoteRanges to use for separating notes */
  private final static ArrayList<NoteRange> ranges;
  static {
    ranges = new ArrayList<>();
    ranges.add(new NoteRange(1, 5, "[:np]", "[:nh]"));
    for (int i = 6; i < 108; i += 5) {
      ranges.add(new NoteRange(i, i + 4 <= 108 ? i + 4 : 108, "[:np]", "[:nh]"));
    }
  }

  public static void main(String[] args) {

    if (args.length == 0) {
      throw new IllegalArgumentException("No filepath was given");
    }

    String inputFilePath = args[0];

    ArrayList<ArrayList<DECNote>> DECTracks = new ArrayList<>();

    if (inputFilePath.contains(".mid")) {
      DECTracks = new MIDIConverter(inputFilePath, ranges).getDECTracks();
    }

    // Scale notes and add pauses to all tracks
    for (ArrayList<DECNote> track : DECTracks) {
      refractorTrack(track);
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
      command.append("[");
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

    if (false/*!System.getProperty("os.name").contains("win")*/) {
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
          "-w", "generated\\" + command.substring(3, 4) + "t" + wavName + ".wav",
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
    for (int i = 0; i < DECTrack.size(); ++i) {
      long iStart = DECTrack.get(i).getStartAsMillis();
      long lastEnd = (i == 0) ? 0 : DECTrack.get(i - 1).getEndAsMillis();
      long pauseTime = iStart - lastEnd - 1;

      if (pauseTime > 1) {
        // If the difference in their times is larger than 1 (minimum separator of notes)
        int j = 0;
        while (pauseTime >= MAX_WAIT_LENGTH) {
          DECTrack.add(i,
              new DECNote(MAX_WAIT_LENGTH * j, MAX_WAIT_LENGTH * (j + 1) - 1));
          pauseTime -= MAX_WAIT_LENGTH;
          ++j;
        }
        if (pauseTime > 0) {
          DECTrack.add(i,
              new DECNote(MAX_WAIT_LENGTH * j, MAX_WAIT_LENGTH * j + pauseTime));
          ++j;
        }
//        DECTrack.add(i, new DECNote(lastEnd + 1, iStart - 1));
        i += j; // Skip the pauses that was just added
        // Loop ends with i == index of the note a pause was just added for, increment step moves
        // i to the next note that needs a pause
      }
    }
  }

  /**
   * Adjusts notes so that the highest does not exceed 37 (highest tone in DECtalk)
   *
   * @param DECTrack Track to adjust notes on
   */
  private static void refractorTrack(ArrayList<DECNote> DECTrack) {
    int highestTone = CONFIG_LOWEST_TONE - 1; // Start at 1 below min DECtalk tone
    int lowestTone = CONFIG_HIGHEST_TONE + 1; // Start at 1 above max DECtalk tone

    for (DECNote note : DECTrack) {
      if (note.getPitch() > highestTone) {
        highestTone = note.getPitch();
      }
      if (note.getPitch() < lowestTone) {
        lowestTone = note.getPitch();
      }
    }

    if (highestTone > CONFIG_HIGHEST_TONE || lowestTone < CONFIG_LOWEST_TONE) {
      for (DECNote note : DECTrack) {
        int scaledNote = scaleNote(note.getPitch(), lowestTone, highestTone);
        note.setPitch(scaledNote);
      }
    }
  }

  /*
   * Scales a given note to ensure the entire track stays within 1 to 37 while keeping note's
   * original sound.
   *
   * @param x Value to scale
   * @param trackMin Minimum value of the track
   * @param trackMax Maximum value of the track
   * @return x scaled to be within 1 to 37 and still in its place of the track
   */
  private static int scaleNote(int x, int trackMin, int trackMax) {
    return (int) ((CONFIG_HIGHEST_TONE - CONFIG_LOWEST_TONE * 1.0) * (x - trackMin)
        / (trackMax - trackMin) + 1);
  }

  /*
   * @param channelID ID of the channel to get a voice command for
   * @return Voice command associated with the channel
   */
  private static String getVoiceCommand(int channelID) {
    return VOICE_SETTINGS[channelID % VOICE_SETTINGS.length];
  }

}
