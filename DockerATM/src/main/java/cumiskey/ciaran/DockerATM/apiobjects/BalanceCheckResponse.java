package cumiskey.ciaran.DockerATM.apiobjects;

import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

public class BalanceCheckResponse extends BasicResponse {
  private BigDecimal currentBalance;
  private BigDecimal maximumWithdrawal;

  public BalanceCheckResponse(HttpStatus status) {
    super(status.value());
  }

  public BalanceCheckResponse(BigDecimal currentBalance, BigDecimal maximumWithdrawal) {
    super(ATMStatus.SUCCESS.getValue(), "Balance checked successfully."); // "OK" HTTP status
    this.currentBalance = currentBalance;
    this.maximumWithdrawal = maximumWithdrawal;
  }

  public BigDecimal getCurrentBalance() {
    return currentBalance;
  }

  public void setCurrentBalance(BigDecimal currentBalance) {
    this.currentBalance = currentBalance;
  }

  public BigDecimal getMaximumWithdrawal() {
    return maximumWithdrawal;
  }

  public void setMaximumWithdrawal(BigDecimal maximumWithdrawal) {
    this.maximumWithdrawal = maximumWithdrawal;
  }
}
