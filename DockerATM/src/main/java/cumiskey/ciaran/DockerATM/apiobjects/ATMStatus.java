package cumiskey.ciaran.DockerATM.apiobjects;

public enum ATMStatus {
  SUCCESS(1),
  ACCOUNT_NOT_FOUND(-1),
  INCORRECT_PIN(-2),
  INSUFFICIENT_FUNDS(-3),
  ATM_CANNOT_FULFIL_WITHDRAWAL(-4),
  INVALID_TRANSACTION(-5);

  private int value;

  ATMStatus(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }
}
