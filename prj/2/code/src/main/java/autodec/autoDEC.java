package autodec;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import com.sun.media.sound.WaveFileWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Collection;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import midifilehandling.MIDIConverter;
import notes.DECNote;
import soundhandling.NoteRange;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Main class for autoDEC
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
  private final static int CONFIG_HIGHEST_KEY = 51 - 15;
  /* Lowest allowed piano key */
  private final static int CONFIG_LOWEST_KEY = 20; // DECtalk can't do tones lower than C2
  /* NoteRanges to use for separating notes */
  private final static ArrayList<NoteRange> ranges;

  // Adds values to ranges array
  static {
    String[] lowCommands = {"[:np]", "[:nh]"};
    ranges = new ArrayList<>();
    ranges.add(new NoteRange(1, 5, 0.5f, lowCommands));
    int autogenMax = 60;
    for (int i = 6; i < autogenMax; i += 5) {
      ranges.add(new NoteRange(i, i + 4 <= autogenMax ? i + 4 : autogenMax, 0.5f, lowCommands));
    }
    autogenMax = 108;
    String[] highCommands = {/*"[:nu]", "[:nb]", */"[:nk]"};
    for (int i = 6; i < autogenMax; i += 5) {
      ranges.add(NoteRange.createWithSpecificRange(i, i + 4 <= autogenMax ? i + 4 : autogenMax,
          5, 9, 0.5f, highCommands));
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
      File midi120BPM = new File(midi120BPMPath);
      try {
        if (!midi120BPM.exists()) {
          Files.copy(new File(inputFilePath).toPath(), midi120BPM.toPath(), REPLACE_EXISTING);
        }
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
      refractorTrack(track);
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
    if (!System.getProperty("os.name").toLowerCase().contains("win")) {
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
    ArrayList<AudioInputStream> audioInputs = new ArrayList<>();
    for (DECCommand command : commandList) {
      // Create a say.exe process to output a WAVE file of the DECtalk
      ProcessBuilder pb = new ProcessBuilder("dectalk\\say.exe",
          "-w", "generated\\" + command.createFileName(),
          "-pre", "\"[:phoneme on]\"",
          command.getCommand());
      pb.directory(new File("dectalk\\"));

      try {
        Process p = pb.start();
        p.waitFor();
      } catch (IOException e) {
        System.err.println("Could not run say.exe");
        e.printStackTrace();
      } catch (InterruptedException e) {
        System.err.println("Error waiting for process to finish");
        e.printStackTrace();
      }

//      try {
//        /*audioInputs.add(AudioSystem.getAudioInputStream(new File(*/lowerVolume(command)/*)))*/;
//      } catch (IOException | LineUnavailableException e) {
//        e.printStackTrace();
//      }
    }
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
      NoteRange range = new NoteRange(109, -1, 0f, "");

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
    NoteRange range = new NoteRange(CONFIG_HIGHEST_TONE + 1, CONFIG_LOWEST_TONE - 1, 0f, "");

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

  /*
   * Lowers the volume of the specified file
   *
   * @param path Path of the file to lower the volume for
   * @throws IOException If the audio file can not be read
   * @throws LineUnavailableException If the audio line cannot be obtained
   * @return Path to the file that was created
   */
  private static String lowerVolume(DECCommand command)
      throws IOException, LineUnavailableException {
    String path = "dectalk\\generated\\" + command.createFileName();

    AudioInputStream audioInputStream = null;
    try {
      audioInputStream = AudioSystem.getAudioInputStream(new File(path));
    } catch (UnsupportedAudioFileException e) {
      // This should really never happen, we always give it a WAVE file
      e.printStackTrace();
      return "";
    }

    Clip clip = AudioSystem.getClip();
    clip.open(audioInputStream);
    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
    float range = gainControl.getMaximum() - gainControl.getMinimum();
    float gain = (range * command.getNoteRange().getVolumeShift()) + gainControl.getMinimum();
    gainControl.setValue(gain);
//    gainControl.setValue(command.getNoteRange().getVolumeShift());
    clip.start();

    String newPath = path.substring(0, path.length() - 4) + "_controlled.wav";

    File output = new File(path);

//    OutputStream outStream = new FileOutputStream(path);

    // Get the header of the old wave file
//    byte[] buffer = new byte[44];
//    InputStream is = new FileInputStream(path);
//    is.read(buffer);
//    is.close();
//
//    outStream.write(buffer);
//    outStream.close();

    int response = AudioSystem.write(audioInputStream, Type.WAVE, output);
    audioInputStream.close();

    return path;
  }

}
