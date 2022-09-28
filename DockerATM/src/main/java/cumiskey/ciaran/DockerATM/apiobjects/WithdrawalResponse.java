package cumiskey.ciaran.DockerATM.apiobjects;

import java.util.Map;

public class WithdrawalResponse extends BasicResponse {
  private Map<Integer, Integer> withdrawnNoteCounts; //the key is the note denomination, the value is how many of them were withdrawn
  private int remainingBalance;

  public WithdrawalResponse(final Map<Integer, Integer> withdrawnNoteCounts, final int remainingBalance) {
    super(ATMStatus.SUCCESS.getStatusId()); // "OK" HTTP status
    this.withdrawnNoteCounts = withdrawnNoteCounts;
    this.remainingBalance = remainingBalance;
  }


  public Map<Integer, Integer> getWithdrawnNoteCounts() {
    return withdrawnNoteCounts;
  }

  public void setWithdrawnNoteCounts(Map<Integer, Integer> withdrawnNoteCounts) {
    this.withdrawnNoteCounts = withdrawnNoteCounts;
  }

  public int getRemainingBalance() {
    return remainingBalance;
  }

  public void setRemainingBalance(int remainingBalance) {
    this.remainingBalance = remainingBalance;
  }
}
