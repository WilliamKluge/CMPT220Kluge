/**
 * Program to
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
public class driver_lab113 {

  /**
   * Main method for lab
   *
   * @param args This program does not utilize arguments
   */
  public static void main(String[] args) {
    Account account = new Account();
    SavingsAccount savingsAccount = new SavingsAccount();
    CheckingAccount checkingAccount = new CheckingAccount(1, 2, 3);

    System.out.println(account.toString() + "\n" + savingsAccount.toString() + "\n"
        + checkingAccount.toString());

  }
}
