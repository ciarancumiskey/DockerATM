package cumiskey.ciaran.DockerATM.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Entity
public class Customer {

  @Id
  @Column(name = "account_number")
  private String accountNumber; //needs to account for account numbers starting with 0

  @Column(name = "pin")
  private String pin; //needs to account for PINs starting with 0

  @Column(name = "balance")
  private BigDecimal balance;

  @Column(name = "overdraft")
  private BigDecimal overdraft;

  @OneToMany(mappedBy = "customer")
  private List<Transaction> transactions;

  public Customer() {
    //default constructor - do not use this! This is just to satisfy the requirement for a default no-args constructor.
    this.accountNumber = "000000000";
    this.pin = "0000";
    this.balance = BigDecimal.ZERO;
    this.overdraft = BigDecimal.ZERO;
  }

  public Customer(final String accountNumber) {
    this(accountNumber, "0000", BigDecimal.ZERO, BigDecimal.ZERO);
  }

  public Customer(final String accountNumber, final String pin, final BigDecimal openingBalance,
                  final BigDecimal overdraft){
    this.accountNumber = accountNumber;
    this.pin = pin;
    this.balance = openingBalance;
    this.overdraft = overdraft;
  }

  public String getAccountNumber() {
    return accountNumber;
  }

  public void setAccountNumber(String accountNumber) {
    this.accountNumber = accountNumber;
  }

  public String getPin() {
    return pin;
  }

  public void setPin(String pin) {
    this.pin = pin;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }

  public void setBalance(int balance) {
    this.balance = BigDecimal.valueOf(balance);
  }

  /**
   * Decreases the balance (and if applicable, the overdraft) by the requested withdrawal amount.
   * @param withdrawalAmount - the amount that the user wants to withdraw
   * @return true if the withdrawal was successful, false if not
   */
  public boolean withdraw(final BigDecimal withdrawalAmount) {
    double balanceValue = this.balance.doubleValue();
    final double overdraftValue = this.overdraft.doubleValue();
    final double negativeBalanceLimit = 0.0 - overdraftValue;
    double withdrawalValue = withdrawalAmount.doubleValue();
    if(balanceValue - withdrawalValue > negativeBalanceLimit) {
      final double newBalance = balanceValue - withdrawalValue;
      if(newBalance < 0) {
        //Check if the withdrawal exceeds the overdraft
        if(0 - newBalance < overdraftValue) {
          this.balance = BigDecimal.valueOf(newBalance);
        } else {
          return false;
        }
      }
    }
    if(withdrawalValue <= balanceValue + overdraftValue) {
      balanceValue -= withdrawalValue;
      this.balance = BigDecimal.valueOf(balanceValue);
      return true;
    }
    return false;
  }

  public boolean withdraw(final double withdrawalAmount) {
    return withdraw(BigDecimal.valueOf(withdrawalAmount).setScale(2, RoundingMode.CEILING));
  }

  public void deposit(final BigDecimal depositAmount) {
    this.balance = this.balance.add(depositAmount);
  }

  public BigDecimal getOverdraft() {
    return overdraft;
  }

  public double getWithdrawalLimit() {
    double withdrawalLimit = this.balance.doubleValue();
    withdrawalLimit += this.overdraft.doubleValue();
    return withdrawalLimit;
  }

  public void setOverdraft(BigDecimal overdraft) {
    this.overdraft = overdraft;
  }


  public List<Transaction> getTransactions() {
    return transactions;
  }

  public void setTransactions(List<Transaction> transactions) {
    this.transactions = transactions;
  }
}
