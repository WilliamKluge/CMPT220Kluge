import WavFileHandling.WavFile;
import WavFileHandling.WavFileException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.sound.sampled.Clip;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;

/**
 * Created to play back sections of the source audio file
 */
public class SourceAudioPlayer implements BasicPlayerListener {

  /**
   * Used to control the settings and position of the audio playback
   */
  public BasicController control;
  /* Where this class should output information */
  private PrintStream out = null;
  /* Bytes per second of the source file */
  private long bytesPerSecond;

  /**
   * Contructor.
   */
  public SourceAudioPlayer(File sourceFile)
      throws BasicPlayerException, FileNotFoundException, JavaLayerException {
    out = System.out;
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

    // Instantiate BasicPlayer.
    BasicPlayer player = new BasicPlayer();
    // BasicPlayer is a BasicController.
    control = (BasicController) player;
    Clip audioClip = new
    // Register BasicPlayerTest to BasicPlayerListener events.
    // It means that this object will be notified on BasicPlayer
    // events such as : opened(...), progress(...), stateUpdated(...)
    player.addBasicPlayerListener(this);

    // Open file, or URL or Stream (shoutcast, icecast) to play.
    control.open(sourceFile);

    control.play();
    control.pause();

  }

  /**
   * Plays the section of audio that a phone occurs in
   *
   * @param dectalkPhone DECtalkPhone to get a time frame to play
   */
  public void playPhoneSection(DECtalkPhone dectalkPhone)
      throws BasicPlayerException, InterruptedException {

    long seekTo = millisToBytes(dectalkPhone.getTimeFrame().getStart());

    out.println("Seeking to " + seekTo
        + " bytes | " + dectalkPhone.getTimeFrame().getStart() + " milliseconds");

    control.seek(seekTo);

    control.resume();

    // Set Volume (0 to 1.0).
    control.setGain(0.85);
    // Set Pan (-1.0 to 1.0).
    control.setPan(0.0);

    out.println("Playing for " + millisToBytes(dectalkPhone.getTimeFrame().length())
        + " bytes | " + dectalkPhone.getTimeFrame().length() + " milliseconds");

//    TimeUnit.MILLISECONDS.sleep(dectalkPhone.getTimeFrame().length());

    out.println();

  }

  /**
   * Plays the full audio file
   *
   * @param filename Name of the file to play TODO specialize this for this class and its purpose
   */
  public void play(String filename) {
    // Instantiate BasicPlayer.
    BasicPlayer player = new BasicPlayer();
    // BasicPlayer is a BasicController.
    control = (BasicController) player;
    // Register BasicPlayerTest to BasicPlayerListener events.
    // It means that this object will be notified on BasicPlayer
    // events such as : opened(...), progress(...), stateUpdated(...)
    player.addBasicPlayerListener(this);

    try {
      // Open file, or URL or Stream (shoutcast, icecast) to play.
      control.open(new File(filename));

      // control.open(new URL("http://yourshoutcastserver.com:8000"));

      // Start playback in a thread.
      control.play();

      // If you want to pause/resume/pause the played file then
      // write a Swing player and just call control.pause(),
      // control.resume() or control.stop().
      // Use control.seek(bytesToSkip) to seek file
      // (i.e. fast forward and rewind). seek feature will
      // work only if underlying JavaSound SPI implements
      // skip(...). True for MP3SPI and SUN SPI's
      // (WAVE, AU, AIFF).

      // Set Volume (0 to 1.0).
      control.setGain(0.85);
      // Set Pan (-1.0 to 1.0).
      control.setPan(0.0);
    } catch (BasicPlayerException e) {
      e.printStackTrace();
    }
  }

  /**
   * Open callback, stream is ready to play.
   *
   * properties map includes audio format dependant features such as bitrate, duration, frequency,
   * channels, number of frames, vbr flag, ...
   *
   * @param stream could be File, URL or InputStream
   * @param properties audio stream properties.
   */
  public void opened(Object stream, Map properties) {
    // Pay attention to properties. It's useful to get duration,
    // bitrate, channels, even tag such as ID3v2.
    display("opened : " + properties.toString());
  }

  /**
   * Progress callback while playing.
   *
   * This method is called severals time per seconds while playing. properties map includes audio
   * format features such as instant bitrate, microseconds position, current frame number, ...
   *
   * @param bytesread from encoded stream.
   * @param microseconds elapsed (<b>reseted after a seek !</b>).
   * @param pcmdata PCM samples.
   * @param properties audio stream parameters.
   */
  public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties) {
    // Pay attention to properties. It depends on underlying JavaSound SPI
    // MP3SPI provides mp3.equalizer.
    display("progress : " + properties.toString());
  }

  /**
   * Notification callback for basicplayer events such as opened, eom ...
   */
  public void stateUpdated(BasicPlayerEvent event) {
    // Notification of BasicPlayer states (opened, playing, end of media, ...)
    display("stateUpdated : " + event.toString());
  }

  /**
   * A handle to the BasicPlayer, plugins may control the player through the controller (play, stop,
   * ...)
   *
   * @param controller : a handle to the player
   */
  public void setController(BasicController controller) {
    display("setController : " + controller);
  }

  public void display(String msg) {
    if (out != null) {
      out.println(msg);
    }
  }

  /**
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
