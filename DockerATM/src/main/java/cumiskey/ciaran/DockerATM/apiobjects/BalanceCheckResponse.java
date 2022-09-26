package cumiskey.ciaran.DockerATM.apiobjects;

import org.springframework.http.HttpStatus;

public class BalanceCheckResponse extends BasicResponse {
  private int currentBalance;
  private int maximumWithdrawal;

  public BalanceCheckResponse(HttpStatus status) {
    super(status.value());
  }

  public BalanceCheckResponse(int currentBalance, int maximumWithdrawal) {
    super(200); // "OK" HTTP status
    this.currentBalance = currentBalance;
    this.maximumWithdrawal = maximumWithdrawal;
  }

  public int getCurrentBalance() {
    return currentBalance;
  }

  public void setCurrentBalance(int currentBalance) {
    this.currentBalance = currentBalance;
  }

  public int getMaximumWithdrawal() {
    return maximumWithdrawal;
  }

  public void setMaximumWithdrawal(int maximumWithdrawal) {
    this.maximumWithdrawal = maximumWithdrawal;
  }
}
