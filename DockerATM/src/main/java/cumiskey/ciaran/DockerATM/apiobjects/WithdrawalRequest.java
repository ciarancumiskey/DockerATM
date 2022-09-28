package cumiskey.ciaran.DockerATM.apiobjects;

public class WithdrawalRequest {
  private String withdrawalAmount;
  private String accountNumber;
  private String accountPIN;

  public WithdrawalRequest(String accountNumber, String accountPIN, String withdrawalAmount) {
    this.accountNumber = accountNumber;
    this.accountPIN = accountPIN;
    this.withdrawalAmount = withdrawalAmount;
  }

  public String getWithdrawalAmount() {
    return withdrawalAmount;
  }

  public String getAccountNumber() {
    return accountNumber;
  }

  public void setAccountNumber(String accountNumber) {
    this.accountNumber = accountNumber;
  }

  public String getAccountPIN() {
    return accountPIN;
  }

  public void setAccountPIN(String accountPIN) {
    this.accountPIN = accountPIN;
  }

  public void setWithdrawalAmount(String withdrawalAmount) {
    this.withdrawalAmount = withdrawalAmount;
  }
}
