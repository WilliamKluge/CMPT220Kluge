package autodec;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.nio.file.CopyOption;
import java.nio.file.Files;
import midifilehandling.MIDIConverter;
import notes.DECNote;
import soundhandling.NoteRange;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Main class for autodec.autoDEC
 */
public class autoDEC {

  /* Maximum length of a pause before it gets broken up */
  public final static int MAX_WAIT_LENGTH = 16000;
  /* If the program should print the DECtalk commands */
  private final static boolean PRINT_DEC = true;
  /* Highest tone that the program will allow usage of */
  private final static int CONFIG_HIGHEST_TONE = 37;
  /* Lowest tone that the program will allow usage of */
  private final static int CONFIG_LOWEST_TONE = 1;
  /* Shift the piano keys of notes to fit in the configured range.
   * Scaling by keys is probably going to make a lot of different notes sound a lot more similar. */
  private final static boolean SHIFT_PIANO_KEYS = false;
  /* Highest allowed piano key. DECtalk can't do tones higher than C5, lower to sound less whinny */
  private final static int CONFIG_HIGHEST_KEY = 51 - 10;
  /* Lowest allowed piano key */
  private final static int CONFIG_LOWEST_KEY = 15; // DECtalk can't do tones lower than C2
  /* NoteRanges to use for separating notes */
  private final static ArrayList<NoteRange> ranges;

  // Adds values to ranges array
  static {
    String[] lowCommands = {"[:np]", "[:nh]"};
    ranges = new ArrayList<>();
    ranges.add(new NoteRange(1, 5, lowCommands));
    int autogenMax = 60;
    for (int i = 6; i < autogenMax; i += 5) {
      ranges.add(new NoteRange(i, i + 4 <= autogenMax ? i + 4 : autogenMax, lowCommands));
    }
    autogenMax = 108;
    String[] highCommands = {"[:nu]", "[:nb]", "[:nk]"};
    for (int i = 6; i < autogenMax; i += 5) {
      ranges.add(NoteRange.createWithSpecificRange(i, i + 4 <= autogenMax ? i + 4 : autogenMax,
          5, 9, highCommands));
    }
  }

