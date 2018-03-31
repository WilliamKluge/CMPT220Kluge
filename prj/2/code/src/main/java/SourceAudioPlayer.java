import WavFileHandling.WavFile;
import WavFileHandling.WavFileException;
import edu.cmu.sphinx.util.TimeFrame;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Created to play back sections of the source audio file
 */
public class SourceAudioPlayer {

  /**
   * Audio clip that the program is working with
   */
  public Clip clip;
  /* Where this class should output information */
  private PrintStream out = null;
  /* Frames per millisecond of the source file */
  private long framesPerMillisecond;
  private DataLine.Info info;
  AudioFormat format;
  private long bytesPerSecond;
  File sourceFile;
  /* Number of bytes to skip to get to where the data section of a wav file starts */
  private final static int WAV_HEADER_LENGTH = 44;

  /**
   * Constructor.
   */
  public SourceAudioPlayer(File sourceFile)
      throws IOException, LineUnavailableException, UnsupportedAudioFileException {
    out = System.out;

    this.sourceFile = sourceFile;

    WavFile wavFile;
    try {
      wavFile = WavFile.openWavFile(sourceFile);

      bytesPerSecond = wavFile.getSampleRate() * wavFile.getNumChannels() *
          (wavFile.getValidBits() / 8);
    } catch (IOException e) {
      System.err.println("An error occurred while opening " + sourceFile.getAbsolutePath());
    } catch (WavFileException e) {
      System.err.print("An error occured while preforming WAV file operations on "
          + sourceFile.getAbsolutePath());
    }

    AudioInputStream stream = AudioSystem.getAudioInputStream(sourceFile);
    format = stream.getFormat();
    info = new DataLine.Info(Clip.class, format);
    clip = (Clip) AudioSystem.getLine(info);
    clip.open(stream);

    framesPerMillisecond = clip.getFrameLength() / (clip.getMicrosecondLength() / 1000);

  }

  /**
   * Plays the section of audio that a phone occurs in
   *
   * @param dectalkPhone DECtalkPhone to get a time frame to play
   */
  public void playPhoneSection(DECtalkPhone dectalkPhone)
      throws LineUnavailableException, IOException {
    // Gets the timeframe of the phone
    TimeFrame phoneTimeFrame = dectalkPhone.getTimeFrame();

    // Prepares the clip to be played
    Clip phoneClip = (Clip) AudioSystem.getLine(info); // New clip
    FileInputStream fileInputStream = new FileInputStream(sourceFile);
    fileInputStream.skip(WAV_HEADER_LENGTH); // Skip header
    fileInputStream.skip(millisToBytes(phoneTimeFrame.getStart())); // Skip previous data
    byte[] clipBytes = new byte[(int) millisToBytes(phoneTimeFrame.length())];
    fileInputStream.read(clipBytes); // Read clipBytes.length bytes of data into array
    phoneClip.open(format, clipBytes, 0, clipBytes.length); // Open the array as audio

    out.println("Playing for " + phoneTimeFrame.length() * framesPerMillisecond
        + " frames | " + phoneTimeFrame.length() + " milliseconds");

    // Set the phone's clip, then play it
    dectalkPhone.setClip(phoneClip);
    dectalkPhone.playClip();

    out.println();

  }

  /*
   * Prints the info of what frame's of the source file's audio are being taken to play
   *
   * @param phoneTimeFrame Time frame of the phone being played
   */
  private void printFrameSeekInfo(TimeFrame phoneTimeFrame) {

    int seekTo = (int) (framesPerMillisecond * phoneTimeFrame.getStart());
    int endFrame = (int) (framesPerMillisecond * phoneTimeFrame.getEnd());

    out.println("Seeking to " + seekTo + " frame | " + phoneTimeFrame.getStart() + " milliseconds");
    out.println("End is " + endFrame + " frame | " + phoneTimeFrame.getEnd() + " milliseconds");

  }

  /*
   * Converts a time in milliseconds into bytes using the calculated bytes per second of audio for
   * the source audio file
   *
   * @param millis Millisecond time to convert
   * @return millis in bytes
   */
  private long millisToBytes(long millis) {
    return (long) Math.ceil((millis / 1000.0) * bytesPerSecond);
  }

}
