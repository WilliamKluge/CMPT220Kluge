public class CheckingAccount extends Account {
  /* Maximum amount the account can be overdrawn */
  private int overdraftLimit;

  /**
   * Creates a CheckingAccount
   *
   * @param overdraftLimit Maximum amount the account can be overdrawn
   */
  public CheckingAccount(int overdraftLimit, int id, int balance) {
    super(id, balance);
    this.overdraftLimit = overdraftLimit;
  }

  /**
   * Overrides withdraw with a check to make sure more money is not being taken out than the account

   * lets it.
   * @param amount Amount of money to take out
   */
  @Override
  public void withdraw(double amount) {
    if (getBalance() - amount < overdraftLimit) {
      System.out.println("Cannot withdraw more than " + overdraftLimit);
    } else {
      super.withdraw(amount);
    }
  }
}
