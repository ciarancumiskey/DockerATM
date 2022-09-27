package cumiskey.ciaran.DockerATM.apiobjects;

public class BalanceCheckRequest {
  private String accountNumber;
  private String accountPIN;

  public BalanceCheckRequest(String accountNumber, String accountPIN) {
    this.accountNumber = accountNumber;
    this.accountPIN = accountPIN;
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
}
