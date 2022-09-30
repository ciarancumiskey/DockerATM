package cumiskey.ciaran.DockerATM.apiobjects;

import java.math.BigDecimal;
import java.util.Map;

public class WithdrawalResponse extends BasicResponse {
  private Map<Integer, Integer> withdrawnNoteCounts; //the key is the note denomination, the value is how many of them were withdrawn
  private BigDecimal remainingBalance;

  public WithdrawalResponse(final Map<Integer, Integer> withdrawnNoteCounts, final BigDecimal remainingBalance) {
    super(ATMStatus.SUCCESS.getValue(), "Withdrawal successful."); // "OK" HTTP status
    this.withdrawnNoteCounts = withdrawnNoteCounts;
    this.remainingBalance = remainingBalance;
  }


  public Map<Integer, Integer> getWithdrawnNoteCounts() {
    return withdrawnNoteCounts;
  }

  public void setWithdrawnNoteCounts(Map<Integer, Integer> withdrawnNoteCounts) {
    this.withdrawnNoteCounts = withdrawnNoteCounts;
  }

  public BigDecimal getRemainingBalance() {
    return remainingBalance;
  }

  public void setRemainingBalance(BigDecimal remainingBalance) {
    this.remainingBalance = remainingBalance;
  }
}
