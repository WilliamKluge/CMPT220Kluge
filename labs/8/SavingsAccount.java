public class SavingsAccount extends Account {

  /**
   * Makes sure the account can not have more money taken out than can be put in
   *
   * @param amount Amount of money to take out
   */
  @Override
  public void withdraw(double amount) {
    if (amount > getBalance()) {
      System.out.println("Cannot withdraw more funds than are in the account");
    } else {
      super.withdraw(amount);
    }
  }
}
