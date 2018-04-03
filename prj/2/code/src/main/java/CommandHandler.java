import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Responsible for interpreting strings as commands to run operations on a DECPhoneCollection.
 *
 * A UI should still feed commands into here. Just use UI elements to trigger methods that get
 * information and spit strings into here. This exists separately from DECPhoneCollection so that a
 * project can use a DECPhoneCollection without needing to feed it commands through here.
 */
public class CommandHandler {

  /* Defines what a command needs */
  private interface Command {

    /* This method must exist in a command. Each command must override this method and place
     * necessary argument conversion and the method calls to DECPhoneCollection there */
    void runCommand(String[] args);
  }

  /* Collection of phones to preform operations on */
  private DECPhoneCollection decPhoneCollection;
  /* Message to the user about what they should do after the current command has run. This is
   * needed if the initiating command spans multiple iterations. */
  private String message;
  /* Stores the keys to run command methods on decPhoneCollections */
  private Map<String, Command> commands = new HashMap<>();
  /* If a command was run that requires multiple iterations, it locks all other commands and
   * takes control of the command class */
  private boolean lockCommands;
  /* Index the queue was at when the CommandHandler was locked (commands after this are new) */
  private int queueLockIndex;
  /* Commands that need to be run. (Needed for chained commands, especially after a lock) */
  private ArrayList<String> queuedCommands;
  /* String describing how to run commands */
  private final static String helpString = "How to run autoDEC commands:\n"
      + "A '-' precedes any main command AFTER the first main command. If you are not chaining "
      + "commands then just enter the characters for the command. These commands specify what "
      + "kind of operation will run\n"
      + "Anything after a main command (before the next main command if they are chained together)"
      + ", put any arguments for the main command separated by spaces.\n"
      + "Commands:\n"
      + "\th - Help (shows this message)"
      + "\tp - Play\n"
      + "\t\ta - Play again\n"
      + "\t\tn - Play next\n"
      + "\t\ts #x #y - Play surrounding x phones backwards and y phones forwards\n";

  /**
   * Creates a CommandHandler
   *
   * @param decPhoneCollection DECPhoneCollection to run operations on
   */
  public CommandHandler(DECPhoneCollection decPhoneCollection) {
    this.decPhoneCollection = decPhoneCollection;
    message = ""; // Start the message as blank
    queuedCommands = new ArrayList<>();
    queueLockIndex = 0;

    // Add the help command
    commands.put("h", args -> System.out.print(helpString));

    // Add the play command
    commands.put("p", args -> {
      switch (args[1]) { // Switch on the first argument after p
        case "a": // Play again
          decPhoneCollection.playCurrentPhone();
          break;
        case "n": // Play next
          decPhoneCollection.playNextPhone();
          break;
        case "s": // Play surrounding
          decPhoneCollection.playSurroundingPhones(Integer.parseInt(args[2]),
              Integer.parseInt(args[3]));
          break;
      }
    });
  }

  /**
   * Runs a command on CommandHandler
   *
   * @param command Syntax of the command to run
   */
  public void runCommand(String command) {
    // Split on the '-'s (allows command chaining) and add those commands to the queue
    Collections.addAll(queuedCommands, command.split("[\\-]"));

    boolean previousLockState = lockCommands;
    for (int i = queueLockIndex; i < queuedCommands.size(); ++i) {
      String[] args = queuedCommands.get(i).split("[ ]"); // Split on spaces
      try {
        commands.get(args[0]).runCommand(args);
      } catch (NullPointerException e) {
        System.out.println("Command " + args[0] + " not found. Try again.");
      }
      queuedCommands.remove(i); // After the command is run it can be removed
      if (lockCommands != previousLockState && lockCommands) {
        // If a command locks it, stop running new commands. Other commands need to unlock it
        queueLockIndex = queuedCommands.size(); // Start at end of array
        break;
      }
    }
  }

  /**
   * @return Value of the message from the last command
   */
  public String getMessage() {
    return message;
  }

  /*
   * Unlock this CommandHandler class
   */
  private void unlock() {
    lockCommands = false;
    queueLockIndex = 0;
  }

}