  /**
   * Main method for autoDEC program
   *
   * @param args Expected argument is a path to a file to convert. Currently only MIDI files are
   * supported.
   */
  public static void main(String[] args) {

    if (args.length == 0) {
      // If there were no arguments
      throw new IllegalArgumentException("No filepath was given");
    }
    /* Path of the target file */
    String inputFilePath = args[0];
    /* Tracks of notes separated in a format that is playable by DECtalk */
    ArrayList<ArrayList<DECNote>> DECTracks;
    /* List of commands to be fed to DECtalk */
    ArrayList<DECCommand> commandList;

    if (inputFilePath.contains(".mid")) {
      String midi120BPMPath = inputFilePath.replace(".mid", "_120BPM.mid");
      try {
        Files.copy(new File(inputFilePath).toPath(), new File(midi120BPMPath).toPath(),
            REPLACE_EXISTING);
      } catch (IOException e) {
        System.err.println("Could not copy file to 120BPM version.");
        e.printStackTrace();
        return;
      }

      // Create DECtalk-style tracks from a MIDI file TODO be more specific
      ProcessBuilder setMIDItempo = new ProcessBuilder("java", "-jar",
          "libs/MidiTempoConverter.jar", midi120BPMPath, String.valueOf(MIDIConverter.BPM));

      try {
        Process p = setMIDItempo.start();
        p.waitFor();
      } catch (IOException e) {
        System.err.println("Could not run say.exe");
        e.printStackTrace();
      } catch (InterruptedException e) {
        System.err.println("Error waiting for the MIDI tempo conversion process to finish");
        e.printStackTrace();
      }

      DECTracks = new MIDIConverter(midi120BPMPath, ranges).getDECTracks();
    } else {
      // The file is of an unsupported file type
      System.err.println("The provided file was not of an expected format. Please consult the "
          + "autoDEC's documentation for a list of supported file types.");
      return;
    }

    // After this point every operation will be identical for any file type

    if (SHIFT_PIANO_KEYS) {
      // If the program is configured to fit all piano keys into a range
      shiftPianoKeys(DECTracks);
    }

    for (ArrayList<DECNote> track : DECTracks) {
      // Scale notes and add pauses to all tracks
//      refractorTrack(track); TODO fix error making low values where they shouldnt be
      addPauses(track);
    }

    System.out.println("Generating DECtalk Commands");
    commandList = new ArrayList<>();
    for (ArrayList<DECNote> decTrack : DECTracks) {
      int track = -1;

      // Find what track this note is on by checking the phone's until we find an actual sound
      int i = 0;
      while (track == -1) {
        // While we haven't found a sound yet
        DECNote note = decTrack.get(i);
        if (!note.getPhone().equals("_")) {
          // If the note is not a pause (because pause's do not have channels)
          track = note.getChannel();
        }
        ++i;
      }

      // Get the track's first note so that we can get the universal data from it
      DECNote firstNote = decTrack.get(0);

      DECCommand decCommand = new DECCommand(firstNote.getRange(), firstNote.getChannel(),
          firstNote.getVoiceCommand());

      for (DECNote phone : decTrack) {
        // For every phone in the DECtrack
        decCommand.append(phone.toString());
      }

      commandList.add(decCommand);

      if (PRINT_DEC) {
        // Prints the DECtalk command
        System.out.println(decCommand.toString());
      }
    }

    ///// WAVE file output. Requires a Windows OS (I know, ew) and DECtalk's speak.exe /////
    if (!System.getProperty("os.name").contains("win")) {
      // If the OS is not windows
      System.out.println("DECtalk is not compatible with any non-windows system. Cannot output "
          + "WAVE files.");
      return;
    }

    System.out.println("Deleting generated files from previous iteration");
    File[] files = new File("dectalk\\generated").listFiles();
    if (files != null) {
      for (File f : files) {
        // For every file in the generation directory
        f.delete();
      }
    }

    System.out.println("Generating wav files");

    // Make sure the executable file exists
    File exe = new File("dectalk\\say.exe");
    if (!exe.exists()) {
      System.err.println("Exe doesn't exist, cannot output WAVE files without speak.exe");
      return;
    }

    System.out.println("Exporting WAVE files ");
    for (DECCommand command : commandList) {
      // Create a say.exe process to output a WAVE file of the DECtalk
      ProcessBuilder pb = new ProcessBuilder("dectalk\\say.exe",
          "-w", "generated\\" + command.createFileName(),
          "-pre", "\"[:phoneme on]\"",
          command.getCommand());
      pb.directory(new File("dectalk\\"));

      try {
        pb.start();
      } catch (IOException e) {
        System.err.println("Could not run say.exe");
        e.printStackTrace();
      }
    }

//    File outputFile = new File("dectalk\\generated");
//    for (int i = 0; i < files.length - 1; ++i, files = outputFile.listFiles()) {
//      // If the first note identifier is the same for both files
//      File firstFile = files[i];
//      File secondFile = files[i + 1];
//      while (firstFile.getName().substring(0, 3).equals(secondFile.getName().substring(0, 3))) {
//        try {
//          firstFile = WAVEMixer.mixWAVEFiles(files[i], files[i + 1]);
//          System.out.println("Deleting " + files[i].getName() + " and " + secondFile.getName());
////          files[i].delete(); // files not actually deleting
////          secondFile.delete();
////          i -= 2; // Deleted two files
//        } catch (Exception e) {
//          e.printStackTrace();
//        }
//        ++i;
//        secondFile = files[++i];
//      }
//    }
  }

