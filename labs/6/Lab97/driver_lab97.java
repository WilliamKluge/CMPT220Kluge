//package Lab97;

/**
 * Program to test the Account class
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
public class driver_lab97 {

  /**
   * Main method for lab 9.7
   *
   * @param args This program does not utilize arguments
   */
  public static void main(String[] args) {

    Account account = new Account(1122, 20000);
    account.setAnnualInterestRate(4.5);
    account.withdraw(2500);
    account.deposit(3000);
    System.out.format("Balance: %f\nMonthly Interest: %f\nDate Created: %s",
        account.getBalance(), account.getMonthlyInterest(), account.getDateCreated().toString());

  }
}
