//package Lab97;

import java.util.Date;

/**
 * Class to hold and handle data on an account
 */
public class Account {

  /**
   * ID of the account
   */
  private int id = 0;
  /**
   * Balance of the account
   */
  private double balance = 0;
  /**
   * Current annual interest rate for the account
   */
  private double annualInterestRate = 0;
  /**
   * Date the account was created
   */
  private Date dateCreated;

  /**
   * Creates the default account
   */
  Account() {
    dateCreated = new Date();
  }

  /**
   * Creates ana account with a specified id and balance
   *
   * @param id ID for this account
   * @param balance Balance to start the account with
   */
  Account(int id, int balance) {

    this.id = id;
    this.balance = balance;
    dateCreated = new Date();

  }

  /**
   * Gets the ID associated with this account
   *
   * @return ID of the account
   */
  public int getId() {
    return id;
  }

  /**
   * Sets the ID of the account
   *
   * @param id ID to set for the account
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Gets the amount of money in the account
   *
   * @return Balance of the account
   */
  public double getBalance() {
    return balance;
  }

  /**
   * Sets the balance of the account
   *
   * @param balance Amount of money to set the account to
   */
  public void setBalance(double balance) {
    this.balance = balance;
  }

  /**
   * Gets the annual interest rate of the account
   *
   * @return Annual interest rate
   */
  public double getAnnualInterestRate() {
    return annualInterestRate;
  }

  /**
   * Sets the annual interest rate for the account
   *
   * @param annualInterestRate Rate to set
   */
  public void setAnnualInterestRate(double annualInterestRate) {
    this.annualInterestRate = annualInterestRate;
  }

  /**
   * Gets the date the account was created
   *
   * @return Date the account was creatd
   */
  public Date getDateCreated() {
    return dateCreated;
  }

  /**
   * Calculate the amount of interest calculated with the monthly rate
   *
   * @return Monthly interest rate
   */
  public double getMonthlyInterest() {

    return balance * ((annualInterestRate / 100) / 12);

  }

  /**
   * Take money out of the account
   *
   * @param amount Amount of money to take out
   */
  public void withdraw(double amount) {

    balance -= amount;

  }

  /**
   * Add money to the account
   *
   * @param amount Amount of money to add
   */
  public void deposit(double amount) {

    balance += amount;

  }

}
