package cumiskey.ciaran.DockerATM.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Customer {

  @Id
  private long accountNumber;

  private String pin; //needs to account for PINs starting with 0
  private int balance;
  private int overdraft;

  public Customer() {
    //default constructor - do not use this! This is just to keep
    this.accountNumber = 0;
    this.pin = "0000";
    this.balance = 0;
    this.overdraft = 0;
  }

  public Customer(final long accountNumber) {
    this(accountNumber, "0000", 0, 0);
  }

  public Customer(final long accountNumber, final String pin, final int openingBalance, final int overdraft){
    this.accountNumber = accountNumber;
    this.pin = pin;
    this.balance = openingBalance;
    this.overdraft = overdraft;
  }

  public Long getAccountNumber() {
    return accountNumber;
  }

  public void setAccountNumber(Long accountNumber) {
    this.accountNumber = accountNumber;
  }

  public String getPin() {
    return pin;
  }

  public void setPin(String pin) {
    this.pin = pin;
  }

  public int getBalance() {
    return balance;
  }

  public void setBalance(int balance) {
    this.balance = balance;
  }

  public int getOverdraft() {
    return overdraft;
  }

  public void setOverdraft(int overdraft) {
    this.overdraft = overdraft;
  }

  /**
   * Decreases the balance (and if applicable, the overdraft) by the requested withdrawal amount.
   * @param withdrawalAmount - the amount that the user wants to withdraw
   * @return true if the withdrawal was successful, false if not
   */
  protected boolean withdrawFunds(int withdrawalAmount) {
    final int negativeBalanceLimit = 0 - this.overdraft;
    if(this.balance - withdrawalAmount > negativeBalanceLimit) {
      final int newBalance = this.balance - withdrawalAmount;
      if(newBalance < 0) {
        //Subtract the original balance from the withdrawal amount,
        //as that's the amount that will be needed from the overdraft.
        withdrawalAmount -= this.balance;
        if(withdrawalAmount < this.overdraft) {
          this.overdraft -= withdrawalAmount;
        } else {
          return false;
        }
      }
      setBalance(newBalance);
    }
    return true;
  }
}
