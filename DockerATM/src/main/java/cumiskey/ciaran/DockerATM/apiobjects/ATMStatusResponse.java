package cumiskey.ciaran.DockerATM.apiobjects;

import cumiskey.ciaran.DockerATM.AutomatedTellerMachine;

import java.util.HashMap;
import java.util.Map;

public class ATMStatusResponse extends BasicResponse {
  private final int fundsAvailable;

  private final Map<Integer, Integer> noteDenominationsAvailable;

  public ATMStatusResponse() {
    super(ATMStatus.SUCCESS.getValue());
    this.fundsAvailable = 0;
    this.noteDenominationsAvailable = new HashMap<>();
  }

  public ATMStatusResponse(final AutomatedTellerMachine atm) {
    super(ATMStatus.SUCCESS.getValue());
    this.fundsAvailable = atm.getCashAvailable();
    this.noteDenominationsAvailable = atm.getNotesAvailable();
  }

  public int getFundsAvailable() {
    return fundsAvailable;
  }

  public Map<Integer, Integer> getNoteDenominationsAvailable() {
    return noteDenominationsAvailable;
  }
}