  /*
   * Adds all the pauses between phones
   *
   * @param DECTrack Track to work with
   */
  private static void addPauses(ArrayList<DECNote> DECTrack) {
    NoteRange trackRange = DECTrack.get(0).getRange();
    int channel = DECTrack.get(0).getChannel();

    for (int i = 0; i < DECTrack.size(); ++i) {
      long iStart = DECTrack.get(i).getStartAsMillis();
      long lastEnd = (i == 0) ? 0 : DECTrack.get(i - 1).getEndAsMillis();
      long pauseTime = iStart - lastEnd - 1;

      if (pauseTime > 1) {
        // If the difference in their times is larger than 1 (minimum separator of notes)
        int j = 0;
        while (pauseTime >= MAX_WAIT_LENGTH) {
          DECNote pause = new DECNote(MAX_WAIT_LENGTH * j, MAX_WAIT_LENGTH * (j + 1) - 1,
              trackRange);
          pause.setChannel(channel);
          DECTrack.add(i, pause);
          pauseTime -= MAX_WAIT_LENGTH;
          ++j;
        }
        if (pauseTime > 0) {
          DECNote pause = new DECNote(MAX_WAIT_LENGTH * j, MAX_WAIT_LENGTH * j + pauseTime,
              trackRange);
          pause.setChannel(channel);
          DECTrack.add(i, pause);
          ++j;
        }
        i += j; // Skip the pauses that was just added
        // Loop ends with i == index of the note a pause was just added for, increment step moves
        // i to the next note that needs a pause
      }
    }
  }

  /*
   * Shifts the piano keys of the given DECTrack to fit within the configured range
   *
   * @param DECTrack Track to shift keys on
   *
   * Probably could squish this and refractorTrack into one method
   */
  private static void shiftPianoKeys(ArrayList<ArrayList<DECNote>> DECTracks) {
    int highestKey = -1; // Start at non-existent piano key (anything is higher)
    int lowestKey = 109; // Start at 1 above max keys on piano (all is lower)

    for (ArrayList<DECNote> DECTrack : DECTracks) {
      NoteRange range = new NoteRange(109, -1, "");

      range.updateRange(DECTrack, true); // Update the track using the piano key

      if (range.getLowestNote() < lowestKey) {
        lowestKey = range.getLowestNote();
      }

      if (range.getHighestNote() > highestKey) {
        highestKey = range.getHighestNote();
      }
    }

    if (highestKey > CONFIG_HIGHEST_KEY || lowestKey < CONFIG_LOWEST_KEY) {
      // If something needs to be shifted
      for (ArrayList<DECNote> DECTrack : DECTracks) {
        // Iterate through tracks
        for (DECNote note : DECTrack) {
          // Iterate through notes
          int scaledNote = scaleNote(CONFIG_HIGHEST_KEY, CONFIG_LOWEST_KEY,
              note.getPianoKey(), lowestKey, highestKey);
          note.setPianoKey(scaledNote);
        }
      }
    }
  }

  /*
   * Adjusts notes so that the highest does not exceed 37 (highest tone in DECtalk)
   *
   * @param DECTrack Track to adjust notes on
   */
  private static void refractorTrack(ArrayList<DECNote> DECTrack) {
    NoteRange range = new NoteRange(CONFIG_HIGHEST_TONE + 1, CONFIG_LOWEST_TONE - 1, "");

    range.updateRange(DECTrack, false); // Update the track using the note's tone number

    if (range.getHighestNote() > CONFIG_HIGHEST_TONE
        || range.getLowestNote() < CONFIG_LOWEST_TONE) {
      // If the range of the track is outside of the configured amount
      for (DECNote note : DECTrack) {
        int scaledNote = scaleNote(CONFIG_HIGHEST_TONE, CONFIG_LOWEST_TONE,
            note.getPitch(), range.getLowestNote(), range.getLowestNote());
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
  private static int scaleNote(int highest, int lowest, int x, int trackMin, int trackMax) {
//    int scaled = (int) ((highest - lowest * 1.0) * (x - trackMin)
//        / (trackMax - trackMin) + 1);
    double scaled = (x * 1.0 - trackMin) / (trackMax - trackMin);
    scaled = Math.ceil(scaled * (highest - lowest));
    return (int) scaled;
  }

}
