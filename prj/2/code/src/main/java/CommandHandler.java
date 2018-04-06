import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
  /* String describing how to run commands TODO just get this in a man page or something*/
  private final static String helpString = "How to run autoDEC commands:\n"
      + "A '-' precedes any main command AFTER the first main command. If you are not chaining "
      + "commands then just enter the characters for the command. These commands specify what "
      + "kind of operation will run\n"
      + "Anything after a main command (before the next main command if they are chained together)"
      + ", put any arguments for the main command separated by spaces.\n"
      + "Commands:\n"
      + "\th - Help (shows this message)"
      + "\tc - Correct, the last played phone matches the audio, move to next phone\n"
      + "\ti {correct phone} - Incorrect, the last played phone does not match the audio, use"
      + "{correct phone} instead\n"
      + "\td - Delete the current phone\n"
      + "\ts - Squish\n"
      + "\tgoto #x - Go to the xth phone"
      + "\t\tp - Previous, squish the current phone's time into the previous one\n"
      + "\t\tn - Next, squish the next phones time into this one\n"
      + "\tp - Play\n"
      + "\t\ta - Play again\n"
      + "\t\tn - Play next\n"
      + "\t\ts #x #y - Play surrounding x phones backwards and y phones forwards\n"
      + "\tf - File\n"
      + "\t\ts - Save\n"
      + "\t\tw - Write (DECtalk file)\n"
      + "\t\tn {name} - New (output file) with the name {name}\n"
      + "\t\tl {path} - Load save file at {path}\n"
      + "\t\tc - Close output\n"
      + "\t\tp - Print DECtalk information to file\n"
      + "\tt - Tone\n"
      + "\t\tb - Balance this phone's tone with it's neighbors (only changes this tone)\n"
      + "\t\t\ta - Balance all tones with their neighbors\n"
      + "\t\tr"
      + "\t\t\t#x - Replace Replace this tone with x tone number\n"
      + "\t\t\ta #x #y Replace all tone numbers x with tone number y\n"
      + "\t\ts #x - Shift this tone by x (can be positive or negative)\n";

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

    // TODO make these commands impossible to run if there are not enough arguments

    // Correct
    commands.put("c", args -> {
      message = "Phone set as correct.";
      decPhoneCollection.goToNextPhone();
    });

    // Incorrect
    commands.put("i", args -> {
      decPhoneCollection.setCurrentPhonePronunciation(args[1]);
      decPhoneCollection.goToNextPhone();
    });

    // Delete current phone
    commands.put("d", args -> {
      decPhoneCollection.removeCurrentPhone();
      decPhoneCollection.goToNextPhone();
    });

    // Squish
    commands.put("s", args -> {
      switch (args[1]) {
        case "p":
          decPhoneCollection.squishWithPrevious();
          break;
        case "n":
          decPhoneCollection.squishWithNext();
          break;
      }
    });

    // Add the goto command
    commands
        .put("goto", args -> decPhoneCollection.setCurrentPhoneIndex(Integer.parseInt(args[1])));

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

    // Add file operations
    commands.put("f", args -> {
      switch (args[1]) {
        case "s":
          decPhoneCollection.saveProgress();
          break;
        case "w":
          decPhoneCollection.writeDECtalkFile();
          break;
        case "n":
          message = "creating new output file";
          decPhoneCollection.createNewOutputFile(args.length > 2 ? args[2] : "");
          break;
        case "l":
          message = "Loading information from save file.";
          decPhoneCollection.loadProgress(args[2]);
          break;
        case "c":
          message = "Closing output file";
          decPhoneCollection.closeOutput();
          break;
        case "p":
          message = "Displaying DECtalk code.";
          decPhoneCollection.printDECToConsole();
          break;
      }
    });

    // Add tone operations
    commands.put("t", args -> {
      switch (args[1]) {
        case "b":
          boolean balanceAll = false;
          if (args.length > 2) {
            balanceAll = args[2].equals("a");
          }
          decPhoneCollection.toneNumberBalance(balanceAll);
          break;
        case "r":
          if (args.length > 2 && args[2].equals("a")) {
            decPhoneCollection.toneReplace(Integer.parseInt(args[3]), Integer.parseInt(args[4]));
          } else {
            decPhoneCollection.toneReplace(Integer.parseInt(args[3]));
          }
          break;
        case "s":
          boolean shiftAll = false;
          if (args.length > 2) {
            shiftAll = args[2].equals("a");
          }
          decPhoneCollection.toneShift(shiftAll, Integer.parseInt(args[3]));
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
      message = "";
      try {
        commands.get(args[0]).runCommand(args);
      } catch (NullPointerException e) {
        System.out.println("Command not found. Try again.");
      }
      System.out.println(message); // Print the message of the last run command TODO add more
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
