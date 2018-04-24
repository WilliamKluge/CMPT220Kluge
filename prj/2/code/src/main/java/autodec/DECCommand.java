package autodec;

import soundhandling.NoteRange;

/**
 * Holds the data of a command
 */
public class DECCommand {

  /* String format of a NoteRange */
  private NoteRange noteRange;
  /* Channel that this command takes place in */
  private int channel;
  /* Voice command to be used */
  private String voiceCommand;
  /* The actual phoneme commands that DECtalk will process */
  private StringBuilder commandBody;

  /**
   * Creates a DECCommand using the metadata associated with it
   *
   * @param noteRange Range of notes this command is responsible for
   * @param channel Channel that this command takes place in
   * @param voiceCommand DECtalk voice command associated with this command
   */
  public DECCommand(NoteRange noteRange, int channel, String voiceCommand) {
    this.noteRange = noteRange;
    this.channel = channel;
    this.voiceCommand = voiceCommand;
    commandBody = new StringBuilder();
  }

  /**
   * Adds text to the command. Do not append the '[' and ']', they are handled by getCommand()
   * @param text Text to append to the command
   */
  public void append(String text) {
    commandBody.append(text);
  }

  /**
   * @return A WAVE filename that describes the data of this command
   */
  public String createFileName() {
    return noteRange.toString() + "_" + channel + ".wav";
  }

  /**
   * @return DECtalk phone commands of this command formatted to be played by DECtalk
   */
  public String getCommand() {
    return "[" + commandBody.toString() + "]";
  }

  /**
   * @return All the data in this class as a string
   */
  @Override
  public String toString() {
    return noteRange.toString() + " " + String.format("%03d", channel) + " " + voiceCommand + " "
        + commandBody.toString();
  }
}
