package cumiskey.ciaran.DockerATM.apiobjects;

public class BalanceCheckRequest {
  private String accountNumber;
  private int accountPIN;

  public BalanceCheckRequest(String accountNumber, int accountPIN) {
    this.accountNumber = accountNumber;
    this.accountPIN = accountPIN;
  }

  public String getAccountNumber() {
    return accountNumber;
  }

  public void setAccountNumber(String accountNumber) {
    this.accountNumber = accountNumber;
  }

  public int getAccountPIN() {
    return accountPIN;
  }

  public void setAccountPIN(int accountPIN) {
    this.accountPIN = accountPIN;
  }
}
