import java.util.Scanner;

/**
 * Program to play rock paper scissors
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
public class driver_lab317 {

  /**
   * Main function for the driver_lab317 program
   *
   * @param args Arguments are not implemented in this program
   */
  public static void main(String[] args) {
    // Setup input
    Scanner input = new Scanner(System.in);

    // Generate computer answer
    int computer_answer = (int) (System.currentTimeMillis() % 2);

    // Get user answer
    System.out.print("Rock (0), Paper (1), or Scissors (2)? ");
    int user_answer = input.nextInt();

    while (user_answer < 0 ^ user_answer > 2) {
      System.out.print("Entered number is not 0-2, please enter a number in the specified range");
      user_answer = input.nextInt();
    }

    // Check and display winner
    String message;

    if (computer_answer == user_answer) {
      message = "It is a draw.";
    } else if (computer_answer > user_answer && !(user_answer == 0 && computer_answer == 2)) {
      message = "The computer wins.";
    } else {
      message = "User wins.";
    }

    System.out.format("The computer is %s, you are %s. %s",
        getAnswer(computer_answer), getAnswer(user_answer), message);

  }

  /**
   * Determines if the answer was Rock, Paper, or Scissors
   *
   * @param answer An integer representing rock, paper, or scissors
   * @return The string form of answer
   */
  private static String getAnswer(int answer) {
    String result = "";
    switch (answer) {
      case 0: // Rock
        result = "Rock";
        break;
      case 1: // Paper
        result = "Paper";
        break;
      case 2: // Scissors
        result = "Scissors";
        break;
      default: // Error
        System.out.println("Something happened to the memory of this program cause this should be "
            + "impossible");
        break;
    }

    return result;
  }

}
