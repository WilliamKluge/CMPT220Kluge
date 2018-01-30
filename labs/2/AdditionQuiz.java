import java.util.Scanner;

/**
 * Program to generate three pseudo-random numbers and quiz the user on their sum
 * @author William Kluge
 *   Contact: klugewilliam@gmail.com
 */
public class AdditionQuiz {

  /**
   * Main method for the addition quiz program
   *
   * @param args Arguments are not utilized in this program
   */
  public static void main(String[] args) {
    // Make some pseudo-random numbers
    int number1 = (int) (System.currentTimeMillis() % 10);
    int number2 = (int) (System.currentTimeMillis() / 10 % 10);
    int number3 = (int) (System.currentTimeMillis() / 20 % 10);

    // Setup input
    Scanner input = new Scanner(System.in);

    // Prompt user
    System.out.format("What is %d + %d + %d? ", number1, number2, number3);

    // Get input
    int user_answer = input.nextInt();

    // Show user answer
    int answer = number1 + number2 + number3;

    System.out.format("%d + %d + %d is %d. You answered %d. This is " + (answer == user_answer),
        number1, number2, number3, answer, user_answer);

  }

}
